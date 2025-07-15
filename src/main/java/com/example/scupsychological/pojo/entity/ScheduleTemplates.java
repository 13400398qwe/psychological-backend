package com.example.scupsychological.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.scupsychological.common.enums.RecurrenceType;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 值班模板表
 * </p>
 *
 * @author tpj
 * @since 2025-06-24
 */
@Getter
@Setter
@TableName("schedule_templates")
public class ScheduleTemplates implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 值班模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的员工ID
     */
    private Long staffId;

    /**
     * 重复类型(目前仅支持按周)
     */
    private RecurrenceType recurrenceType;

    /**
     * 星期几(1-7, 1代表周一)
     */
    private Integer dayOfWeek;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 单次时长(分钟)
     */
    private Integer slotDurationMinutes;

    /**
     * 默认地点
     */
    private String location;

    /**
     * 模板生效日期
     */
    private LocalDate effectiveFrom;

    /**
     * 模板失效日期
     */
    private LocalDate effectiveTo;

    /**
     * 是否激活
     */
    private Boolean isActive;

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
        return "ScheduleTemplates{" +
                "id=" + id +
                ", staffId=" + staffId +
                ", recurrenceType='" + recurrenceType + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", slotDurationMinutes=" + slotDurationMinutes +
                ", location='" + location + '\'' +
                ", effectiveFrom=" + effectiveFrom +
                ", effectiveTo=" + effectiveTo +
                ", isActive=" + isActive +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
