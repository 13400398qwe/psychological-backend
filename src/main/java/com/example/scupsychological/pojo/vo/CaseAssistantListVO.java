package com.example.scupsychological.pojo.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "向心理助理展示的个案列表视图对象")
public class CaseAssistantListVO {

    @Schema(description = "个案ID")
    private Long caseId;
    @Schema(description = "学生姓名")
    private String studentName;
    @Schema(description = "负责的咨询师姓名")
    private String counselorName;
    @Schema(description = "咨询师电话")
    private String counselorPhone;
    @Schema(description = "学生电话")
    private String studentPhone;
    @Schema(description = "个案状态")
    private String status;
    @Schema(description = "咨询个案提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    @Schema(description = "已完成/总咨询次数", example = "3/8")
    private String sessionProgress;

    @Schema(description = "下一次咨询时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime nextSessionTime;
}
