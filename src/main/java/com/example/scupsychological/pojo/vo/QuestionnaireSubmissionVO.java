package com.example.scupsychological.pojo.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "问卷提交记录的详细视图对象，用于管理员审核")
public class QuestionnaireSubmissionVO {

    @Schema(description = "提交记录的唯一ID")
    private Long id;

    @Schema(description = "系统根据规则自动计算出的总分")
    private Integer calculatedScore;

    @Schema(description = "问卷完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    @Schema(description = "详细的问答列表，包含了问题和学生的具体回答")
    private List<AnsweredQuestionVO> answeredQuestions;
}

