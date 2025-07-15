package com.example.scupsychological.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.vo.CaseReportSummaryVO;
import com.example.scupsychological.pojo.dto.CaseStatsQueryDto;
import com.example.scupsychological.pojo.dto.WorkloadQueryDto;

public interface StatisticsService {
    byte[] exportReportsToZip(CaseStatsQueryDto queryDto);

    Page<CaseReportSummaryVO> queryClosedCases(CaseStatsQueryDto queryDto);


    byte[] exportCounselorWorkloadAsExcel(WorkloadQueryDto queryDto);
}
