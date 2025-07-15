package com.example.scupsychological.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "在指定时间可用的初访员信息")
public class AvailableInterviewerVO {
    private Long staffId;       // 员工ID
    private String staffName;   // 员工姓名
    private Long scheduleSlotId;// 对应的可预约号源ID
}
