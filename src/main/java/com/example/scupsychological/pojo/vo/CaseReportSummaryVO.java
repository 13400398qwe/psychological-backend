package com.example.scupsychological.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "已结案个案的摘要视图对象，用于统计分析列表")
public class CaseReportSummaryVO {

    @Schema(description = "个案ID")
    private Long caseId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "学生学号")
    private String studentUsername;
    @Schema(description = "学生所属学院")
    private String studentCollege;
    @Schema(description = "学生电话")
    private String studentPhone;

    @Schema(description = "负责该个案的咨询师姓名")
    private String counselorName;
    @Schema(description = "咨询师所属学院")
    private String counselorCollege;
    @Schema(description = "咨询师电话")
    private String counselorPhone;

    @Schema(description = "问题类型 (从初访记录中获取)")
    private String problemType;

    @Schema(description = "个案状态 (如 CLOSED, DROPPED_OUT)")
    private String status;

    @Schema(description = "结案报告完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportFinalizedAt;
}

