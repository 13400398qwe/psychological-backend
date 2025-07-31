package com.example.scupsychological.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notifications")
public class Notification {
    private Long id;
    private Long recipientId; // 接收者ID
    private String content;   // 通知内容
    private String type;
    private Long relatedEntityId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}

