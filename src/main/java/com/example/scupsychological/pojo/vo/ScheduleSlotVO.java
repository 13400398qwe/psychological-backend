package com.example.scupsychological.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "可预约号源的视图对象")
public class ScheduleSlotVO {

    @Schema(description = "号源的唯一ID")
    private Long id;

    @Schema(description = "关联的员工ID")
    private Long staffId;

    @Schema(description = "关联的员工姓名", example = "张老师")
    private String staffName;

    @Schema(description = "号源开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "号源结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "咨询地点")
    private String location;

    @Schema(description = "状态 (AVAILABLE, BOOKED, CANCELED)")
    private String status;
}