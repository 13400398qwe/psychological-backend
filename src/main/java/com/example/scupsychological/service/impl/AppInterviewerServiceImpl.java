package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.mapper.InitialVisitApplicationsMapper;
import com.example.scupsychological.mapper.ScheduleSlotsMapper;
import com.example.scupsychological.mapper.UsersMapper;
import com.example.scupsychological.pojo.entity.InitialVisitApplications;
import com.example.scupsychological.pojo.entity.ScheduleSlots;
import com.example.scupsychological.pojo.entity.Users;
import com.example.scupsychological.pojo.vo.ApplicationInterviewerVO;
import com.example.scupsychological.service.AppInterviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppInterviewerServiceImpl implements AppInterviewerService {
    private final InitialVisitApplicationsMapper applicationsMapper;
    private final UsersMapper usersMapper;
    private final ScheduleSlotsMapper slotMapper;
    // 注入用于查询问卷提交记录的 Service 或 Mapper
    // private final QuestionnaireSubmissionService submissionService;

    @Override
    public Page<ApplicationInterviewerVO> listAssignedToMe(long pageNum,long pageSize,Long interviewerId) {
        // 1. 创建 MyBatis-Plus 的分页对象，用于查询主表
        Page<InitialVisitApplications> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件，只查询属于当前用户的申请，并按创建时间倒序
        QueryWrapper<InitialVisitApplications> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "APPROVED");
        wrapper.eq("assigned_interviewer_id", interviewerId);
        wrapper.orderByDesc("created_at");

        // 3. 执行分页查询，得到 Page<InitialVisitApplication>
        applicationsMapper.selectPage(page, wrapper);

        // --- 数据转换与组装 ---

        // 4. 创建一个用于最终返回的 Page<ApplicationListVO> 对象，并复制分页信息
        Page<ApplicationInterviewerVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<InitialVisitApplications> records = page.getRecords();
        if (records.isEmpty()) {
            return voPage; // 如果当前页没有数据，直接返回
        }

        // 5. 【核心】避免 N+1 查询：
        // a. 提取所有需要查询的关联ID (初访员ID和号源ID)
        Set<Long> studentIds = records.stream()
                .map(InitialVisitApplications::getStudentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> slotIds = records.stream()
                .map(InitialVisitApplications::getScheduleSlotId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // b. 一次性批量查询出所有相关的用户信息和号源信息
        Map<Long, Users> userMap;
        if (!studentIds.isEmpty()) {
            userMap = usersMapper.selectBatchIds(studentIds).stream()
                    .collect(Collectors.toMap(Users::getId, Function.identity()));
        } else {
            userMap = Collections.emptyMap();
        }

        Map<Long, ScheduleSlots> slotMap;
        if (!slotIds.isEmpty()) {
            slotMap = slotMapper.selectBatchIds(slotIds).stream()
                    .collect(Collectors.toMap(ScheduleSlots::getId, Function.identity()));
        } else {
            slotMap = Collections.emptyMap();
        }

        // c. 遍历原始实体列表，在内存中进行数据组装
        List<ApplicationInterviewerVO> voRecords = records.stream().map(application -> {
            ApplicationInterviewerVO vo = new ApplicationInterviewerVO();
            BeanUtils.copyProperties(application, vo);
            vo.setApplicationId(application.getId());
            // 设置学生姓名
            if (application.getAssignedInterviewerId() != null) {
                Users student = userMap.get(application.getStudentId());
                if (student != null) {
                    vo.setStudentName(student.getName());
                    vo.setStudentUsername(student.getUsername());
                    vo.setStudentPhone(student.getPhone());
                }
            }

            // 设置预约时间
            if (application.getScheduleSlotId() != null) {
                ScheduleSlots slot = slotMap.get(application.getScheduleSlotId());
                if (slot != null) {
                    vo.setScheduledTime(slot.getStartTime());
                }
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voRecords);
        return voPage;

    }

    @Override
    public ApplicationInterviewerVO getAssignedDetail(Long interviewerId, Long applicationId) {
        // 1. 调用自定义的 Mapper 方法，获取包含基本信息的详情
        //    这个查询的 WHERE 条件中必须同时包含 applicationId 和 interviewerId，确保权限安全
        ApplicationInterviewerVO detailVO = applicationsMapper.selectApplicationDetailForInterviewer(applicationId, interviewerId);

        // 2. 安全校验：如果查询结果为空，说明该任务不存在或不属于当前初访员
        if (detailVO == null) {
            throw new AccessDeniedException("无法访问该申请，可能不存在或未指派给您");
        }

        // 3. 补充问卷信息：根据 applicationId 查询关联的问卷提交记录
        // QuestionnaireSubmissionVO submissionVO = submissionService.getSubmissionByApplicationId(applicationId);
        // detailVO.setSubmission(submissionVO);

        return detailVO;
    }
}
