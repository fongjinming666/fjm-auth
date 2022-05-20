package com.fjm.granter;

import com.aliyuncs.DefaultAcsClient;
import com.fjm.emun.ApiResult;
import com.fjm.exception.BadRequestException;
import com.fjm.service.oauth.SecurityUserService;
import com.fjm.utils.sms.SmsUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-25 下午5:20
 * @Description: 一键登录授权模式
 */
public class MobTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "one_key";

    private SecurityUserService securityUserService;

    private DefaultAcsClient defaultAcsClient;

    /**
     * 构造方法提供一些必要的注入的参数
     * 通过这些参数来完成我们父类的构建
     *
     * @param tokenServices
     * @param clientDetailsService
     * @param oAuth2RequestFactory
     * @param securityUserService
     * @param defaultAcsClient
     */
    public MobTokenGranter(AuthorizationServerTokenServices tokenServices,
                           ClientDetailsService clientDetailsService,
                           OAuth2RequestFactory oAuth2RequestFactory,
                           SecurityUserService securityUserService,
                           DefaultAcsClient defaultAcsClient) {
        super(tokenServices, clientDetailsService, oAuth2RequestFactory, GRANT_TYPE);
        this.securityUserService = securityUserService;
        this.defaultAcsClient = defaultAcsClient;
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
        //String clientId = params.getOrDefault("client_id", "");
        String mobAccessToken = params.getOrDefault("mob_access_token", "");
        /** 实现一键登录获取phone. */
        String phone = null;
        try {
            phone = SmsUtil.queyMobile(defaultAcsClient, mobAccessToken);
        } catch (Exception e) {
            throw new BadRequestException(ApiResult.ONEKEY_LOGIN_FAILED, "一键登录获取手机号失败");
        }
        UserDetails userDetails = securityUserService.loadUserByPhone(phone);

        Authentication user = new UsernamePasswordAuthenticationToken(userDetails,
                userDetails.getPassword(), userDetails.getAuthorities());
        return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), user);
    }
}
