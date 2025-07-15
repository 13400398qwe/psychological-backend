package com.example.scupsychological.pojo.vo;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CaseReportContentVO {
    @Schema(description = "个案ID")
    private Long caseId;
    @Schema(description = "结案报告内容 (将作为真正的JSON对象返回)")
    @JsonRawValue // 2. 在字段上添加注解
    private String reportContent;
}
