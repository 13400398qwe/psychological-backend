package com.example.scupsychological.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.CaseStatsQueryDto;
import com.example.scupsychological.pojo.dto.WorkloadQueryDto;
import com.example.scupsychological.pojo.vo.CaseReportSummaryVO;
import com.example.scupsychological.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "后台管理模块 - 统计分析")
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/cases")
    @Operation(summary = "汇总查询已结案的个案列表")
    public Result<Page<CaseReportSummaryVO>> queryCaseReports(CaseStatsQueryDto queryDto) {
        Page<CaseReportSummaryVO> resultPage = statisticsService.queryClosedCases(queryDto);
        return Result.success(resultPage);
    }

    @GetMapping("/cases/export-reports")
    @Operation(summary = "根据条件批量下载结案报告 (ZIP)")
    public ResponseEntity<byte[]> exportCaseReportsAsZip(CaseStatsQueryDto queryDto) {
        byte[] zipBytes = statisticsService.exportReportsToZip(queryDto);

        String filename = "case_reports_" + LocalDate.now() + ".zip";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }
    @GetMapping("/counselor-workload/export-excel")
    @Operation(summary = "导出咨询师工作量统计Excel")
    public ResponseEntity<byte[]> exportCounselorWorkload(WorkloadQueryDto queryDto) { // 假设有一个新的查询DTO
        byte[] excelBytes = statisticsService.exportCounselorWorkloadAsExcel(queryDto);

        String filename = "counselor_workload_" + LocalDate.now() + ".xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
}

