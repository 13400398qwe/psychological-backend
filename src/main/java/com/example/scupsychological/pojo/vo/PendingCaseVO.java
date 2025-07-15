package com.example.scupsychological.pojo.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "待安排正式咨询的个案摘要视图对象 (供心理助理使用)")
public class PendingCaseVO {

    @Schema(description = "初访记录ID，助理安排时需要用到此ID")
    private Long initialVisitRecordId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "学生学号")
    private String studentUsername;

    @Schema(description = "初访员评估的【危机等级】")
    private String crisisLevel;

    @Schema(description = "初访员评估的【问题类型】")
    private String problemType;

    @Schema(description = "初访员的专业备注或观察记录")
    private String interviewerNotes;
}