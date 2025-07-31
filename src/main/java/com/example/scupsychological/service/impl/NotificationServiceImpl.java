package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.mapper.NotificationMapper;
import com.example.scupsychological.pojo.entity.Notification;
import com.example.scupsychological.service.NotificationService;
import com.example.scupsychological.sse.SseConnectionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;
    private final SseConnectionManager sseConnectionManager; // 注入连接管理器
    private final ObjectMapper objectMapper; // 用于将对象转为JSON

    @Override
    public void createAndSendNotification(Long recipientId, String content, String type) {
        // 1. 创建并保存通知到数据库 (逻辑不变)
        Notification notification = new Notification();
        // ... 设置字段 ...
        notification.setRecipientId(recipientId);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(false);
        notificationMapper.insert(notification);

        // 2. 【核心】尝试通过 SSE 推送实时通知
        String userId = String.valueOf(recipientId);
        SseEmitter emitter = sseConnectionManager.get(userId);

        // 如果 emitter 不为 null，说明该用户当前在线并保持着连接
        if (emitter != null) {
            try {
                // 将完整的 notification 对象转换为 JSON 字符串
                String notificationJson = objectMapper.writeValueAsString(notification);

                // 发送一个名为 "new_notification" 的事件
                emitter.send(SseEmitter.event().name("new_notification").data(notificationJson));
                log.info("已成功向在线用户 {} 推送实时通知", userId);
            } catch (Exception e) {
                log.warn("向用户 {} 推送实时通知失败，可能是连接已断开", userId, e);
                // 推送失败时，可以考虑移除这个可能已失效的连接
                sseConnectionManager.remove(userId);
            }
        } else {
            log.info("用户 {} 当前不在线，通知已存入数据库，待其上线后拉取。", userId);
        }
    }
    @Override
    public Page<Notification> findMyNotifications(Long recipientId, Page<Notification> page) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.eq("recipient_id", recipientId);
        wrapper.orderByDesc("created_at");
        return notificationMapper.selectPage(page, wrapper);
    }

    @Override
    public void markNotificationAsRead(Long currentUserId, Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        // 安全校验：确保用户只能标记自己的消息
        if (notification == null || !notification.getRecipientId().equals(currentUserId)) {
            throw new AccessDeniedException("无权操作此通知");
        }

        notification.setIsRead(true);
        notificationMapper.updateById(notification);

        // (可选) 可以在这里通过 SSE/WebSocket 再次推送一个“未读数更新”的事件
    }
}
