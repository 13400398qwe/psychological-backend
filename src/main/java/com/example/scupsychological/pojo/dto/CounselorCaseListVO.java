package com.example.scupsychological.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "咨询师个案列表的摘要视图对象")
public class CounselorCaseListVO {
    @Schema(description = "个案ID") private Long caseId;
    @Schema(description = "学生姓名") private String studentName;
    @Schema(description = "个案当前状态") private String status;
    @Schema(description = "已完成/总咨询次数", example = "3/8") private String sessionProgress;
    @Schema(description = "下一次咨询时间") @JsonFormat(pattern = "yyyy-MM-dd HH:mm") private LocalDateTime nextSessionTime;
}
