package com.example.scupsychological.pojo.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "待审批的加时申请视图对象")
public class PendingExtensionRequestVO {

    @Schema(description = "加时申请的ID")
    private Long requestId;

    @Schema(description = "关联的咨询个案ID")
    private Long caseId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "发起申请的咨询师姓名")
    private String counselorName;

    @Schema(description = "申请追加的咨询次数")
    private Integer requestedSessions;

    @Schema(description = "申请理由")
    private String reason;

    @Schema(description = "申请提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
}
