package com.example.scupsychological.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "申请详情视图对象")
public class ApplicationDetailVO extends ApplicationListVO {
    // 继承列表VO的所有字段
    private String studentPhone;
    private String studentCollege;
    private String interviewerPhone;
    // 还可以包含完整的问卷提交VO等
    // private QuestionnaireSubmissionVO submission;
}