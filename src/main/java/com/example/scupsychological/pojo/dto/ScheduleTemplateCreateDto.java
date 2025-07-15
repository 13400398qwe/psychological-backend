package com.example.scupsychological.pojo.dto;

import com.example.scupsychological.common.enums.RecurrenceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "创建值班模板请求体")
public class ScheduleTemplateCreateDto {
    @NotNull
    @Schema(description = "关联的员工ID") private Long staffId;
    @NotNull @Schema(description = "重复类型, 如 WEEKLY") private RecurrenceType recurrenceType;
    @NotNull @Schema(description = "星期几 (1-7)") private Integer dayOfWeek;
    @NotNull @Schema(description = "开始时间, 格式 HH:mm:ss") private LocalTime startTime;
    @NotNull @Schema(description = "结束时间, 格式 HH:mm:ss") private LocalTime endTime;
    @NotNull @Schema(description = "单次服务时长（分钟）") private Integer slotDurationMinutes;
    @Schema(description = "默认地点") private String location;
    @NotNull @Schema(description = "模板生效开始日期") private LocalDate effectiveFrom;
    @NotNull @Schema(description = "模板生效结束日期") private LocalDate effectiveTo;
    @NotNull @Schema(description = "是否激活") private Boolean isActive;
}
