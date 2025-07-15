package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "管理员分页查询待审批加时申请的参数")
public class PendingExtensionQueryDto {

    @Schema(description = "页码, 从1开始", defaultValue = "1")
    private long pageNum = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    private long pageSize = 10;

    @Schema(description = "按学生姓名或学号模糊搜索")
    private String studentKeyword;

    @Schema(description = "按发起申请的咨询师姓名模糊搜索")
    private String counselorName;
}
