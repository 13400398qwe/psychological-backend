package com.example.scupsychological.pojo.dto;


import com.example.scupsychological.common.enums.Gender;
import com.example.scupsychological.common.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "管理员更新用户信息的请求体")
public class UserUpdateRequestDto {

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "用户真实姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "李老师")
    private String name;

    @NotNull(message = "角色不能为空")
    @Schema(description = "分配的角色", requiredMode = Schema.RequiredMode.REQUIRED, example = "COUNSELOR")
    private Role role;

    @Schema(description = "联系电话", example = "13900139000")
    private String phone;

    @Schema(description = "性别", example = "FEMALE")
    private Gender gender;

    @Schema(description = "院系（如果是学生）", example = "计算机学院")
    private String college;

    @Schema(description = "用户密码", example = "12345678")
    private String password;
    //
    @NotNull(message = "账号状态不能为空")
    @Schema(description = "账号是否激活", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean isActive;
}
