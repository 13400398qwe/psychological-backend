package com.example.scupsychological.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "向初访员展示的初访申请任务详情VO")
public class ApplicationInterviewerVO {

    @Schema(description = "申请ID")
    private Long applicationId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "学生学号")
    private String studentUsername;

    @Schema(description = "学生联系电话")
    private String studentPhone;

    @Schema(description = "预定的初访时间")
    private LocalDateTime scheduledTime;

    @Schema(description = "预定的初访地点")
    private String location;

    @Schema(description = "申请的紧急状态")
    private Boolean isUrgent;

    @Schema(description = "申请的当前状态 (如 APPROVED, COMPLETED)")
    private String status;

    @Schema(description = "【仅详情页提供】学生的完整问卷提交记录")
    private QuestionnaireSubmissionVO submission; // 复用我们之前设计的问卷提交VO
}
