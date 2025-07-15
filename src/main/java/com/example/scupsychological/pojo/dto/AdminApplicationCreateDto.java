package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "管理员为学生新增初访预约的请求体")
public class AdminApplicationCreateDto {
    @NotNull(message = "必须指定学生ID")
    @Schema(description = "要为其创建预约的学生用户ID")
    private Long studentId;

    @NotNull(message = "必须选择一个预约时间段")
    @Schema(description = "要预约的时间段ID (schedule_slot_id)")
    private Long scheduleSlotId;

    // 注意：管理员创建时，通常直接进入“已批准”状态，
    // 因此问卷环节可以跳过或后续由学生补充。
}

