package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.enums.Role;
import com.example.scupsychological.common.exception.BaseException;
import com.example.scupsychological.mapper.InitialVisitApplicationsMapper;
import com.example.scupsychological.mapper.QuestionsMapper;
import com.example.scupsychological.mapper.ScheduleSlotsMapper;
import com.example.scupsychological.mapper.UsersMapper;
import com.example.scupsychological.pojo.dto.*;
import com.example.scupsychological.pojo.entity.InitialVisitApplications;
import com.example.scupsychological.pojo.entity.Questions;
import com.example.scupsychological.pojo.entity.ScheduleSlots;
import com.example.scupsychological.pojo.entity.Users;
import com.example.scupsychological.pojo.vo.ApplicationDetailVO;
import com.example.scupsychological.pojo.vo.ApplicationListVO;
import com.example.scupsychological.pojo.vo.EvaluationResult;
import com.example.scupsychological.pojo.vo.QuestionnaireEvaluationResult;
import com.example.scupsychological.service.InitialVisitApplicationService;
import com.example.scupsychological.service.NotificationService;
import com.example.scupsychological.service.QuestionnaireService;
import com.example.scupsychological.service.ScheduleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitialVisitApplicationServiceImpl implements InitialVisitApplicationService {

    private final QuestionnaireService questionnaireService;
    private final InitialVisitApplicationsMapper applicationsMapper;
    private final UsersMapper usersMapper;
    private final ScheduleSlotsMapper scheduleSlotsMapper;
    private final NotificationService notificationService;
    private final QuestionsMapper questionsMapper;
    private final ScheduleSlotsMapper slotMapper;
    private final ScheduleService scheduleService;
    // ... 其他 Mapper 和 Service

    @Transactional
    @Override
    public void createApplication(Long studentId, StudentApplicationCreateDto createDto) {
        // 1. 创建并保存【待指派】的申请记录
        InitialVisitApplications application = new InitialVisitApplications();
        application.setStudentId(studentId);
        application.setRequestedTime(LocalDateTime.of(createDto.getRequestedDate(), createDto.getRequestedTime()));
        application.setStatus("SUBMITTED");
        application.setQuestionnaireContent(createQuestionnaireSnapshot(createDto.getAnswers()));
        application.setScheduleSlotId(createDto.getScheduleSlotId());
        application.setAssignedInterviewerId(createDto.getAssignedInterviewerId());
        // 注意：此时 schedule_slot_id 和 assigned_interviewer_id 都是 NULL
        applicationsMapper.insert(application);

        // 2. 执行问卷评估...
        EvaluationResult evalResult = questionnaireService.evaluate(createDto.getAnswers());
        log.info("评估结果：evalResult{}", evalResult);
        // 3. 创建问卷提交记录...
        // ... 设置 submission 的字段 ...
        // 4. 更新申请记录的紧急状态
        application.setIsUrgent(evalResult.isUrgent());
        application.setCalculatedScore(evalResult.getScore());
        applicationsMapper.updateById(application);
        //将号源头设为BOOKED
        if (!scheduleService.bookSlot(createDto.getScheduleSlotId())) {
            throw new BaseException("手速慢了一点，预约失败");
        }
    }

    private String createQuestionnaireSnapshot(@NotNull(message = "问卷答案不能为空") Map<Long, Long> answers) {
        List<Long> questionIds = new ArrayList<>(answers.keySet());
        Map<Long, String> questionTextMap = questionsMapper.selectBatchIds(questionIds)
                .stream()
                .collect(Collectors.toMap(Questions::getId, Questions::getQuestionText));
        List<Map<String, Object>> snapshotList = answers.entrySet().stream().map(entry -> {
            Map<String, Object> snapshotEntry = new HashMap<>();
            snapshotEntry.put("questionId", entry.getKey());
            snapshotEntry.put("questionText", questionTextMap.getOrDefault(entry.getKey(), "题目已删除"));
            snapshotEntry.put("studentAnswer", entry.getValue());
            return snapshotEntry;
        }).collect(Collectors.toList());
        try {
            return new ObjectMapper().writeValueAsString(snapshotList);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    @Override
    @Transactional
    public ApplicationDetailVO reviewApplication(Long id, ApplicationReviewDto reviewDto) {
        // 1. 查找申请记录并进行基础校验
        InitialVisitApplications application = applicationsMapper.selectById(id);
        if (application == null) {
            throw new BaseException("申请记录不存在");
        }
        if (!"SUBMITTED".equals(application.getStatus())) {
            throw new BaseException("该申请已处理，请勿重复操作");
        }

        String decision = reviewDto.getStatus().toUpperCase();

        if ("APPROVED".equals(decision)) {
            // --- 处理“批准”逻辑 ---
//            Long interviewerId = reviewDto.getAssignedInterviewerId();
//            if (interviewerId == null&& application.getAssignedInterviewerId() == null) {
//                throw new BaseException("批准申请时必须指派一名初访员");
//            }
//
//            // 校验被指派的是否是合法的初访员
//            Users interviewer = usersMapper.selectById(interviewerId);
//            if (interviewer == null || interviewer.getRole() != Role.VISITOR) {
//                throw new BaseException("指派的初访员无效或不存在");
//            }

            // 更新申请状态和指派的初访员ID
            application.setStatus("APPROVED");

        } else if ("REJECTED".equals(decision)) {
            // --- 处理“拒绝”逻辑 ---
            application.setStatus("REJECTED");
            // 可以在一个备注字段中记录拒绝原因
            // application.setRejectionReason(reviewDto.getRejectionReason());

            // 关键：如果申请被拒绝，必须释放被占用的时间段
            if (application.getScheduleSlotId() != null) {
                int affectedRows = scheduleSlotsMapper.atomicallyCancelBooking(application.getScheduleSlotId());
                if (affectedRows == 0) {
                    // 记录一个警告日志，因为这可能意味着数据状态不一致，但对于用户操作来说，拒绝已经完成了
                    log.warn("拒绝申请(id={})时，尝试释放号源(id={})失败，其状态并非'BOOKED'。", id, application.getScheduleSlotId());
                }
            }
        } else {
            throw new BaseException("无效的审核状态，请提交 'APPROVED' 或 'REJECTED'");
        }

        // 3. 将更新保存到数据库
        applicationsMapper.updateById(application);
        //如果审核通过发送短信
        if ("APPROVED".equals(decision)) {
            notificationService.sendAppointmentApprovedSmsToStudent(usersMapper.selectById(application.getStudentId()), usersMapper.selectById(application.getAssignedInterviewerId()));
        }

        // 4. 查询并返回更新后的完整视图对象
        return buildDetailVO(application);
    }

    //分页查询所有初访问申请记录
    @Override
    public Page<ApplicationListVO> listAllApplications(ApplicationAdminQueryDto queryDto) {
        // 1. 根据前端传来的分页参数，创建 MyBatis-Plus 的 Page 对象
        Page<InitialVisitApplications> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        QueryWrapper<InitialVisitApplications> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDto.getStudentName()), "student_name", queryDto.getStudentName());
        wrapper.like(StringUtils.hasText(queryDto.getStudentUserName()), "student_user_name", queryDto.getStudentUserName());
        wrapper.like(StringUtils.hasText(queryDto.getInterviewerName()), "interviewer_name", queryDto.getInterviewerName());
        wrapper.like(StringUtils.hasText(queryDto.getStatus()), "status", queryDto.getStatus());
        wrapper.like(queryDto.getIsUrgent() != null, "is_urgent", queryDto.getIsUrgent());
        wrapper.orderByDesc("created_at");
        applicationsMapper.selectPage(page, wrapper);
        // 4. 创建一个用于最终返回的 Page<ApplicationListVO> 对象，并复制分页信息
        Page<ApplicationListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<InitialVisitApplications> records = page.getRecords();

        if (records.isEmpty()) {
            return voPage; // 如果当前页没有数据，直接返回
        }

        // 5. 【核心】避免 N+1 查询：
        // a. 提取所有需要查询的关联ID (初访员ID和号源ID)
        Set<Long> interviewerIds = records.stream()
                .map(InitialVisitApplications::getAssignedInterviewerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> slotIds = records.stream()
                .map(InitialVisitApplications::getScheduleSlotId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // b. 一次性批量查询出所有相关的用户信息和号源信息
        Map<Long, Users> userMap;
        if (!interviewerIds.isEmpty()) {
            userMap = usersMapper.selectBatchIds(interviewerIds).stream()
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
        List<ApplicationListVO> voRecords = records.stream().map(application -> {
            ApplicationListVO vo = new ApplicationListVO();
            BeanUtils.copyProperties(application, vo);
            // 设置初访员姓名
            if (application.getAssignedInterviewerId() != null) {
                Users interviewer = userMap.get(application.getAssignedInterviewerId());
                if (interviewer != null) {
                    vo.setInterviewerName(interviewer.getName());
                }
            }
            // 设置学生姓名
            if (application.getStudentId() != null) {
                Users student = usersMapper.selectById(application.getStudentId());
                if (student != null) {
                    vo.setStudentName(student.getName());
                    vo.setStudentUsername(student.getUsername());
                }
            }
            // 设置预约时间
            if (application.getScheduleSlotId() != null) {
                ScheduleSlots slot = slotMap.get(application.getScheduleSlotId());
                if (slot != null) {
                    vo.setRequestedTime(slot.getStartTime());
                }
            }
            vo.setSubmittedTime(application.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voRecords);
        return voPage;
    }

    @Override
    public ApplicationDetailVO createApplicationForStudent(AdminApplicationCreateDto createDto) {
        // 1. 验证学生是否存在
        Users student = usersMapper.selectById(createDto.getStudentId());
        if (student == null || !"STUDENT".equals(student.getRole().name())) {
            throw new BaseException("指定的学生ID无效");
        }

        // 2. 验证号源是否存在且可用，并用原子操作锁定它
        if (scheduleService.bookSlot(createDto.getScheduleSlotId())) {
            throw new BaseException("该时间段不可用或已被预约");
        }
        ScheduleSlots slot = scheduleSlotsMapper.selectById(createDto.getScheduleSlotId());

        // 3. 创建一个新的 InitialVisitApplication 实例
        InitialVisitApplications application = new InitialVisitApplications();
        application.setStudentId(createDto.getStudentId());
        application.setScheduleSlotId(createDto.getScheduleSlotId());

        // 4. 管理员创建的预约，状态直接设为 "APPROVED"，并指派初访员
        application.setStatus("APPROVED");
        application.setAssignedInterviewerId(slot.getStaffId());

        // 5. 保存到数据库
        applicationsMapper.insert(application);
        notificationService.sendAppointmentApprovedSmsToStudent(student, usersMapper.selectById(slot.getStaffId()));
        // 6. 组装详细的VO并返回
        return buildDetailVO(application);
    }

    @Override
    @Transactional
    public void cancelApplication(Long currentUserId, Long applicationId) {
        // 1. 查找申请记录
        InitialVisitApplications application = applicationsMapper.selectById(applicationId);
        if (application == null) {
            throw new BaseException("申请记录不存在");
        }

        // 2. 【安全校验】确认该申请属于当前登录用户
        if (!application.getStudentId().equals(currentUserId)) {
            throw new AccessDeniedException("无权操作他人的申请");
        }

        // 3. 【业务规则校验】
        String status = application.getStatus();
        if ("SUBMITTED".equals(status)) {
            // 审核前，可以直接撤销
        } else if ("APPROVED".equals(status)) {
            // 审核后，检查预约时间是否在24小时之外
            ScheduleSlots slot = scheduleSlotsMapper.selectById(application.getScheduleSlotId());
            if (slot != null && LocalDateTime.now().plusHours(24).isAfter(slot.getStartTime())) {
                throw new BaseException("预约开始前24小时内无法在线撤销，请联系中心管理员");
            }
        } else {
            // 其他状态（如已完成、已取消）不允许操作
            throw new BaseException("当前状态下无法撤销申请");
        }

        // 4. 更新申请状态为 "CANCELED"
        application.setStatus("CANCELED");
        applicationsMapper.updateById(application);

        // 5. 释放被占用的号源
        if (application.getScheduleSlotId() != null) {
            scheduleSlotsMapper.atomicallyCancelBooking(application.getScheduleSlotId()); // 假设有这个原子取消方法
        }
    }

    @Override
    public ApplicationDetailVO updateApplication(Long id, AdminApplicationUpdateDto updateDto) {
        // 1. 查找并锁定申请记录，防止并发修改
        InitialVisitApplications application = applicationsMapper.selectById(id);
        if (application == null) {
            throw new BaseException("找不到要更新的申请记录");
        }

        // 业务校验：通常只有处于“已批准(APPROVED)”状态的预约才能被改约
        if (!"APPROVED".equals(application.getStatus())) {
            throw new BaseException("只有已批准的预约才能进行改约操作");
        }

        Long oldSlotId = application.getScheduleSlotId();
        Long newSlotId = updateDto.getScheduleSlotId();
        String newLocation = updateDto.getLocation();

        // 2. 处理时间/老师的改约（当传入了新的 scheduleSlotId 时）
        if (newSlotId != null && !newSlotId.equals(oldSlotId)) {
            // a. 释放【旧的】号源
            if (oldSlotId != null) {
                int cancelRows = slotMapper.atomicallyCancelBooking(oldSlotId);
                if (cancelRows == 0) {
                    log.warn("改约时，尝试释放旧号源(id={})失败，其状态可能已不是BOOKED。", oldSlotId);
                }
            }
            // b. 预定【新的】号源
            int bookRows = slotMapper.atomicallyBookSlot(newSlotId);
            if (bookRows == 0) {
                // 重要：因为旧号源已被释放，但新号源预定失败，事务必须回滚！
                throw new BaseException("改约失败，您选择的新时间段已被他人抢先预约");
            }

            // c. 更新申请记录中的关联信息
            ScheduleSlots newSlot = slotMapper.selectById(newSlotId);
            application.setScheduleSlotId(newSlotId);
            application.setAssignedInterviewerId(newSlot.getStaffId());
        }

        // 3. 处理地点的改约
        // 注意：这里的逻辑是，如果地点和时间都改了，就更新新号源的地点
        Long finalSlotId = application.getScheduleSlotId();
        if (StringUtils.hasText(newLocation) && finalSlotId != null) {
            ScheduleSlots slotToUpdateLocation = new ScheduleSlots();
            slotToUpdateLocation.setId(finalSlotId);
            slotToUpdateLocation.setLocation(newLocation);
            slotMapper.updateById(slotToUpdateLocation);
        }

        // 4. 将申请表本身的更新持久化到数据库
        applicationsMapper.updateById(application);

        // 5. 查询并返回更新后的完整视图对象
        return buildDetailVO(application);
    }

    @Override
    public Page<ApplicationListVO> listMyApplications(Long userId, long pageNum, long pageSize) {
        // 1. 创建 MyBatis-Plus 的分页对象，用于查询主表
        Page<InitialVisitApplications> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件，只查询属于当前用户的申请，并按创建时间倒序
        QueryWrapper<InitialVisitApplications> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", userId);
        wrapper.orderByDesc("created_at");

        // 3. 执行分页查询，得到 Page<InitialVisitApplication>
        applicationsMapper.selectPage(page, wrapper);

        // --- 数据转换与组装 ---

        // 4. 创建一个用于最终返回的 Page<ApplicationListVO> 对象，并复制分页信息
        Page<ApplicationListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<InitialVisitApplications> records = page.getRecords();

        if (records.isEmpty()) {
            return voPage; // 如果当前页没有数据，直接返回
        }

        // 5. 【核心】避免 N+1 查询：
        // a. 提取所有需要查询的关联ID (初访员ID和号源ID)
        Set<Long> interviewerIds = records.stream()
                .map(InitialVisitApplications::getAssignedInterviewerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> slotIds = records.stream()
                .map(InitialVisitApplications::getScheduleSlotId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // b. 一次性批量查询出所有相关的用户信息和号源信息
        Map<Long, Users> userMap;
        if (!interviewerIds.isEmpty()) {
            userMap = usersMapper.selectBatchIds(interviewerIds).stream()
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
        List<ApplicationListVO> voRecords = records.stream().map(application -> {
            ApplicationListVO vo = new ApplicationListVO();
            BeanUtils.copyProperties(application, vo);
            // 设置初访员姓名
            if (application.getAssignedInterviewerId() != null) {
                Users interviewer = userMap.get(application.getAssignedInterviewerId());
                if (interviewer != null) {
                    vo.setInterviewerName(interviewer.getName());
                }
            }

            // 设置预约时间
            if (application.getScheduleSlotId() != null) {
                ScheduleSlots slot = slotMap.get(application.getScheduleSlotId());
                if (slot != null) {
                    vo.setRequestedTime(slot.getStartTime());
                }
            }
            vo.setSubmittedTime(application.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voRecords);
        return voPage;
    }

    @Override
    public void cancelApplicationByAdmin(Long applicationId) {
        // 1. 查找申请记录
        InitialVisitApplications application = applicationsMapper.selectById(applicationId);
        if (application == null) {
            throw new BaseException("操作失败，申请记录不存在");
        }

        // 2. 状态检查：只有处于“已提交”或“已批准”状态的申请才能被取消
        String currentStatus = application.getStatus();
        if (!"SUBMITTED".equals(currentStatus) && !"APPROVED".equals(currentStatus)) {
            throw new BaseException("操作失败，该申请已完成或已被取消，无法操作");
        }

        // 3. 更新申请状态为 "CANCELED"
        application.setStatus("CANCELED");
        // 可以在一个备注字段中记录取消原因
        // application.setNotes("取消原因: " + reason);
        applicationsMapper.updateById(application);

        // 4. 【关键】释放被占用的号源，使其恢复为可预约状态
        // 只有当申请有关联的号源时（即已预约或已批准），才需要释放
        if (application.getScheduleSlotId() != null) {
            int affectedRows = slotMapper.atomicallyCancelBooking(application.getScheduleSlotId());
            if (affectedRows == 0) {
                // 记录一个警告日志，因为这可能意味着数据状态不一致
                log.warn("管理员取消申请(id={})时，尝试释放号源(id={})失败，其状态并非'BOOKED'。", applicationId, application.getScheduleSlotId());
            }
        }

        // 5. (可选) 发送取消通知给学生
        // User student = userMapper.selectById(application.getStudentId());
        // notificationService.sendCancellationSmsToStudent(student, reason);

    }


    private ApplicationDetailVO buildDetailVO(InitialVisitApplications application) {
        if (application == null) {
            return null;
        }

        ApplicationDetailVO vo = new ApplicationDetailVO();
        BeanUtils.copyProperties(application, vo);

        // 使用 Batch 查询优化性能，避免 N+1 问题
        Users student = usersMapper.selectById(application.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getName());
            vo.setStudentUsername(student.getUsername());
            vo.setStudentPhone(student.getPhone());
            // vo.setStudentCollege(student.getCollege()); // 假设User实体有college字段
        }

        if (application.getAssignedInterviewerId() != null) {
            Users interviewer = usersMapper.selectById(application.getAssignedInterviewerId());
            if (interviewer != null) {
                vo.setInterviewerName(interviewer.getName());
                vo.setInterviewerPhone(interviewer.getPhone());
            }
        }

        if (application.getScheduleSlotId() != null) {
            ScheduleSlots slot = scheduleSlotsMapper.selectById(application.getScheduleSlotId());
            if (slot != null) {
                vo.setRequestedTime(slot.getStartTime());
                // 如果 VO 需要地点信息，可以在这里设置
                // vo.setLocation(slot.getLocation());
            }
        }

        return vo;
    }
}
