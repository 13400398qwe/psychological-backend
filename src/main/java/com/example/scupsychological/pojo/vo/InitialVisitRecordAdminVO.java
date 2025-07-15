package com.example.scupsychological.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "向管理员展示的初访记录视图对象")
public class InitialVisitRecordAdminVO {

    @Schema(description = "初访记录ID")
    private Long recordId;

    @Schema(description = "关联的初访申请ID")
    private Long applicationId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "学生学号")
    private String studentUsername;
    @Schema(description = "学生号码")
    private String studentPhone;

    @Schema(description = "负责本次初访的初访员姓名")
    private String interviewerName;

    @Schema(description = "实际初访时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime visitTime;

    @Schema(description = "评估的危机等级")
    private String crisisLevel;

    @Schema(description = "评估的问题类型")
    private String problemType;

    @Schema(description = "最终的初访结论")
    private String conclusion;
}