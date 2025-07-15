package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class InitialVisitCreateDto {
    @NotNull
    private Long scheduleSlotId;

    @NotEmpty(message = "问卷答案不能为空")
    // key 是题目ID, value 是学生的选择 (A/B/C/D)
    private Map<Long, String> answers;
}