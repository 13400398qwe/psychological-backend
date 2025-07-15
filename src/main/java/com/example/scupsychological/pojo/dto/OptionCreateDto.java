package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "创建单个题目选项的请求体")
public class OptionCreateDto {

    @NotBlank(message = "选项的唯一值不能为空")
    @Schema(description = "选项的唯一标识值 (提交给后端的值)", requiredMode = Schema.RequiredMode.REQUIRED, example = "A")
    private String value;

    @NotBlank(message = "选项的显示文本不能为空")
    @Schema(description = "选项展示给用户的文本", requiredMode = Schema.RequiredMode.REQUIRED, example = "完全没有")
    private String text;

    @NotNull(message = "选项分数不能为空")
    @Schema(description = "该选项对应的分数，用于后端评估", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer score;
}
