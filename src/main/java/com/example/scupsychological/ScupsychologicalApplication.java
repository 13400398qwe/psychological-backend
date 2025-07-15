package com.example.scupsychological;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.example.scupsychological.mapper")
@SpringBootApplication
@EnableScheduling // 2. 开启定时任务功能
public class ScupsychologicalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScupsychologicalApplication.class, args);
    }

}
