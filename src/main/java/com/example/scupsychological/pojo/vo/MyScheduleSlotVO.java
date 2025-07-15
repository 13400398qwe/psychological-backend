package com.example.scupsychological.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "我的具体日程（号源）视图对象")
public class MyScheduleSlotVO {
    // 这个VO与之前设计的 ApplicationListVO 中的部分信息重叠
    // 但它更聚焦于号源本身的状态
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String status; // AVAILABLE, BOOKED, CANCELED

    // 如果号源已被预约，我们需要显示学生信息
    private Long studentId;
    private String studentName;
    private String studentUsername; // 学号
    private String studentPhone;
}
