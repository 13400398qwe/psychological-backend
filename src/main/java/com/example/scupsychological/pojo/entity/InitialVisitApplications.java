package com.example.scupsychological.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 初访预约申请表
 * </p>
 *
 * @author tpj
 * @since 2025-06-24
 */
@Getter
@Setter
@TableName("initial_visit_applications")
public class InitialVisitApplications implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 初访申请ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请学生ID
     */
    private Long studentId;

    /**
     * 预约的时间段ID
     */
    private Long scheduleSlotId;

    /**
     * 申请状态
     */
    private String status;

    /**
     * 是否紧急(系统评估结果)
     */
    private Boolean isUrgent;

    /**
     * 指派的初访员ID
     */
    private Long assignedInterviewerId;

    private LocalDateTime requestedTime;
    /**
     * 问题快照
     */
    private String questionnaireContent;

    /**
     * 问卷得分
     */
    private Integer calculatedScore;
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
        return "InitialVisitApplications{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", scheduleSlotId=" + scheduleSlotId +
                ", status='" + status + '\'' +
                ", isUrgent=" + isUrgent +
                ", assignedInterviewerId=" + assignedInterviewerId +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
