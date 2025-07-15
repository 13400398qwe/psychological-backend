package com.example.scupsychological.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用于列表展示的申请信息摘要")
public class ApplicationListVO {
    private Long id;
    private String studentName;
    private String studentUsername; // 学号
    private String interviewerName;
    private LocalDateTime requestedTime;
    private LocalDateTime submittedTime;
    private String status;
    private Boolean isUrgent;
    private String crisisLevel;
    private String questionnaireContent;
    private Integer calculatedScore;
}