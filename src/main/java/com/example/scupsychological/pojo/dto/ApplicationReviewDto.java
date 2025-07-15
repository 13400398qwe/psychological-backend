package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "管理员审核初访申请的请求体")
public class ApplicationReviewDto {
    @NotBlank(message = "审核决策不能为空")
    @Schema(description = "审核后的新状态 (APPROVED, REJECTED)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "【如果批准】必须指派一名初访员的ID")
    private Long assignedInterviewerId;

    @Schema(description = "【如果拒绝】可以提供拒绝的原因")
    private String rejectionReason;
}
