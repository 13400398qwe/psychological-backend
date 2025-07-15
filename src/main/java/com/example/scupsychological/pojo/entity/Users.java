package com.example.scupsychological.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.example.scupsychological.common.enums.Gender;
import com.example.scupsychological.common.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author tpj
 * @since 2025-06-21
 */
@Setter
@Getter
public class Users implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录名 (学号/工号)
     */
    private String username;

    /**
     * 加密后的密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 用户角色
     */
    private Role role;
    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 院系 (学生专属)
     */
    private String college;

    /**
     * 逻辑删除
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
        return "Users{" +
            "id = " + id +
            ", username = " + username +
            ", password = " + password +
            ", name = " + name +
            ", role = " + role +
            ", phone = " + phone +
            ", gender = " + gender +
            ", college = " + college +
            ", delFlag = " + isDeleted +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
        "}";
    }
}
