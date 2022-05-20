package com.fjm.config;

import com.fjm.authentication.OauthSmsCodeLoginFilter;
import com.fjm.authentication.SmsCodeAuthenticationSecurityConfig;
import com.fjm.constants.FromOauthLoginConstant;
import com.fjm.filter.ValidateCodeGranterFilter;
import com.fjm.firewall.CustomStrictHttpFirewall;
import com.fjm.mapper.CustomMapper;
import com.fjm.service.RedisService;
import com.fjm.service.oauth.SecurityUserService;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.firewall.HttpFirewall;

import javax.annotation.Resource;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 下午5:24
 * @Description:
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private RedisService redisService;

    @Resource
    private CustomMapper customMapper;

    @Resource
    private ValidateCodeGranterFilter validateCodeGranterFilter;

    @Resource
    private AuthenticationSuccessHandler oauthAuthenticationSuccessHandler;

    @Resource
    private AuthenticationFailureHandler oauthAuthenticationFailureHandler;

    @Resource
    private LogoutSuccessHandler oauthLogoutSuccessHandler;

    @Resource
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    /**
     * 密码编码验证器
     *
     * @return
     */
    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("authenticationManager")
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean("securityUserService")
    public SecurityUserService securityUserService() {
        return new SecurityUserService();
    }

    /**
     * 创建允许在URL中使用斜线的自定义防火墙
     *
     * @return
     */
    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        CustomStrictHttpFirewall firewall = new CustomStrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers("/static/**").and().httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        OauthSmsCodeLoginFilter oauthSmsCodeLoginFilter = new OauthSmsCodeLoginFilter(redisService);
        oauthSmsCodeLoginFilter.setAuthenticationFailureHandler(oauthAuthenticationFailureHandler);
        oauthSmsCodeLoginFilter.setCustomMapper(customMapper);
        oauthSmsCodeLoginFilter.afterPropertiesSet();

        httpSecurity
                /** 增加最前置日志过滤filter. */
                //.addFilterAfter(logFilter, ChannelProcessingFilter.class)
                .addFilterBefore(oauthSmsCodeLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(validateCodeGranterFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .authorizeRequests().requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .and().formLogin().loginPage(FromOauthLoginConstant.LOGIN_PAGE).loginProcessingUrl(FromOauthLoginConstant.LOGIN_PROCESSING_URL)
                .successHandler(oauthAuthenticationSuccessHandler)
                .failureHandler(oauthAuthenticationFailureHandler)
                .and().logout().logoutUrl(FromOauthLoginConstant.LOGOUT_PAGE).logoutSuccessHandler(oauthLogoutSuccessHandler).deleteCookies("JSESSIONID")
                .and().authorizeRequests().antMatchers(
                FromOauthLoginConstant.LOGIN_PAGE, FromOauthLoginConstant.LOGIN_PROCESSING_URL, FromOauthLoginConstant.LOGOUT_PAGE,
                FromOauthLoginConstant.AFTER_LOGING_URL, FromOauthLoginConstant.AFTER_GRANT_URL, FromOauthLoginConstant.AFTER_LOGOUT_URL
                //,FromOauthLoginConstant.AFTER_HOME_PAGE,"/"
                , "/oauth/rsa/publicKey", "/oauth/login/code", "/oauth/authorize_token", "/oauth/revokeToken", "/validate/code/**").permitAll()
                //.anyRequest().authenticated()
                .and().csrf().disable()
                .apply(smsCodeAuthenticationSecurityConfig);
    }
}
