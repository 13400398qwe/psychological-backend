package com.example.scupsychological.sse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseConnectionManager {

    // 使用 ConcurrentHashMap 来保证线程安全
    // Key: 用户的唯一标识 (例如 userId)
    // Value: 该用户对应的 SseEmitter 连接对象
    private final Map<String, SseEmitter> connections = new ConcurrentHashMap<>();

    // 添加一个新连接
    public void add(String userId, SseEmitter emitter) {
        this.connections.put(userId, emitter);
    }

    // 移除一个连接
    public void remove(String userId) {
        this.connections.remove(userId);
    }

    // 根据用户ID获取连接
    public SseEmitter get(String userId) {
        return this.connections.get(userId);
    }

}
