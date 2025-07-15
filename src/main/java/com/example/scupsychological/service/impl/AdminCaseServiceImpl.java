package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.exception.BaseException;
import com.example.scupsychological.mapper.ScheduleSlotsMapper;
import com.example.scupsychological.pojo.dto.PendingExtensionQueryDto;
import com.example.scupsychological.pojo.entity.CounselingCases;
import com.example.scupsychological.pojo.entity.CounselingSessions;
import com.example.scupsychological.pojo.entity.CaseExtensionRequest;
import com.example.scupsychological.mapper.CounselingCasesMapper;
import com.example.scupsychological.mapper.CounselingSessionsMapper;
import com.example.scupsychological.mapper.CaseExtensionRequestMapper;
import com.example.scupsychological.pojo.entity.ScheduleSlots;
import com.example.scupsychological.service.AdminCaseService;
import com.example.scupsychological.pojo.dto.ExtensionApprovalDto;
import com.example.scupsychological.pojo.vo.PendingExtensionRequestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCaseServiceImpl implements AdminCaseService {

    private final CaseExtensionRequestMapper extensionRequestMapper;
    private final CounselingCasesMapper caseMapper;
    private final CounselingSessionsMapper sessionMapper;
    private final ScheduleSlotsMapper slotMapper;

    @Override
    public Page<PendingExtensionRequestVO> findPendingExtensionRequests(PendingExtensionQueryDto queryDto) {
        // 1. 创建分页对象
        Page<PendingExtensionRequestVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());

        // 2. 调用自定义的 Mapper 方法，传入分页对象和筛选条件
        extensionRequestMapper.selectPendingRequests(page, queryDto);

        return page;
    }

    @Override
    @Transactional
    public void approveExtension(Long requestId, ExtensionApprovalDto approvalDto) {
        // 1. 查找申请记录并校验
        CaseExtensionRequest request = extensionRequestMapper.selectById(requestId);
        if (request == null || !"PENDING".equals(request.getStatus())) {
            throw new BaseException("该申请不存在或已被处理");
        }
        CounselingCases counselingCase = caseMapper.selectById(request.getCaseId());
        if (counselingCase == null) {
            throw new BaseException("关联的咨询个案不存在");
        }

        // 2. 计算未来需要追加的咨询的【期望开始时间】
        CounselingSessions lastSession = sessionMapper.selectOne(
                new QueryWrapper<CounselingSessions>().eq("case_id", request.getCaseId()).orderByDesc("session_number").last("LIMIT 1")
        );
        if (lastSession == null) {
            throw new BaseException("无法为没有历史记录的个案加时");
        }

        List<LocalDateTime> expectedStartTimes = new ArrayList<>();
        for (int i = 1; i <= request.getRequestedSessions(); i++) {
            expectedStartTimes.add(lastSession.getStartTime().plusWeeks(i));
        }

        // 3. 查找这些时间点上，该咨询师【真实存在且可用】的号源
        List<ScheduleSlots> availableSlots = slotMapper.selectList(
                new QueryWrapper<ScheduleSlots>()
                        .eq("staff_id", counselingCase.getCounselorId())
                        .in("start_time", expectedStartTimes)
                        .eq("status", "AVAILABLE")
        );

        // 4. 【预检查】
        if (availableSlots.size() < request.getRequestedSessions()) {
            throw new BaseException("批准失败：该咨询师在未来的部分期望时间点没有可用的排班号源。");
        }

        // 5. 【批量锁定】
        List<Long> slotIdsToBook = availableSlots.stream().map(ScheduleSlots::getId).toList();
        int affectedRows = slotMapper.atomicallyBookSlots(slotIdsToBook);
        if (affectedRows != request.getRequestedSessions()) {
            throw new BaseException("批准失败，部分时间段在您操作时已被占用，请刷新后重试。");
        }

        // 6. 更新个案的总次数
        counselingCase.setTotalSessions(counselingCase.getTotalSessions() + request.getRequestedSessions());
        counselingCase.setStatus("IN_PROGRESS");
        caseMapper.updateById(counselingCase);

        // 7. 创建新的咨询场次记录，并关联上已锁定的号源ID
        List<CounselingSessions> newSessions = new ArrayList<>();
        for (int i = 0; i < availableSlots.size(); i++) {
            ScheduleSlots slot = availableSlots.get(i);
            CounselingSessions session = new CounselingSessions();
            session.setCaseId(request.getCaseId());
            session.setSessionNumber(lastSession.getSessionNumber() + i + 1);
            session.setScheduleSlotId(slot.getId()); // 关键：关联号源ID
            session.setStartTime(slot.getStartTime());
            session.setEndTime(slot.getEndTime());
            session.setLocation(slot.getLocation());
            session.setStatus("PENDING");
            newSessions.add(session);
        }
        sessionMapper.insertBatch(newSessions);

        // 8. 更新加时申请记录本身的状态
        request.setStatus("APPROVED");
        request.setAdminNotes(approvalDto != null ? approvalDto.getAdminNotes() : "批准通过");
        request.setProcessedAt(LocalDateTime.now());
        extensionRequestMapper.updateById(request);

        // 6. (可选) 发送批准通知给咨询师
    }

    @Override
    @Transactional
    public void rejectExtension(Long requestId, ExtensionApprovalDto approvalDto) {
        // 1. 校验输入
        if (approvalDto == null || !StringUtils.hasText(approvalDto.getAdminNotes())) {
            throw new BaseException("拒绝加时申请时必须提供理由");
        }

        // 2. 查找申请记录并校验状态
        CaseExtensionRequest request = extensionRequestMapper.selectById(requestId);
        if (request == null || !"PENDING".equals(request.getStatus())) {
            throw new BaseException("该申请不存在或已被处理");
        }

        // 3. 找到对应的咨询个案
        CounselingCases counselingCase = caseMapper.selectById(request.getCaseId());
        if (counselingCase != null) {
            // 4. 将个案状态从“等待审批”恢复为“进行中”
            counselingCase.setStatus("IN_PROGRESS");
            caseMapper.updateById(counselingCase);
        }

        // 5. 更新加时申请记录本身的状态和备注
        request.setStatus("REJECTED");
        request.setAdminNotes(approvalDto.getAdminNotes());
        request.setProcessedAt(LocalDateTime.now());
        extensionRequestMapper.updateById(request);

        // 6. (可选) 发送拒绝通知给咨询师，并告知原因
    }
}
