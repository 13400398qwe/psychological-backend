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
 * 初访记录表
 * </p>
 *
 * @author tpj
 * @since 2025-06-24
 */
@Getter
@Setter
@TableName("initial_visit_records")
public class InitialVisitRecords implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 初访记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的初访申请ID
     */
    private Long applicationId;

    /**
     * 危机等级(人工评估)
     */
    private String crisisLevel;

    /**
     * 问题类型
     */
    private String problemType;

    /**
     * 初访结论
     */
    private String conclusion;

    /**
     * 初访员备注
     */
    private String notes;

    /**
     * 初访时间
     */
    private LocalDateTime visitTime;

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
        return "InitialVisitRecords{" +
                "id=" + id +
                ", applicationId=" + applicationId +
                ", crisisLevel='" + crisisLevel + '\'' +
                ", problemType='" + problemType + '\'' +
                ", conclusion='" + conclusion + '\'' +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
