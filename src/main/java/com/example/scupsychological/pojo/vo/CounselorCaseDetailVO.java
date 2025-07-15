package com.example.scupsychological.pojo.vo;


import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;




@Data
@Schema(description = "咨询师查看的个案完整详情视图对象")
public class CounselorCaseDetailVO {
    @Schema(description = "个案ID") private Long caseId;
    @Schema(description = "学生姓名") private String studentName;
    @Schema(description = "学生学号") private String studentUsername;
    @Schema(description = "学生联系电话") private String studentPhone;
    @Schema(description = "个案状态") private String status;
    @Schema(description = "总咨询次数") private Integer totalSessions;
    @Schema(description = "来源的初访记录中的问题类型") private String problemType;
    @Schema(description = "结案报告内容 (原始JSON格式)") @JsonRawValue private String reportContent;
    @Schema(description = "该个案的所有单次咨询安排列表") private List<CounselingSessionVO> sessions;
}