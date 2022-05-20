package com.fjm.config;

import com.fjm.dao.IWechatAuthDao;
import com.fjm.granter.AppleTokenGranter;
import com.fjm.granter.MobTokenGranter;
import com.fjm.granter.SmsTokenGranter;
import com.fjm.granter.WechatTokenGranter;
import com.fjm.service.oauth.OauthAuthorizationCodeServices;
import com.fjm.service.oauth.OauthClientService;
import com.fjm.service.oauth.SecurityUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-05-20 上午11:39
 * @Description:
 */
@Configuration
public class TokenGranterConfig {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private OauthClientService oauthClientService;

    @Resource
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Resource
    private OauthAuthorizationCodeServices oauthAuthorizationCodeServices;

    @Resource
    private OAuth2RequestFactory oAuth2RequestFactory;

    @Resource
    private SecurityUserService securityUserService;

    @Resource
    private IWechatAuthDao wechatAuthDao;

    private TokenGranter tokenGranter;

    @Bean("tokenGranter")
    public TokenGranter tokenGranter() {
        if (null == tokenGranter) {
            tokenGranter = new TokenGranter() {
                private CompositeTokenGranter delegate;

                @Override
                public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                    if (delegate == null) {
                        delegate = new CompositeTokenGranter(tokenGranters());
                    }
                    return delegate.grant(grantType, tokenRequest);
                }
            };
        }
        return tokenGranter;
    }

    public List<TokenGranter> tokenGranters() {
        List<TokenGranter> granters = new ArrayList<>();
        //授权码模式
        granters.add(new AuthorizationCodeTokenGranter(authorizationServerTokenServices, oauthAuthorizationCodeServices, oauthClientService, oAuth2RequestFactory));
        //refresh模式
        granters.add(new RefreshTokenGranter(authorizationServerTokenServices, oauthClientService, oAuth2RequestFactory));
        //简化模式
        ImplicitTokenGranter implicit = new ImplicitTokenGranter(authorizationServerTokenServices, oauthClientService, oAuth2RequestFactory);
        granters.add(implicit);
        //客户端模式
        granters.add(new ClientCredentialsTokenGranter(authorizationServerTokenServices, oauthClientService, oAuth2RequestFactory));

        if (authenticationManager != null) {
            //密码模式
            granters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, authorizationServerTokenServices, oauthClientService, oAuth2RequestFactory));
        }
        granters.addAll(Arrays.asList(
                //短信登录
                new SmsTokenGranter(authorizationServerTokenServices, oauthClientService,
                        oAuth2RequestFactory, securityUserService),
                //一键登录
                new MobTokenGranter(authorizationServerTokenServices, oauthClientService,
                        oAuth2RequestFactory, securityUserService),
                //微信授权登录
                new WechatTokenGranter(authorizationServerTokenServices, oauthClientService,
                        oAuth2RequestFactory, securityUserService, wechatAuthDao),
                //苹果授权登录
                new AppleTokenGranter(authorizationServerTokenServices, oauthClientService,
                        oAuth2RequestFactory, securityUserService)
        ));
        return granters;
    }
}
