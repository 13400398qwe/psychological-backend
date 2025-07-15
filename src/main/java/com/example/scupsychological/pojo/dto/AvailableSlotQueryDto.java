package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@Schema(description = "学生查询可用号源的参数")
public class AvailableSlotQueryDto {
    @Schema(description = "按咨询师筛选（可选，查询指定咨询师的号源）")
    private Long staffId;

    @Schema(description = "按日期筛选（可选，查询指定日期的号源）")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate queryDate;
}