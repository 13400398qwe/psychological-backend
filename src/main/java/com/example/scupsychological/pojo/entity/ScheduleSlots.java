package com.example.scupsychological.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 可预约时间段表
 * </p>
 *
 * @author tpj
 * @since 2025-06-24
 */
@Getter
@Setter
@TableName("schedule_slots")
public class ScheduleSlots implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 可预约时间段ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的员工ID
     */
    private Long staffId;

    /**
     * 号源开始时间
     */
    private LocalDateTime startTime;

    /**
     * 号源结束时间
     */
    private LocalDateTime endTime;

    /**
     * 咨询地点
     */
    private String location;

    /**
     * 状态
     */
    private String status;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "ScheduleSlots{" +
                "id=" + id +
                ", staffId=" + staffId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
