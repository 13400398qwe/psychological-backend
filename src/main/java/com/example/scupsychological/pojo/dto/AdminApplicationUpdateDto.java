package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "管理员改约申请的请求体")
public class AdminApplicationUpdateDto {
    @Schema(description = "新的时间段ID (如果需要改约时间或老师)")
    private Long scheduleSlotId;

    @Schema(description = "新的咨询地点 (如果需要)")
    private String location; // 假设 schedule_slots 表中有 location 字段
}