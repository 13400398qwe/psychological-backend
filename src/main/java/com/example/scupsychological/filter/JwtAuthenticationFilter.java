package com.example.scupsychological.filter;

import com.example.scupsychological.security.LoginUser;
import com.example.scupsychological.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT 认证过滤器
 * 这个过滤器会在每个请求到达 Controller 之前运行，用于校验 Token。
 */
@Component
@Slf4j
@RequiredArgsConstructor // 使用 Lombok 自动生成构造函数
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate stringRedisTemplate; // 2. 注入 RedisTemplate
    public static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist:";
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 1. 从 HTTP 请求头中获取 Token
        String authHeader = request.getHeader("Authorization");

        // 如果请求头为空，或者不以 "Bearer " 开头，说明这个请求没有携带有效的 Token，直接放行。
        // 后续的 Spring Security 过滤器会处理这种情况（通常会判定为未认证）。
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 2. 提取 Token 字符串 (去掉 "Bearer " 前缀)
        String token = authHeader.substring(7);

        // 3. 使用 JwtUtil 校验 Token 并解析出 Claims
        Claims claims = jwtUtil.validateAndParseToken(token);

        // 如果 claims 为 null，说明 Token 无效（签名错误、过期、在黑名单中等），直接放行。
        // 同样，后续的 Spring Security 过滤器会认为这是一个无效的认证尝试。
        if (claims == null) {
            filterChain.doFilter(request, response);
            return;
        }
        // --- 3. 【核心改造】增加黑名单校验 ---
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(BLACKLIST_KEY_PREFIX + token))) {
            log.warn("检测到已登出的Token ({}) 正在尝试访问，已拒绝。", token);
            // 直接放行，后续的 Spring Security 过滤器会因上下文中没有认证信息而拒绝访问
            filterChain.doFilter(request, response);
            return;
        }
        // 4. Token 校验成功，从 Claims 中解析出用户信息
        Long userId = jwtUtil.getUserId(claims);
        String username = jwtUtil.getUsername(claims);
        String role = jwtUtil.getRole(claims);

        // 5. 将用户信息封装成 Spring Security 的 Authentication 对象
        // 这一步是关键！我们在这里手动构建一个已认证的用户凭证。
        // 参数说明：
        // principal: 主要信息，可以是用户名，也可以是用户对象。
        // credentials: 证书/密码，对于已认证的 Token，我们不需要密码，所以设为 null。
        // authorities: 权限集合，这里我们将角色字符串转换为权限对象。
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
        LoginUser loginUser = new LoginUser(userId, username, authorities);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginUser,
                null,
                authorities
        );

        // 6. 将 Authentication 对象存入 SecurityContextHolder
        // SecurityContextHolder 是 Spring Security 的安全上下文，后续的授权检查会从这里获取当前用户信息。
        // 这一步完成后，就相当于告诉 Spring Security：“这个用户已经成功认证了！”
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 7. 放行请求，让它继续访问后续的过滤器和 Controller
        filterChain.doFilter(request, response);

    }
}
