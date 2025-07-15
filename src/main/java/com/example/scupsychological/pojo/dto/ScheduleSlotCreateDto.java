package com.example.scupsychological.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理员手动创建号源的请求体")
public class ScheduleSlotCreateDto {

    @NotNull(message = "必须指定员工ID")
    @Schema(description = "关联的员工ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long staffId;

    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须是未来的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "号源开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-07-01 14:00:00")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Future(message = "结束时间必须是未来的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "号源结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-07-01 14:50:00")
    private LocalDateTime endTime;

    @Schema(description = "咨询地点", example = "A301咨询室")
    private String location;
}