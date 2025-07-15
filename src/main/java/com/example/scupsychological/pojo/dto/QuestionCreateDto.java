package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "单个题目的创建信息")
public class QuestionCreateDto {


    @NotBlank(message = "题目内容不能为空")
    @Schema(description = "题目的具体文字内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "在过去的两周里，您是否时常感到情绪低落？")
    private String questionText;

}