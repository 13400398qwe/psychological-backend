package com.example.scupsychological.common.enums; // 建议放在一个 enums 包下

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 性别枚举
 * <p>
 * MyBatis-Plus 默认会将枚举的名称 (e.g., "MALE") 作为字符串存入数据库，
 * 这与 MySQL 的 ENUM('MALE', 'FEMALE', 'OTHER') 类型完美匹配。
 */
@Getter
public enum Gender {

    /**
     * 男
     */
    MALE("男"),

    /**
     * 女
     */
    FEMALE("女"),

    /**
     * 其他 (为多样性提供选项)
     */
    OTHER("其他"),

    /**
     * 保密 (用户选择不透露)
     */
    SECRET("保密");

    /**
     * 性别的中文描述
     */
    private final String description;

    Gender(String description) {
        this.description = description;
    }

    /**
     * 在序列化为 JSON 或用于数据库存储时，我们使用枚举的名称 (如 "MALE")。
     * @JsonValue 确保了返回给前端的也是这个字符串值。
     *
     * @return 性别的枚举名，例如 "MALE"
     */
    @JsonValue
    public String getValue() {
        return this.name();
    }
}