package com.example.scupsychological.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaseListVO {
    private Long caseId;
    private String studentName;
    private String counselorName;
    private String status;
    private Integer totalSessions;
    private Integer completedSessions; // 已完成的咨询次数
    private LocalDateTime nextSessionTime; // 下一次咨询的时间
}
