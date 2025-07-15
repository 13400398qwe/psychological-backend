package com.example.scupsychological.pojo.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 问卷评估结果的数据传输对象
 * <p>
 * 这个类不与任何数据库表对应，它只是一个临时的、用于封装业务方法返回值的容器。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor // 提供一个包含所有参数的构造函数，方便快速创建实例
public class EvaluationResult {

    /**
     * 系统根据规则计算出的总分
     */
    private int score;

    /**
     * 系统根据规则判断出的紧急状态 (true 代表紧急)
     */
    private boolean isUrgent;
}