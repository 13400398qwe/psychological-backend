package com.example.scupsychological.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
// 定义 OpenAPI 的基本信息，并声明一个全局的安全需求
@OpenAPIDefinition(
        info = @Info(title = "心理咨询平台 API", description = "本文档描述了平台的所有后端接口", version = "1.0"),
        security = @SecurityRequirement(name = "bearerAuth")
)
// 定义一个名为 "bearerAuth" 的安全方案
@SecurityScheme(
        name = "bearerAuth", // 这个名字将在 @SecurityRequirement 中被引用
        type = SecuritySchemeType.HTTP, // 类型为 HTTP
        scheme = "bearer", // 认证方案为 Bearer
        bearerFormat = "JWT" // 提示这是一个 JWT Token
)
public class OpenApiConfig {
    // 这个类不需要任何内容，它的作用就是承载上面的注解
}
