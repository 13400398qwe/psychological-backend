package com.example.scupsychological.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.CaseAssistantQueryDto;
import com.example.scupsychological.pojo.dto.CaseScheduleDto;
import com.example.scupsychological.pojo.entity.CounselingCases;
import com.example.scupsychological.pojo.vo.CaseAssistantListVO;
import com.example.scupsychological.pojo.vo.CaseReportContentVO;
import com.example.scupsychological.pojo.vo.PendingCaseVO;
import io.swagger.v3.core.util.Json;
import jakarta.validation.Valid;

import java.util.List;

public interface CounselingCaseService {
    public List<PendingCaseVO> findPendingCases();
    public CounselingCases scheduleNewCase(@Valid CaseScheduleDto scheduleDto, Long userId) ;

    byte[] exportReportAsWord(Long counselorId, Long caseId);


    Page<CaseAssistantListVO> listMyCases(Long userId, CaseAssistantQueryDto queryDto);

    Page<CaseReportContentVO> getCaseReportContent(Long userId, Long pageNum, Long pageSize);
}
