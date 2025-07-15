package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "生成号源请求体")
public class SlotGenerationRequestDto {
    @NotNull
    @Schema(description = "生成号源的开始日期")
    private LocalDate startDate;

    @NotNull @Schema(description = "生成号源的结束日期")
    private LocalDate endDate;
}
