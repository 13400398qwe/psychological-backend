package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "管理员审核并指派初访员的请求体")
public class AdminReviewAssignDto {

    @NotNull(message = "必须选择一个号源进行指派")
    @Schema(description = "最终确认并要锁定的号源ID (schedule_slot_id)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long scheduleSlotId;

    @NotNull(message = "必须指派一名初访员")
    @Schema(description = "被指派的初访员ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long assignedInterviewerId;
}

