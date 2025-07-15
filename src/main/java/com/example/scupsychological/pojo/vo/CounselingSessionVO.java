package com.example.scupsychological.pojo.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "单次咨询安排的视图对象")
public class CounselingSessionVO {

    @Schema(description = "单次咨询记录的ID")
    private Long sessionId;

    @Schema(description = "这是第几次咨询")
    private Integer sessionNumber;

    @Schema(description = "预定的咨询时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledTime;

    @Schema(description = "咨询地点")
    private String location;

    @Schema(description = "本次咨询的状态 (PENDING, COMPLETED, NO_SHOW, LEAVE)")
    private String status;
}