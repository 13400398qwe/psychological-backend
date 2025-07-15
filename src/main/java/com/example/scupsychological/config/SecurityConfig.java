package com.example.scupsychological.config;

import com.example.scupsychological.filter.JwtAuthenticationFilter;
import com.example.scupsychological.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 启用方法级别的权限注解
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF
                .csrf(csrf -> csrf.disable())
                // 2. 设置 Session 管理策略为无状态 (STATELESS)，因为我们用JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 公开访问路径：登录、注册等
                        .requestMatchers("/api/auth/login").permitAll()
                        // Swagger 文档路径 (如果使用)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                );

        // 4. 添加自定义的 JWT 过滤器到 Spring Security 过滤器链中
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}