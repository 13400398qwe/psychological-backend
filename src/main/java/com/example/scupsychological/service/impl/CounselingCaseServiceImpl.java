package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.enums.Role;
import com.example.scupsychological.common.exception.BaseException;
import com.example.scupsychological.mapper.*;
import com.example.scupsychological.pojo.dto.CaseAssistantQueryDto;
import com.example.scupsychological.pojo.dto.CaseScheduleDto;
import com.example.scupsychological.pojo.entity.*;
import com.example.scupsychological.pojo.vo.*;
import com.example.scupsychological.service.CounselingCaseService;
import com.example.scupsychological.service.NotificationService;
import com.example.scupsychological.utils.WordGeneratorUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CounselingCaseServiceImpl implements CounselingCaseService {
    private final CounselingCasesMapper caseMapper;
    private final CounselingSessionsMapper sessionMapper;
    private final InitialVisitRecordsMapper recordMapper;
    private final UsersMapper userMapper; // 用于查询学生信息
    private final ScheduleSlotsMapper slotMapper;
    private final InitialVisitApplicationsMapper applicationMapper;
    private final ObjectMapper objectMapper;
    private final WordGeneratorUtil wordGeneratorUtil;
    private final NotificationService notificationService;
    private static final int COUNSELING_SESSION_DURATION_MINUTES = 60;

    @Override
    public List<PendingCaseVO> findPendingCases() {
        // 直接调用自定义的 Mapper 方法即可
        return caseMapper.selectPendingCases();
    }


    @Override
    @Transactional // 关键：整个操作是原子性的
    public CounselingCases scheduleNewCase(CaseScheduleDto scheduleDto, Long userId) {
        // 1. 校验前置条件
        InitialVisitRecords record = recordMapper.selectById(scheduleDto.getInitialVisitRecordId());
        if (record == null || !"ARRANGE_COUNSELING".equals(record.getConclusion())) {
            throw new BaseException("该初访记录无效或无需安排咨询");
        }
        long counselorId = scheduleDto.getCounselorId();
        // 3. 从数据库中查询这个用户实体
        Users counselor = userMapper.selectById(counselorId);

// 4. 【核心】进行统一的、安全的校验
//    这个判断同时处理了“用户不存在”和“用户角色不正确”两种情况
        if (counselor == null || counselor.getRole() != Role.COUNSELOR) {
            throw new BaseException("指派的咨询师无效或不存在");
        }
        // ... (此处还应检查该初访记录是否已被处理过，防止重复创建个案) ...
        long StudentId = applicationMapper.selectById(record.getApplicationId()).getStudentId();
        List<LocalDateTime> expectedStartTimes = new ArrayList<>();
        LocalDateTime firstSessionTime = scheduleDto.getFirstSessionStartTime();
        for (int i = 0; i < scheduleDto.getTotalSessions(); i++) {
            expectedStartTimes.add(firstSessionTime.plusWeeks(i));
        }

        // 2. 一次性查询出这些时间点，该咨询师名下所有【可能相关】的号源
        List<ScheduleSlots> potentialSlots = slotMapper.selectList(
                new QueryWrapper<ScheduleSlots>()
                        .eq("staff_id", scheduleDto.getCounselorId())
                        .in("start_time", expectedStartTimes)
        );

        // 3. 【预检查】
        // a. 检查查询出的号源数量是否足够
        if (potentialSlots.size() < scheduleDto.getTotalSessions()) {
            throw new BaseException("排期失败：该咨询师在未来部分周期的该时间点没有排班。");
        }
        // b. 检查所有号源的状态是否都为 "AVAILABLE"
        boolean allAvailable = potentialSlots.stream().allMatch(slot -> "AVAILABLE".equals(slot.getStatus()));
        if (!allAvailable) {
            throw new BaseException("排期失败：该咨询师在未来部分周期的该时间点已被预约。");
        }

        // 4. 【批量锁定】
        // a. 提取所有需要被锁定的号源ID
        List<Long> slotIdsToBook = potentialSlots.stream().map(ScheduleSlots::getId).toList();
        // b. 执行原子更新，尝试锁定所有这些号源
        int affectedRows = slotMapper.atomicallyBookSlots(slotIdsToBook);
        // c. 校验是否全部锁定成功
        if (affectedRows != scheduleDto.getTotalSessions()) {
            // 这是一个并发冲突的信号，意味着在检查和锁定之间，有号源被别人抢占了
            throw new BaseException("排期失败，部分时间段在您操作时已被占用，请重试。");
        }

        // --- 号源已全部成功锁定，现在可以安全地创建业务记录了 ---

        // 5. 创建并保存“咨询个案”主记录
        CounselingCases newCase = new CounselingCases();
        newCase.setStudentId(StudentId);
        newCase.setCounselorId(counselorId);
        newCase.setAssistantId(userId);
        newCase.setStatus("IN_PROGRESS");
        newCase.setInitialVisitRecordId(scheduleDto.getInitialVisitRecordId());
        newCase.setTotalSessions(scheduleDto.getTotalSessions());
        newCase.setReportFinalizedAt(null);
        newCase.setCreatedAt(LocalDateTime.now());
        newCase.setUpdatedAt(LocalDateTime.now());
        newCase.setIsDeleted(false);
        // ... (设置 newCase 的字段) ...
        caseMapper.insert(newCase);

        // 6. 将号源按时间排序，然后创建8条单次咨询记录并关联号源ID
        Map<LocalDateTime, ScheduleSlots> timeToSlotMap = potentialSlots.stream()
                .collect(Collectors.toMap(ScheduleSlots::getStartTime, Function.identity()));

        List<CounselingSessions> sessionsToCreate = new ArrayList<>();
        for (int i = 0; i < expectedStartTimes.size(); i++) {
            LocalDateTime scheduledTime = expectedStartTimes.get(i);
            ScheduleSlots correspondingSlot = timeToSlotMap.get(scheduledTime);

            CounselingSessions session = new CounselingSessions();
            session.setCaseId(newCase.getId());
            session.setSessionNumber(i + 1);
            session.setScheduleSlotId(correspondingSlot.getId()); // 关键：关联号源ID
            session.setStartTime(correspondingSlot.getStartTime());
            session.setEndTime(correspondingSlot.getEndTime());
            session.setLocation(correspondingSlot.getLocation());
            session.setStatus("PENDING");
            sessionsToCreate.add(session);
        }

        // 7. 批量插入所有 sessions
        sessionMapper.insertBatch(sessionsToCreate);

        // ... (发送通知等后续逻辑) ...
//        notificationService.sendAppointmentApprovedSmsToStudent(student,counselor);
        return newCase;
    }

    @Override
    public byte[] exportReportAsWord(Long counselorId, Long caseId) {
        // 1. 获取包含所有关联信息的个案详情
        CounselorCaseDetailVO caseDetail = this.getCaseDetails(counselorId, caseId);
        if (caseDetail == null || caseDetail.getReportContent() == null) {
            throw new BaseException("未找到可导出的结案报告");
        }

        // 2. 准备传入模板的数据模型
        Map<String, Object> dataModel;
        try {
            // a. 首先，将报告内容 (reportContent) 这个 JSON 字符串解析回一个 Map
            dataModel = objectMapper.readValue(caseDetail.getReportContent(), new TypeReference<HashMap<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("报告内容解析失败，无法生成文档", e);
        }

        // b. 然后，将其他重要的、非报告内容的信息也加入到这个 Map 中
        dataModel.put("studentName", caseDetail.getStudentName());
        dataModel.put("studentUsername", caseDetail.getStudentUsername());
        // dataModel.put("studentGender", caseDetail.getStudentGender()); // 假设VO中有这些字段
        // dataModel.put("studentCollege", caseDetail.getStudentCollege());
        dataModel.put("studentPhone", caseDetail.getStudentPhone());
        dataModel.put("problemType", caseDetail.getProblemType());
        dataModel.put("totalSessions", caseDetail.getTotalSessions());
        // ... 添加任何其他您需要在Word文档中显示的字段 ...

        // 3. 调用 Word 生成工具类，传入【完整的 Map 对象】来创建文档
        try {
            return wordGeneratorUtil.createWord(dataModel);
        } catch (IOException e) {
            throw new RuntimeException("生成Word文档时发生IO错误", e);
        }
    }

    @Override
    public Page<CaseAssistantListVO> listMyCases(Long assistantId, CaseAssistantQueryDto queryDto) {
        // 1. 创建分页对象
        Page<CaseAssistantListVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());

        // 2. 调用我们即将实现的、新的自定义 Mapper 方法
        caseMapper.selectMyCasesForAssistant(page, assistantId, queryDto);

        return page;
    }
    @Override
    public Page<CaseReportContentVO> getCaseReportContent(Long userId, Long pageNum, Long pageSize){
        Page<CaseReportContentVO> page = new Page<>(pageNum, pageSize);
        return caseMapper.getCaseReportContent(page,userId);
    }

    private CounselorCaseDetailVO getCaseDetails(Long counselorId, Long caseId) {
        // 1. 调用自定义 Mapper 方法，通过一次 JOIN 查询获取个案的核心信息
        CounselorCaseDetailVO caseDetailVO = caseMapper.selectCaseDetailForCounselor(caseId,counselorId);

        // 2. 检查个案是否存在
        if (caseDetailVO == null) {
            throw new BaseException("个案不存在");
        }

        // 3. 【安全校验】确认个案是否属于当前登录的咨询师
        //    为了进行这个校验，我们需要查询原始的实体记录
        CounselingCases originalCase = caseMapper.selectById(caseId);
        if (originalCase == null || !originalCase.getCounselorId().equals(counselorId)) {
            throw new AccessDeniedException("无权查看不属于您的个案");
        }

        // 4. 查询该个案下所有已安排的单次咨询记录
        List<CounselingSessions> sessionEntities = sessionMapper.selectList(
                new QueryWrapper<CounselingSessions>().eq("case_id", caseId).orderByAsc("session_number")
        );

        // 5. 将实体列表 (Entity List) 转换为视图对象列表 (VO List)
        List<CounselingSessionVO> sessionVOs = sessionEntities.stream().map(entity -> {
            CounselingSessionVO vo = new CounselingSessionVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());

        // 6. 将单次咨询列表设置到最终的详情 VO 中
        caseDetailVO.setSessions(sessionVOs);

        return caseDetailVO;
    }

}
