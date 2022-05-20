package com.fjm.granter;

import com.fjm.emun.ApiResult;
import com.fjm.exception.BadRequestException;
import com.fjm.service.oauth.SecurityUserService;
import com.fjm.utils.AppleUtil;
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
 * @Description: 苹果授权模式
 */
public class AppleTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "apple";

    private SecurityUserService securityUserService;

    /**
     * 构造方法提供一些必要的注入的参数
     * 通过这些参数来完成我们父类的构建
     *
     * @param tokenServices
     * @param clientDetailsService
     * @param oAuth2RequestFactory
     * @param securityUserService
     */
    public AppleTokenGranter(AuthorizationServerTokenServices tokenServices,
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
        String identityToken = params.getOrDefault("identityToken", "");
        String appleUserId = params.getOrDefault("appleUserId", "");
        /** 实现苹果登录. */
        try {
            boolean appleAuthState = AppleUtil.verify(identityToken, appleUserId);
            if (!appleAuthState) {
                throw new BadRequestException(ApiResult.APPLE_AUTH_FAILED, "苹果第三方授权登录失败");
            }
        } catch (Exception e) {
            throw new BadRequestException(ApiResult.APPLE_AUTH_FAILED, "苹果第三方授权登录失败");
        }
        UserDetails userDetails = securityUserService.loadUserByAppleUserId(appleUserId);

        Authentication user = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                userDetails.getPassword(), userDetails.getAuthorities());
        return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), user);
    }
}
