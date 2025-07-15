package com.example.scupsychological.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "心理助理创建新个案并安排首次咨询的请求体")
public class CaseScheduleDto {

    @NotNull(message = "必须关联一个初访记录ID")
    @Schema(description = "要转为个案的初访记录ID (initial_visit_records.id)")
    private Long initialVisitRecordId;

    @NotNull(message = "必须指派一名咨询师")
    @Schema(description = "负责该个案的咨询师ID")
    private Long counselorId;

    @NotNull(message = "必须指定首次咨询的开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "首次咨询的开始时间")
    private LocalDateTime firstSessionStartTime;

    @NotBlank(message = "必须指定咨询地点")
    @Schema(description = "咨询地点")
    private String location;

    @Schema(description = "预设的咨询总次数", defaultValue = "8")
    private Integer totalSessions = 8;
}
