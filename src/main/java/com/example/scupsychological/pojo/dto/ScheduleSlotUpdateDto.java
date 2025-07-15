package com.example.scupsychological.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理员手动更新号源的请求体")
public class ScheduleSlotUpdateDto {

    @Schema(description = "关联的员工ID")
    private Long staffId;

    @Future(message = "开始时间必须是未来的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "号源开始时间", example = "2025-07-01 14:00:00")
    private LocalDateTime startTime;
    @Future(message = "结束时间必须是未来的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "号源结束时间", example = "2025-07-01 14:30:00")
    private LocalDateTime endTime;
    @Schema(description = "状态", example = "AVAILABLE")
    private String status;
    @Schema(description = "咨询地点", example = "A301咨询室")
    private String location;
    // ... 其他字段可以按需添加
}