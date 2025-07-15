package com.example.scupsychological.service;

import com.example.scupsychological.pojo.entity.InitialVisitApplications;
import com.example.scupsychological.pojo.entity.ScheduleSlots;
import com.example.scupsychological.pojo.entity.Users;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor

public class NotificationService {

    private final SmsService smsService; // 注入我们之前设计的短信发送底层服务
    private final ObjectMapper objectMapper;

    @Value("${aliyun.sms.template-code.notify}")
    private String approvalTemplateCode;

    /**
     * 发送“初访预约已批准”的通知给学生
     */
    public void sendAppointmentApprovedSmsToStudent(Users student, Users interviewer) {
        if (student == null || student.getPhone() == null) return;

        try {
            // 根据您的测试模板来构建参数。
            // 假设测试模板的变量是 ${name} 和 ${time}
            Map<String, String> params = Map.of(
                    "code", "心理咨询平台"+student.getName()
            );
            String templateParamJson = objectMapper.writeValueAsString(params);
            // 调用真实发送服务
            smsService.sendMessage(student.getPhone(), approvalTemplateCode, templateParamJson);

        } catch (JsonProcessingException e) {
            log.error("构建学生通知短信参数失败", e);
        }
    }
}
