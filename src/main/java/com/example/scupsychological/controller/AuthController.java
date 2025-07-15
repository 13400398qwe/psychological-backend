package com.example.scupsychological.controller;

import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.LoginDTO;
import com.example.scupsychological.pojo.vo.LoginResponseVO;
import com.example.scupsychological.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "用户模块 - 登录与登出")
@Slf4j
public class AuthController {
    private final UserService userService;
    @Operation(summary = "用户登录", description = "用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {

        log.info("员工登录：{}", loginDTO);
        LoginResponseVO loginResponseVO = userService.login(loginDTO);
        if(loginResponseVO == null)
            return Result.error("登录失败");
        return Result.success(loginResponseVO);
    }


    @Operation(summary = "用户登出", description = "将当前用户的JWT加入黑名单，使其失效")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String authHeader) {


        if (authHeader == null)
            return Result.error("请先登录");
        String token = authHeader.substring(7);
        log.info("员工登出：{}", token);
        userService.logout(token);
        return Result.success("登出成功");
    }
}
