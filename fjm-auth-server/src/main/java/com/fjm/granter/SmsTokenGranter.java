package com.fjm.granter;

import com.fjm.service.oauth.SecurityUserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-25 下午4:49
 * @Description: 短信授权模式
 */
public class SmsTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "sms";

    private SecurityUserService securityUserService;

    /**
     * 构造方法提供一些必要的注入的参数
     * 通过这些参数来完成我们父类的构建
     *
     * @param tokenServices        tokenServices
     * @param clientDetailsService clientDetailsService
     * @param oAuth2RequestFactory oAuth2RequestFactory
     * @param securityUserService  securityUserService
     */
    public SmsTokenGranter(AuthorizationServerTokenServices tokenServices,
                           ClientDetailsService clientDetailsService,
                           OAuth2RequestFactory oAuth2RequestFactory,
                           SecurityUserService securityUserService) {
        super(tokenServices, clientDetailsService, oAuth2RequestFactory, GRANT_TYPE);
        this.securityUserService = securityUserService;
    }

    /**
     * 在这里查询我们用户，构建用户的授权信息
     *
     * @param client       客户端
     * @param tokenRequest tokenRequest
     * @return OAuth2Authentication
     */
    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> params = tokenRequest.getRequestParameters();
        String phone = params.getOrDefault("sms", "");
        UserDetails userDetails = securityUserService.loadUserByPhone(phone);

        Authentication user = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                userDetails.getPassword(), userDetails.getAuthorities());
        return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), user);
    }
}
