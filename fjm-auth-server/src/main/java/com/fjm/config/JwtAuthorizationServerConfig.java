package com.fjm.config;

import com.fjm.service.oauth.OauthAuthorizationCodeServices;
import com.fjm.service.oauth.OauthClientService;
import com.fjm.service.oauth.SecurityUserService;
import com.fjm.store.OauthApprovalStore;
import com.fjm.store.OauthTokenStore;
import com.fjm.translator.OauthExceptionTranslator;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-04-23 下午3:09
 * @Description: 授权服务器配置
 */
@Configuration
//@AllArgsConstructor
@EnableAuthorizationServer   //注解开启了验证服务器
public class JwtAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private SecurityUserService securityUserService;

    @Resource
    private OauthClientService oauthClientService;

    @Resource
    private OAuth2RequestFactory oAuth2RequestFactory;

    @Resource
    private OauthTokenStore tokenStore;

    @Resource
    private OauthAuthorizationCodeServices oauthAuthorizationCodeServices;

    @Resource
    private OauthApprovalStore oauthApprovalStore;

    @Resource
    private OauthExceptionTranslator oauthExceptionTranslator;

    @Resource
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Resource
    private TokenEnhancer tokenEnhancer;

    @Resource
    private TokenGranter tokenGranter;


    /**
     * 配置 token 节点的安全策略 允许表单认证
     *
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        /** 实现oauth2.0 token策略. */
        security.allowFormAuthenticationForClients();
        //security.addTokenEndpointAuthenticationFilter(integrationAuthenticationFilter);
        /*security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");*/
    }

    @Override
    @SneakyThrows
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(this.oauthClientService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(this.tokenEnhancer);
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        endpoints.authenticationManager(this.authenticationManager) // 开启密码验证，来源于 WebSecurityConfigurerAdapter
                .userDetailsService(this.securityUserService) // 读取验证用户的信息
                .requestFactory(this.oAuth2RequestFactory)

                //.tokenEnhancer(tokenEnhancerChain)
                //原mongo逻辑
                .authorizationCodeServices(this.oauthAuthorizationCodeServices)
                .tokenStore(this.tokenStore)
                .approvalStore(this.oauthApprovalStore)
                .tokenServices(this.authorizationServerTokenServices)
                /** 特殊性tokenGrander 20210520. */
                .tokenGranter(this.tokenGranter)
                //.tokenGranter(tokenGranters(endpoints))
                .exceptionTranslator(this.oauthExceptionTranslator)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET)
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }
}
