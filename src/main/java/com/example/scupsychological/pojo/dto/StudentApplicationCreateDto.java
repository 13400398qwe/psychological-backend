package com.example.scupsychological.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Data
@Schema(description = "学生提交初访申请时使用的数据")

public class StudentApplicationCreateDto{

    @NotNull(message = "必须选择预约日期")
    @Schema(description = "学生希望预约的日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-07-01")
    private LocalDate requestedDate;

    @NotNull(message = "必须选择预约时间")
    @JsonFormat(pattern = "H:mm")
    @Schema(description = "学生希望预约的时间点", requiredMode = Schema.RequiredMode.REQUIRED, example = "14:00")
    private LocalTime requestedTime;
    @Schema(description = "预约的咨询师ID")
    private Long assignedInterviewerId;
    @Schema(description = "预约的时间段ID")
    private Long scheduleSlotId;

    @NotNull(message = "问卷答案不能为空")
    @Schema(description = "问卷答案")
    private Map<Long, Long> answers;
}
