package com.example.scupsychological.pojo.vo;

import com.example.scupsychological.common.enums.Gender;
import com.example.scupsychological.common.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "返回给前端的用户信息视图对象")
public class UserVO {

    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "登录账号")
    private String username;
    @Schema(description = "真实姓名")
    private String name;
    @Schema(description = "角色")
    private Role role;
    @Schema(description = "联系电话")
    private String phone;
    @Schema(description = "用户头像")
    private String avatar;
    @Schema(description = "学院")
    private String college;
    @Schema(description = "性别")
    private Gender gender;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
