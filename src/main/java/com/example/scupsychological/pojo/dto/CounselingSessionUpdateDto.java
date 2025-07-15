package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "咨询师更新单次咨询记录的请求体")
public class CounselingSessionUpdateDto {

    @NotBlank(message = "必须提供本次咨询的状态")
    @Schema(description = "咨询状态 (COMPLETED, NO_SHOW, LEAVE, CANCELED)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status; // 使用枚举更佳

    @Schema(description = "咨询师为本次咨询添加的专业备注")
    private String counselorNotes;
}