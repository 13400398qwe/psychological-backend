package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "管理员分页查询题目的参数")
public class QuestionQueryDto {

    @Schema(description = "页码, 从1开始", defaultValue = "1")
    private long pageNum = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    private long pageSize = 10;

    @Schema(description = "按题目内容模糊搜索")
    private String questionText;

    @Schema(description = "按是否高危指标筛选")
    private Boolean isCrisisIndicator;

    @Schema(description = "按是否激活筛选")
    private Boolean isActive;
}
