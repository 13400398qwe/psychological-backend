package com.example.scupsychological.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "面向学生的正式咨询个案视图对象")
public class CounselingCaseVO {

    @Schema(description = "个案ID")
    private Long caseId;

    @Schema(description = "负责此个案的咨询师姓名")
    private String counselorName;

    @Schema(description = "个案当前状态 (IN_PROGRESS, CLOSED等)")
    private String status;

    @Schema(description = "总咨询次数")
    private Integer totalSessions;

    @Schema(description = "已完成的咨询次数")
    private Integer completedSessions;

    @Schema(description = "下一次咨询的时间")
    private LocalDateTime nextSessionTime;
}