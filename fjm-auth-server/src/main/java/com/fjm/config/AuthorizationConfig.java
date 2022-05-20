package com.fjm.config;

import com.fjm.domain.AuthConstant;
import com.fjm.domain.User;
import com.fjm.service.oauth.OauthAuthorizationCodeServices;
import com.fjm.service.oauth.OauthAuthorizationTokenServices;
import com.fjm.service.oauth.OauthClientService;
import com.fjm.store.OauthApprovalStore;
import com.fjm.store.OauthTokenStore;
import com.fjm.translator.OauthExceptionTranslator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-05-26 下午1:45
 * @Description:
 */
@Configuration
public class AuthorizationConfig {

    /**
     * oauthClientService
     *
     * @return
     */
    @Bean("oauthClientService")
    public OauthClientService oauthClientService() {
        return new OauthClientService();
    }

    /**
     * OAuth2RequestFactory的默认实现，它初始化参数映射中的字段，
     * 验证授权类型(grant_type)和范围(scope)，并使用客户端的默认值填充范围(scope)（如果缺少这些值）。
     *
     * @return
     */
    @Bean("oAuth2RequestFactory")
    public OAuth2RequestFactory requestFactory() {
        return new DefaultOAuth2RequestFactory(oauthClientService());
    }

    /**
     * tokenStore
     *
     * @return
     */
    @Bean("tokenStore")
    public OauthTokenStore tokenStore() {
        return new OauthTokenStore();
    }

    @Bean("oauthAuthorizationCodeServices")
    public OauthAuthorizationCodeServices oauthAuthorizationCodeServices() {
        return new OauthAuthorizationCodeServices();
    }

    /**
     * approvalStore
     *
     * @return
     */
    @Bean("oauthApprovalStore")
    public OauthApprovalStore oauthApprovalStore() {
        return new OauthApprovalStore();
    }


    @Bean("oauthExceptionTranslator")
    public OauthExceptionTranslator uacExceptionTranslator() {
        return new OauthExceptionTranslator();
    }

    @Bean("authorizationServerTokenServices")
    public AuthorizationServerTokenServices authorizationServerTokenServices() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(tokenEnhancer());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        OauthAuthorizationTokenServices oauthAuthorizationTokenServices = new OauthAuthorizationTokenServices();
        oauthAuthorizationTokenServices.setTokenStore(tokenStore());
        oauthAuthorizationTokenServices.setSupportRefreshToken(true);
        oauthAuthorizationTokenServices.setReuseRefreshToken(false);
        oauthAuthorizationTokenServices.setClientDetailsService(oauthClientService());
        oauthAuthorizationTokenServices.setTokenEnhancer(tokenEnhancerChain);
        return oauthAuthorizationTokenServices;
    }

    /**
     * JWT内容增强
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> map = new HashMap<>(6);
            map.put(AuthConstant.JWT_CLIENT_ID_KEY, authentication.getOAuth2Request().getClientId());
            if (authentication.getUserAuthentication() == null) {
                return accessToken;
            }
            if (authentication.getUserAuthentication().getPrincipal() == null) {
                return accessToken;
            }
            User user = (User) authentication.getUserAuthentication().getPrincipal();
            user.setClientId(authentication.getOAuth2Request().getClientId());
            map.put(AuthConstant.JWT_USER_ID_KEY, user.getId());
            map.put(AuthConstant.JWT_USER_NAME_KEY, user.getUsername());
            map.put(AuthConstant.JWT_USER_SMSCODE_KEY, StringUtils.isBlank(user.getSmsCode()) ? "86" : user.getSmsCode());
            map.put(AuthConstant.JWT_USER_PHONE_KEY, user.getPhone());
            map.put(AuthConstant.JWT_USER_SOURCE_KEY, StringUtils.isBlank(user.getSource()) ? "-1" : user.getSource());
            //map.put("oldVeePermissions", user.getOldVeePermissions());
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
            return accessToken;
        };
    }
}
