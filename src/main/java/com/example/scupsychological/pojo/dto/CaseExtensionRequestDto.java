package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "咨询师申请追加咨询次数的请求体")
public class CaseExtensionRequestDto {
    @NotNull(message = "必须指定要追加的次数")
    @Min(value = 1, message = "追加次数至少为1")
    @Schema(description = "希望追加的咨询次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    private Integer additionalSessions;

    @NotBlank(message = "必须提供申请理由")
    @Schema(description = "申请追加的专业理由")
    private String reason;
}
