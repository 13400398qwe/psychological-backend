package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Schema(description = "结案报告汇总查询的参数")
public class CaseStatsQueryDto {
    // 这里可以包含分页参数，但对于导出功能，我们通常会忽略它们以导出全部
    @Schema(description = "页码, 从1开始", defaultValue = "1")
    private long pageNum = 1;
    @Schema(description = "每页数量", defaultValue = "10")
    private long pageSize = 10;

    @Schema(description = "按学生姓名或学号筛选")
    private String studentKeyword;

    @Schema(description = "按咨询师姓名筛选")
    private String counselorName;

    @Schema(description = "按问题类型筛选")
    private String problemType;

    @Schema(description = "按结案日期范围筛选 - 开始日期")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Schema(description = "按结案日期范围筛选 - 结束日期")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}