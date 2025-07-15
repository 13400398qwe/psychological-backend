package com.example.scupsychological.service;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {

    // 从配置文件中注入 AccessKey ID
    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;

    // 从配置文件中注入 AccessKey Secret
    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;

    // 注入短信签名
    @Value("${aliyun.sms.sign-name}")
    private String signName;

    /**
     * 发送短信
     *
     * @param phoneNumber 目标手机号
     * @param templateCode 短信模板CODE
     * @param templateParamJson 模板参数的JSON字符串, 例如: "{\"code\":\"123456\"}"
     * @return true 如果发送成功, false 如果失败
     */
    public boolean sendMessage(String phoneNumber, String templateCode, String templateParamJson) {
        // 1. 创建阿里云客户端
        // 我们不在这里直接 new，而是通过一个静态方法创建，避免每次都创建新对象
        com.aliyun.dysmsapi20170525.Client client = createClient();
        if (client == null) {
            return false;
        }

        // 2. 构建发送请求
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phoneNumber)
                .setSignName(this.signName)
                .setTemplateCode(templateCode)
                .setTemplateParam(templateParamJson);

        try {
            // 3. 发起API调用
            SendSmsResponse response = client.sendSms(sendSmsRequest);

            // 4. 处理响应
            log.info("短信发送成功，响应: {}", response.getBody().getMessage());
            // 阿里云的成功响应码是 "OK"
            if ("OK".equals(response.getBody().getCode())) {
                return true;
            } else {
                log.error("短信发送失败，错误码: {}, 错误信息: {}", response.getBody().getCode(), response.getBody().getMessage());
                return false;
            }
        } catch (TeaException teaEx) {
            log.error("调用阿里云短信服务API时发生SDK异常: {}", teaEx.getMessage());
        } catch (Exception e) {
            log.error("发送短信时发生未知异常", e);
        }
        return false;
    }

    /**
     * 创建阿里云短信服务的客户端
     * @return Client 实例
     */
    private com.aliyun.dysmsapi20170525.Client createClient() {
        try {
            Config config = new Config()
                    // 您的 AccessKey ID
                    .setAccessKeyId(this.accessKeyId)
                    // 您的 AccessKey Secret
                    .setAccessKeySecret(this.accessKeySecret);
            // 访问的域名
            config.endpoint = "dysmsapi.aliyuncs.com";
            return new com.aliyun.dysmsapi20170525.Client(config);
        } catch (Exception e) {
            log.error("创建阿里云短信客户端失败", e);
            return null;
        }
    }
}