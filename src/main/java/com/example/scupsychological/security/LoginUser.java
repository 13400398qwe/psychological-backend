package com.example.scupsychological.security; // 建议放在一个新的 security 包下

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 自定义的、用于 Spring Security 认证的用户详情对象。
 * 它将作为我们新的 Principal。
 */
@Getter
public class LoginUser implements UserDetails {

    // 我们需要携带的核心信息
    private final Long userId;
    private final String username;
    private final List<GrantedAuthority> authorities;

    // UserDetails 接口要求的一些字段
    private final boolean enabled;
    private final String password = null; // 认证通过后，密码不再需要，设为 null

    public LoginUser(Long userId, String username, List<GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
        this.enabled = true; // 默认账户可用
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    // 以下方法根据您的业务需求实现，这里为了简单都返回 true
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}