package com.example.scupsychological.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.entity.Notification;
import com.example.scupsychological.security.LoginUser;
import com.example.scupsychological.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "消息通知模块")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "分页获取我的通知列表")
    public Result<Page<Notification>> listMyNotifications(
            @AuthenticationPrincipal LoginUser loginUser) {

        Page<Notification> resultPage = new Page<>();
        notificationService.findMyNotifications(loginUser.getUserId(), resultPage);
        return Result.success(resultPage);
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "将通知标记为已读")
    public Result<Object> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal LoginUser loginUser) {

        notificationService.markNotificationAsRead(loginUser.getUserId(), id);
        return Result.success("标记成功");
    }
}