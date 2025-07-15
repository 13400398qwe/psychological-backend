package com.example.scupsychological.pojo.vo;


import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "咨询师工作量统计视图对象，用于Excel导出")
public class CounselorWorkloadVO {

    @ExcelProperty("咨询师姓名") // EasyExcel 注解，指定Excel中的列名
    @Schema(description = "咨询师姓名")
    private String counselorName;

    @ExcelProperty("完成咨询人次")
    @Schema(description = "已完成的咨询总人次（总session数）")
    private Long totalCompletedSessions;

    @ExcelProperty("咨询总时长（分钟）")
    @Schema(description = "所有已完成咨询的总时长，单位：分钟")
    private Long totalMinutes;
}