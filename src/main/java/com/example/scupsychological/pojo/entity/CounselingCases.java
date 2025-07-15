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
 * 正式咨询个案表
 * </p>
 *
 * @author tpj
 * @since 2025-06-24
 */
@Getter
@Setter
@TableName("counseling_cases")
public class CounselingCases implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 咨询个案ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 咨询师ID
     */
    private Long counselorId;
    /**
     * 心理助理id
     */
    private Long assistantId;

    /**
     * 来源的初访记录ID
     */
    private Long initialVisitRecordId;

    /**
     * 个案状态
     */
    private String status;

    /**
     * 总咨询次数
     */
    private Integer totalSessions;

    /**
     * 结案报告内容(JSON)
     */
    private String reportContent;

    /**
     * 结案报告完成时间
     */
    private LocalDateTime reportFinalizedAt;

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
        return "CounselingCases{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", counselorId=" + counselorId +
                ", initialVisitRecordId=" + initialVisitRecordId +
                ", status='" + status + '\'' +
                ", totalSessions=" + totalSessions +
                ", reportContent='" + reportContent + '\'' +
                ", reportFinalizedAt=" + reportFinalizedAt +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
