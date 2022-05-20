package com.fjm.authentication;

import com.fjm.mobile.SmsCodeAuthenticationToken;
import com.fjm.service.oauth.SecurityUserService;
import lombok.Data;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-09-24 上午9:43
 * @Description:
 */
@Data
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

    private SecurityUserService securityUserService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;

        /** 校验  username 区号+手机号. */
        UserDetails user = securityUserService.loadUserByUsername((String) authenticationToken.getPrincipal());
        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        /** 已认证成功. */
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(user, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        /** 该SmsCodeAuthenticationProvider仅支持SmsCodeAuthenticationToken的token认证. */
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
