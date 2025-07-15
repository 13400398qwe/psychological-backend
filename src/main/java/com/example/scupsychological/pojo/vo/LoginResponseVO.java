package com.example.scupsychological.pojo.vo;

import com.example.scupsychological.common.enums.Gender;
import com.example.scupsychological.common.enums.Role;
import lombok.Data;

@Data
public class LoginResponseVO {

    /**
     * JWT 认证令牌
     */
    private String token;

    /**
     * 用户基础信息
     */
    private UserInfoVO userInfo;
    // 内部静态类，用于封装用户信息
    @Data
    public static class UserInfoVO {
        private Long id;
        private String username;
        private String name;
        private Role role;
        private Gender gender;
        private String phone;
        private String avatar;
        // private List<String> permissions; // 可选的权限列表
    }
}
