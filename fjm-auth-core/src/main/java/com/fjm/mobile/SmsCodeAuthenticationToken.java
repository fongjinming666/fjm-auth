package com.fjm.mobile;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-09-24 上午9:41
 * @Description:
 */
public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    public SmsCodeAuthenticationToken(String username) {
        //因为刚开始并没有认证，因此用户没有任何权限，并且设置没有认证的信息（setAuthenticated(false)）
        super(null);
        /** 这里的principal就是username = 区号+手机号. */
        this.principal = username;
        this.setAuthenticated(false);
    }

    public SmsCodeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
