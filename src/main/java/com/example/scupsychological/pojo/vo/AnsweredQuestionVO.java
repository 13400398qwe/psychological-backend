package com.example.scupsychological.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "单个已回答问题的视图对象")
public class AnsweredQuestionVO {

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "题目的完整文字内容")
    private String questionText;

    @Schema(description = "题目类型 (SINGLE_CHOICE, TEXT等)")
    private String questionType;

    @Schema(description = "学生提交的原始答案值 (如 'YES' 或文本内容)")
    private Object studentAnswerValue;

    @Schema(description = "【如果是选择题】学生所选选项的【显示文本】(如 '是')")
    private String studentAnswerText;

    @Schema(description = "该题目是否为高危指标")
    private Boolean isCrisisIndicator;
}