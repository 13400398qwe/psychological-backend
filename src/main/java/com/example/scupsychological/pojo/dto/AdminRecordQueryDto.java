package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@Schema(description = "管理员分页查询初访记录的参数")
public class AdminRecordQueryDto {

    @Schema(description = "页码, 从1开始", defaultValue = "1")
    private long pageNum = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    private long pageSize = 10;

    @Schema(description = "按学生姓名或学号模糊搜索")
    private String studentKeyword;

    @Schema(description = "按初访员姓名模糊搜索")
    private String interviewerName;

    @Schema(description = "按初访结论筛选 (NO_COUNSELING, ARRANGE_COUNSELING, REFERRAL)")
    private String conclusion;

    @Schema(description = "按初访日期范围筛选 - 开始日期")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Schema(description = "按初访日期范围筛选 - 结束日期")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}
