package com.example.scupsychological.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 单次咨询记录表
 * </p>
 *
 * @author tpj
 * @since 2025-06-24
 */
@Getter
@Setter
@TableName("counseling_sessions")
public class CounselingSessions implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long caseId;
    private Long scheduleSlotId; // <-- 新增字段
    private Integer sessionNumber;

    /**
     * 【修正】预定的咨询【开始】时间
     */
    private LocalDateTime startTime;

    /**
     * 【新增】预定的咨询【结束】时间
     * 这个时间可以根据“单次时长”配置自动计算得出 (startTime + duration)。
     */
    private LocalDateTime endTime;

    private String location;
    private String status;
    private String counselorNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "CounselingSessions{" +
                "id=" + id +
                ", caseId=" + caseId +
                ", sessionNumber=" + sessionNumber +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", counselorNotes='" + counselorNotes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
