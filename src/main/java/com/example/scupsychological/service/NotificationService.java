package com.example.scupsychological.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.entity.Notification;

public interface NotificationService {
    void createAndSendNotification(Long recipientId, String content, String type);

    Page<Notification> findMyNotifications(Long userId, Page<Notification> page);

    void markNotificationAsRead(Long userId, Long id);
}
