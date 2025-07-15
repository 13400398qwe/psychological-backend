package com.example.scupsychological.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户角色枚举 (适配数据库 ENUM 字符串类型)
 * <p>
 * MyBatis-Plus 默认会将枚举的名称 (e.g., "ADMIN") 作为字符串存入数据库，
 * 这与 MySQL 的 ENUM('ADMIN', 'STUDENT', ...) 类型完美匹配。
 * 因此，我们不再需要实现 IEnum 接口。
 */
@Getter
public enum Role {

    /**
     * 学生
     */
    STUDENT(5,"学生"),

    /**
     * 初访员
     */
    VISITOR(4,"初访员"),

    /**
     * 心理助理
     */
    ASSISTANT(3,"心理助理"),

    /**
     * 咨询师
     */
    COUNSELOR(2,"咨询师"),

    /**
     * 中心管理员
     */
    ADMIN(1,"中心管理员");

    /**
     * 角色的中文描述
     */
    private final String description;
    private  final  int code;

    Role(int code,String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * [Spring Security & Jackson]
     * 在序列化为 JSON 或用于权限判断时，我们使用枚举的名称 (如 "ADMIN")。
     * @JsonValue 确保了返回给前端的也是这个字符串值。
     *
     * @return 角色的枚举名，例如 "ADMIN"
     */
    @JsonValue
    public String getValue() {
        return this.name();
    }
}
