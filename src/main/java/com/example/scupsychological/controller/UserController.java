package com.example.scupsychological.controller;

import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.ChangePasswordDTO;
import com.example.scupsychological.pojo.dto.UserUpdateRequestDto;
import com.example.scupsychological.pojo.vo.UserVO;
import com.example.scupsychological.security.LoginUser;
import com.example.scupsychological.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "用户模块 - 用户信息管理")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传或更新当前用户头像")
    public Result<Map<String, String>> uploadAvatar(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "要上传的头像文件", required = true) @RequestParam("file") MultipartFile file) {
        // 从 Spring Security 上下文中获取当前登录用户的ID
        Long currentUserId = loginUser.getUserId(); // 例如: StpUtil.getLoginIdAsLong(); 或从 SecurityContextHolder 获取

        // 调用 Service 完成上传和数据库更新
        String newAvatarUrl = userService.updateAvatar(currentUserId, file);

        Map<String, String> responseData = Map.of("avatarUrl", newAvatarUrl);
        return Result.success(responseData, "头像上传成功");
    }
    @Operation(summary = "修改密码")
    @PostMapping("/changePassword")
    public Result changePassword(@AuthenticationPrincipal LoginUser loginUser, @RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(loginUser.getUserId(), changePasswordDTO);
        return Result.success("密码修改成功");
    }

}
