package com.example.scupsychological.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.CaseExtensionRequestDto;
import com.example.scupsychological.pojo.dto.CaseReportSubmitDto;
import com.example.scupsychological.pojo.dto.CounselingSessionUpdateDto;
import com.example.scupsychological.pojo.dto.CounselorCaseQueryDto;
import com.example.scupsychological.pojo.entity.CounselingCases;
import com.example.scupsychological.pojo.entity.CounselingSessions;
import com.example.scupsychological.pojo.vo.CounselorCaseDetailVO;
import com.example.scupsychological.pojo.vo.CounselorCaseListVO;
import jakarta.validation.Valid;

public interface CounselingService {
    // ... 其他已有的方法保持不变 ...
    Page<CounselorCaseListVO> getMyCases(Long counselorId, CounselorCaseQueryDto page);

    CounselorCaseDetailVO getCaseDetail(Long counselorId, Long caseId);

    CounselingSessions updateSession(Long userId, Long sessionId, @Valid CounselingSessionUpdateDto updateDto);

    void requestExtension(Long userId, Long caseId, @Valid CaseExtensionRequestDto requestDto);

    CounselingCases submitReport(Long counselorId, CaseReportSubmitDto submitDto);
}
