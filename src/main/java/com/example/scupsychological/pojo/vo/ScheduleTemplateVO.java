package com.example.scupsychological.pojo.vo; // 建议放在 vo 包下

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Schema(description = "值班模板视图对象，用于前端展示")
public class ScheduleTemplateVO {

    @Schema(description = "模板的唯一ID")
    private Long id;

    @Schema(description = "关联的员工ID")
    private Long staffId;


    @Schema(description = "重复类型", example = "WEEKLY")
    private String recurrenceType; // 使用字符串表示，更通用

    @Schema(description = "星期几 (1-7, 1代表周一)", example = "1")
    private Integer dayOfWeek;

    @Schema(description = "开始时间", example = "14:00")
    @JsonFormat(pattern = "HH:mm") // 控制JSON输出格式
    private LocalTime startTime;

    @Schema(description = "结束时间", example = "17:00")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "单次服务时长（分钟）", example = "50")
    private Integer slotDurationMinutes;

    @Schema(description = "默认地点", example = "心理咨询室 A301")
    private String location;

    @Schema(description = "模板生效开始日期", example = "2025-09-01")
    private LocalDate effectiveFrom;

    @Schema(description = "模板生效结束日期", example = "2026-01-15")
    private LocalDate effectiveTo;

    @Schema(description = "是否激活")
    private Boolean isActive;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间")
    private LocalDateTime updatedAt;
}
