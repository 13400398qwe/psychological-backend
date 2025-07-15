package com.example.scupsychological.pojo.vo; // 建议放在 vo 或 dto 包下

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 问卷评估结果的数据传输对象
 * 用于封装一次自动化评估的核心产出。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireEvaluationResult {

    /**
     * 系统根据规则计算出的总分
     */
    private int score;

    /**
     * 系统根据规则判断出的紧急状态 (true 代表紧急)
     */
    private boolean isUrgent;

    /**
     * 系统根据分数范围评估出的危机等级 (例如：高、中、低)
     * 这个字段可以由初访员在后续人工评估时进行修正。
     */
    private String crisisLevel;
}