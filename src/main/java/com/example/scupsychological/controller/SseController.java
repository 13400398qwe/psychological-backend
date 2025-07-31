package com.example.scupsychological.controller;

import com.example.scupsychological.security.LoginUser;
import com.example.scupsychological.sse.SseConnectionManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Tag(name = "实时通知模块 (SSE)")
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseConnectionManager sseConnectionManager;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "建立 SSE 长连接用于接收实时通知")
    public SseEmitter connect(@AuthenticationPrincipal LoginUser loginUser) {
        String userId = String.valueOf(loginUser.getUserId());

        // 1. 创建一个新的 SseEmitter，可以设置一个较长的超时时间，例如1小时
        SseEmitter emitter = new SseEmitter(3600_000L);

        // 2. 将这个连接存入我们的管理器中
        sseConnectionManager.add(userId, emitter);
        log.info("用户 {} 建立了 SSE 连接", userId);

        // 3. 注册回调：当连接完成（包括超时或浏览器关闭）时，从管理器中移除
        emitter.onCompletion(() -> {
            log.info("用户 {} 的 SSE 连接已关闭 (onCompletion)", userId);
            sseConnectionManager.remove(userId);
        });

        // 4. 注册回调：当连接超时时
        emitter.onTimeout(() -> {
            log.warn("用户 {} 的 SSE 连接已超时 (onTimeout)", userId);
            emitter.complete(); // 主动完成连接
            // onCompletion 回调会自动被触发，所以无需在这里 remove
        });

        // 5. 发送一个初始的“心跳”或“连接成功”消息，确认连接已建立
        try {
            emitter.send(SseEmitter.event().name("connected").data("连接成功！"));
        } catch (IOException e) {
            log.error("向用户 {} 发送初始 SSE 事件失败", userId, e);
            emitter.completeWithError(e);
        }

        return emitter;
    }
}
