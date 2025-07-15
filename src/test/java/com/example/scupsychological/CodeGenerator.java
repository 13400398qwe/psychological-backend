package com.example.scupsychological;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class CodeGenerator {

    public static void main(String[] args) {
        // 数据库连接信息
        final String URL = "jdbc:mysql://localhost:3306/psychological?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
        final String USERNAME = "root";
        final String PASSWORD = "134398"; // 你的密码

        // 项目路径
        final String projectPath = System.getProperty("user.dir");

        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                .globalConfig(builder -> {
                    builder.author("tpj")
                            .outputDir(projectPath + "/src/main/java")
                            .disableOpenDir();
                })
                .packageConfig(builder -> {
                    builder.parent("com.example.scupsychological")
                            .entity("entity")
                            .mapper("mapper")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/src/main/resources/mapper"));
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.entityBuilder()
                            .enableLombok()
                            .build();

                    builder.controllerBuilder()
                            .enableRestStyle()
                            .build();

                    builder.serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            .build();

                    builder.mapperBuilder()
                            .enableMapperAnnotation()
                            .build();
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}