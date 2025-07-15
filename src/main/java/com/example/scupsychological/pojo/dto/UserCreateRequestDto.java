package com.example.scupsychological.pojo.dto; // Or dto package

import com.example.scupsychological.common.enums.Gender;
import com.example.scupsychological.common.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "管理员创建用户请求体")
public class UserCreateRequestDto {

    @NotBlank(message = "登录账号不能为空")

    @Size(min = 4, max = 20, message = "登录账号长度必须在4到20位之间")

    @Schema(description = "登录账号 (工号)", requiredMode = Schema.RequiredMode.REQUIRED, example = "counselor001")
    private String username;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "用户真实姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张老师")
    private String name;

    @NotBlank(message = "初始密码不能为空")
    @Size(min = 8, message = "密码长度至少为8位")
    @Schema(description = "用户初始密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "InitialPassword123")
    private String password;

    @NotNull(message = "角色不能为空")
    @Schema(description = "分配的角色 (ADMIN, COUNSELOR, INTERVIEWER, ASSISTANT)", requiredMode = Schema.RequiredMode.REQUIRED, example = "COUNSELOR")
    private Role role;
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;
    @Schema(description = "性别", example = "MALE")
    private Gender gender;
    @Schema(description = "学院", example = "软件学院")
    private String college;
}