package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "咨询师提交结案报告的请求体")
public class CaseReportSubmitDto {

    @NotNull(message = "必须关联一个咨询个案ID")
    @Schema(description = "要结案的个案ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long caseId;

    // 将报告的所有字段封装在一个 Map 或一个专门的 ReportContentDto 中
    // 这与我们数据库的 JSON 字段设计完美匹配
    @NotNull(message = "报告内容不能为空")
    @Schema(description = "包含所有报告字段的JSON对象")
    private Map<String, Object> reportContent;
}
