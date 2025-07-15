package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.exception.BaseException;
import com.example.scupsychological.mapper.CaseExtensionRequestMapper;
import com.example.scupsychological.mapper.CounselingCasesMapper;
import com.example.scupsychological.mapper.CounselingSessionsMapper;
import com.example.scupsychological.mapper.ScheduleSlotsMapper;
import com.example.scupsychological.pojo.dto.CaseExtensionRequestDto;
import com.example.scupsychological.pojo.dto.CaseReportSubmitDto;
import com.example.scupsychological.pojo.dto.CounselingSessionUpdateDto;
import com.example.scupsychological.pojo.dto.CounselorCaseQueryDto;
import com.example.scupsychological.pojo.entity.CaseExtensionRequest;
import com.example.scupsychological.pojo.entity.CounselingCases;
import com.example.scupsychological.pojo.entity.CounselingSessions;
import com.example.scupsychological.pojo.entity.ScheduleSlots;
import com.example.scupsychological.pojo.vo.CounselingSessionVO;
import com.example.scupsychological.pojo.vo.CounselorCaseDetailVO;
import com.example.scupsychological.pojo.vo.CounselorCaseListVO;
import com.example.scupsychological.service.CounselingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CounselingServiceImpl implements CounselingService {

    private final CounselingCasesMapper caseMapper;
    private final CounselingSessionsMapper sessionMapper;
    private final ObjectMapper objectMapper;
    private final CaseExtensionRequestMapper extensionRequestMapper;
    private final ScheduleSlotsMapper slotMapper;

    // ... 其他已有的方法保持不变 ...

    @Override
    public Page<CounselorCaseListVO> getMyCases(Long counselorId, CounselorCaseQueryDto queryDto) {
        // 1. 创建分页对象
        Page<CounselorCaseListVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        // 2. 调用自定义的 Mapper 方法，传入所有参数
        caseMapper.selectCasesForCounselor(page, counselorId, queryDto);
        return page;
    }

    @Override
    public CounselorCaseDetailVO getCaseDetail(Long counselorId, Long caseId) {
        // 1. 调用自定义 Mapper 方法，获取个案的核心信息（已包含安全校验）
        CounselorCaseDetailVO caseDetail = caseMapper.selectCaseDetailForCounselor(caseId, counselorId);

        if (caseDetail == null) {
            throw new AccessDeniedException("无法访问该个案，可能不存在或不属于您");
        }

        // 2. 查询该个案下的所有咨询安排实体
        List<CounselingSessions> sessionEntities = sessionMapper.selectList(
                new QueryWrapper<CounselingSessions>().eq("case_id", caseId).orderByAsc("session_number")
        );

        // 3. 将实体列表转换为 VO 列表
        List<CounselingSessionVO> sessionVOs = sessionEntities.stream().map(entity -> {
            CounselingSessionVO vo = new CounselingSessionVO();
            BeanUtils.copyProperties(entity, vo);
            vo.setScheduledTime(entity.getStartTime());
            vo.setSessionId(entity.getId());
            return vo;
        }).collect(Collectors.toList());

        // 4. 将 session 列表设置到详情 VO 中并返回
        caseDetail.setSessions(sessionVOs);
        return caseDetail;
    }
    @Override
    @Transactional
    public CounselingSessions updateSession(Long counselorId, Long sessionId, CounselingSessionUpdateDto updateDto) {
        CounselingSessions session = sessionMapper.selectById(sessionId);
        if (session == null) throw new BaseException("该次咨询记录不存在");

        CounselingCases counselingCase = caseMapper.selectById(session.getCaseId());
        if (counselingCase == null || !counselingCase.getCounselorId().equals(counselorId)) {
            throw new AccessDeniedException("无权操作不属于您的个案记录");
        }

        session.setStatus(updateDto.getStatus());
        session.setCounselorNotes(updateDto.getCounselorNotes());
        sessionMapper.updateById(session);
        //将slot的状态改为completed
        slotMapper.markSlotAsCompleted(session.getScheduleSlotId());
        return session;
    }

    @Override
    @Transactional
    public void requestExtension(Long counselorId, Long caseId, CaseExtensionRequestDto requestDto) {
        // 1. 安全校验：确认个案属于当前咨询师
        CounselingCases counselingCase = caseMapper.selectById(caseId);
        if (counselingCase == null || !counselingCase.getCounselorId().equals(counselorId)) {
            throw new AccessDeniedException("无权为不属于您的个案申请加时");
        }

        // 2. 业务校验：只有“进行中”的个案才能申请加时
        if (!"IN_PROGRESS".equals(counselingCase.getStatus())) {
            throw new BaseException("只有进行中的个案才能申请加时");
        }

        // 3. 业务校验：检查是否存在已在审批中的加时申请
        if (extensionRequestMapper.exists(new QueryWrapper<CaseExtensionRequest>().eq("case_id", caseId).eq("status", "PENDING"))) {
            throw new BaseException("该个案已存在待审批的加时申请，请勿重复提交");
        }

        // 4. 创建并保存一条新的“加时申请”记录
        CaseExtensionRequest newRequest = new CaseExtensionRequest();
        newRequest.setCaseId(caseId);
        newRequest.setCounselorId(counselorId);
        newRequest.setRequestedSessions(requestDto.getAdditionalSessions());
        newRequest.setReason(requestDto.getReason());
        newRequest.setStatus("PENDING"); // 初始状态为待审批
        extensionRequestMapper.insert(newRequest);

        // 5. 更新个案主表的状态，标记为“等待加时审批”
        counselingCase.setStatus("AWAITING_EXTENSION_APPROVAL");
        caseMapper.updateById(counselingCase);

        // 6. (可选) 发送通知给中心管理员，告知有新的审批任务
    }

    @Override
    @Transactional
    public CounselingCases submitReport(Long counselorId, CaseReportSubmitDto submitDto) {
        Long caseId = submitDto.getCaseId();

        // 1. 安全校验：确认个案属于当前咨询师
        CounselingCases counselingCase = caseMapper.selectById(caseId);
        if (counselingCase == null || !counselingCase.getCounselorId().equals(counselorId)) {
            throw new AccessDeniedException("无权为不属于您的个案提交报告");
        }

        // 2. 业务校验：防止重复提交报告
        if (counselingCase.getReportContent() != null) {
            throw new BaseException("请勿重复提交结案报告");
        }

        // 3. 将报告内容序列化为 JSON 字符串
        try {
            String contentJson = objectMapper.writeValueAsString(submitDto.getReportContent());
            counselingCase.setReportContent(contentJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("报告内容序列化失败", e);
        }

        // 4. 更新个案状态和报告完成时间
        counselingCase.setStatus("CLOSED");
        counselingCase.setReportFinalizedAt(LocalDateTime.now());

        // 5. 将包含报告的完整个案信息更新回数据库
        caseMapper.updateById(counselingCase);

        return counselingCase;
    }
}
