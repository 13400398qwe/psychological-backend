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
 * 问卷题目表
 * </p>
 *
 * @author tpj
 * @since 2025-06-24
 */
@Getter
@Setter
@TableName("questions")
public class Questions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 题目内容
     */
    private String questionText;

    /**
     * 题目类型
     */
    private String questionType;


    /**
     * 是否为高危指标
     */
    private Boolean isCrisisIndicator;
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
        return "QuestionnaireQuestions{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", questionType='" + questionType + '\'' +
                ", isCrisisIndicator=" + isCrisisIndicator +
                '}';
    }
}
