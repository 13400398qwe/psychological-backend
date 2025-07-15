package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ExtensionApprovalDto {
    @Schema(description = "管理员审批的备注信息（特别是拒绝时）")
    private String adminNotes;
}

