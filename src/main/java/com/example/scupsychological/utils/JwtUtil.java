package com.example.scupsychological.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretString;

    @Value("${jwt.expiration-millis}")
    private long expirationMillis;

    @Value("${jwt.issuer}")
    private String issuer;

    private SecretKey secretKey;

    /**
     * 在依赖注入完成后，初始化 SecretKey
     */
    @PostConstruct
    public void init() {
        // 将配置文件中的字符串密钥转换为 SecretKey 对象
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 根据用户信息生成 JWT
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色
     * @return 生成的 JWT 字符串
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .issuer(issuer) // 签发者
                .issuedAt(now) // 签发时间
                .expiration(expirationDate) // 过期时间
                .subject(username) // 主题，通常是用户名
                .claim("userId", userId) // 自定义 Claim：存放用户ID
                .claim("role", role) // 自定义 Claim：存放角色
                .signWith(secretKey) // 使用 SecretKey 对象进行签名
                .compact();
    }

    /**
     * 校验 JWT 并解析出 Claims (载荷)
     * @param token JWT 字符串
     * @return 解析出的 Claims 对象。如果校验失败，则返回 null。
     */
    public Claims validateAndParseToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parser() // 获取解析器
                    .verifyWith(secretKey) // 使用密钥进行验签
                    .build()
                    .parseSignedClaims(token); // 解析 token

            // 校验签发者 (如果需要)
            if (!issuer.equals(jws.getPayload().getIssuer())) {
                // log.warn("JWT issuer mismatch. Expected: {}, but got: {}", issuer, jws.getPayload().getIssuer());
                return null;
            }

            return jws.getPayload(); // 返回载荷部分
        } catch (ExpiredJwtException e) {
            // log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            // log.warn("JWT token is malformed: {}", e.getMessage());
        } catch (SecurityException e) {
            // log.warn("JWT signature validation failed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return null; // 校验失败返回 null
    }

    /**
     * 从 Claims 中获取用户ID
     * @param claims 载荷对象
     * @return 用户ID
     */
    public Long getUserId(Claims claims) {
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Claims 中获取用户名
     * @param claims 载荷对象
     * @return 用户名 (即 Subject)
     */
    public String getUsername(Claims claims) {
        return claims.getSubject();
    }

    /**
     * 从 Claims 中获取角色
     * @param claims 载荷对象
     * @return 角色
     */
    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }
    /**
     * 【新增】从 Claims 中获取 JTI
     */
    public String getJti(Claims claims) {
        return claims.getId();
    }
}