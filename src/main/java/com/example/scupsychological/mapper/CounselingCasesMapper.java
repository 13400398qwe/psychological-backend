package com.example.scupsychological.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.CaseAssistantQueryDto;
import com.example.scupsychological.pojo.dto.CaseStatsQueryDto;
import com.example.scupsychological.pojo.dto.CounselorCaseQueryDto;
import com.example.scupsychological.pojo.dto.WorkloadQueryDto;
import com.example.scupsychological.pojo.entity.CounselingCases;
import com.example.scupsychological.pojo.vo.*;
import io.swagger.v3.core.util.Json;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
 * <p>
 * 咨询个案表 Mapper 接口
 * </p>
 *
 * @author tpj
 * @since 2025-06-21
 */
@Mapper
public interface CounselingCasesMapper extends BaseMapper<CounselingCases> {

    List<PendingCaseVO> selectPendingCases();

    Page<CounselorCaseListVO> selectCasesForCounselor(Page<?> page, @Param("counselorId") Long counselorId, @Param("query")CounselorCaseQueryDto queryDto);


    List<CounselorWorkloadVO> calculateCounselorWorkload(WorkloadQueryDto queryDto);

    List<CounselorCaseDetailVO> selectAllClosedCaseDetails(@Param("query")CaseStatsQueryDto queryDto);

    Page<CaseAssistantListVO> selectMyCasesForAssistant(Page<CaseAssistantListVO> page, Long assistantId,@Param("query") CaseAssistantQueryDto queryDto);

    CounselorCaseDetailVO selectCaseDetailForCounselor(Long caseId, Long counselorId);

    Page<CaseReportSummaryVO> selectClosedCaseSummaries(Page<CaseReportSummaryVO> page, @Param("query") CaseStatsQueryDto queryDto);

    Page<CaseReportContentVO> getCaseReportContent(Page<CaseReportContentVO> page, @Param("userId") Long userId);
}
