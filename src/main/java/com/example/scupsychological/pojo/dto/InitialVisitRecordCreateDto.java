package com.example.scupsychological.pojo.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "初访员录入初访记录的请求体")
public class InitialVisitRecordCreateDto {

    @NotNull(message = "必须关联一个初访申请ID")
    @Schema(description = "对应的初访申请ID (initial_visit_applications.id)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long applicationId;

    @NotBlank(message = "危机等级不能为空")
    @Schema(description = "危机等级评估结果 (例如：高、中、低)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String crisisLevel;

    @NotBlank(message = "问题类型不能为空")
    @Schema(description = "对学生问题类型的初步判断", requiredMode = Schema.RequiredMode.REQUIRED)
    private String problemType;

    @NotNull(message = "实际初访时间不能为空")
    @Schema(description = "实际进行初访的时间点", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime visitTime;

    @NotBlank(message = "必须给出初访结论")
    @Schema(description = "初访结论 (NO_COUNSELING, ARRANGE_COUNSELING, REFERRAL)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String conclusion;

    @Schema(description = "初访员的详细备注、观察记录或专业意见")
    private String notes;
}