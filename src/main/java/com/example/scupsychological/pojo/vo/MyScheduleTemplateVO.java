package com.example.scupsychological.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Schema(description = "我的值班模板视图对象")
public class MyScheduleTemplateVO {
    // 这个VO的结构可以复用我们之前设计的 ScheduleTemplateVO，
    // 但可能不需要 staffId 和 staffName，因为用户查的就是自己的
    private Long id;
    private String recurrenceType;
    private Integer dayOfWeek;
    private LocalTime startTime; // 格式化为 "HH:mm"
    private LocalTime endTime;
    private Integer slotDurationMinutes;
    private String location;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Boolean isActive;
}