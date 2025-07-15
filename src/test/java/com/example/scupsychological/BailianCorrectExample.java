package com.example.scupsychological;

import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;

import java.util.Scanner;

public class BailianCorrectExample {

    // 定义 AI 服务接口
    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        // 1. 使用 DashscopeChatModel.builder() 创建模型实例
        // 默认会从环境变量 DASHSCOPE_API_KEY 读取密钥
        ChatLanguageModel model = QwenChatModel.builder()
                .apiKey("sk-**********************") // 也可以直接传入 apiKey
                .modelName("qwen-max") // 指定需要调用的模型，如 qwen-max, qwen-turbo 等
                .build();

        // 2. 使用 AiServices 创建一个简单的助手
        Assistant assistant = AiServices.create(Assistant.class, model);

        Scanner scanner = new Scanner(System.in);
        System.out.println("你好，我是你的AI助手，我们来聊天吧！(输入 'exit' 退出)");

        while (true) {
            System.out.print("You: ");
            String userMessage = scanner.nextLine();

            if ("exit".equalsIgnoreCase(userMessage)) {
                break;
            }

            String aiMessage = assistant.chat(userMessage);
            System.out.println("AI: " + aiMessage);
        }
        scanner.close();
    }
}