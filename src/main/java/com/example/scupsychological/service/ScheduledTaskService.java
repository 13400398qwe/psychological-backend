package com.example.scupsychological.service;

import com.example.scupsychological.mapper.ScheduleSlotsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 存放所有定时任务的组件
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTaskService {

    private final ScheduleSlotsMapper slotMapper;

    /**
     * 清理过期号源的定时任务
     * cron 表达式 "0 0 2 * * ?" 表示 "每天凌晨2点整" 执行
     *
     * cron表达式详解:
     * 第一个 * (秒) -> 0
     * 第二个 * (分) -> 0
     * 第三个 * (时) -> 2
     * 第四个 * (日) -> * (任意)
     * 第五个 * (月) -> * (任意)
     * 第六个 * (周) -> ? (不关心)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredSlots() {
        log.info("【定时任务】开始执行：清理已过期的可用号源...");

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 调用我们自定义的 Mapper 方法执行物理删除
        int deletedRows = slotMapper.physicallyDeleteExpiredAvailableSlots(now);

        log.info("【定时任务】执行完毕：共清理了 {} 条过期号源。", deletedRows);
    }
}

