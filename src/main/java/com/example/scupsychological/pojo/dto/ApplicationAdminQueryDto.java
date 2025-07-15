package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "管理员分页查询初访申请的参数")
public class ApplicationAdminQueryDto {

    @Schema(description = "页码, 从1开始", defaultValue = "1")
    private long pageNum = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    private long pageSize = 10;
    @Schema(description = "按学生账户模糊搜索")
    private String studentUserName;

    @Schema(description = "按学生姓名模糊搜索")
    private String studentName;

    @Schema(description = "按初访员姓名模糊搜索")
    private String interviewerName;

    @Schema(description = "按申请状态筛选 (SUBMITTED, APPROVED, COMPLETED, CANCELED)")
    private String status;

    @Schema(description = "筛选紧急申请")
    private Boolean isUrgent;
}
