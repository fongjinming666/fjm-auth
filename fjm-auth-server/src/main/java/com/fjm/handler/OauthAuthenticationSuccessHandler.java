package com.fjm.handler;

import com.fjm.domain.AuthConstant;
import com.fjm.mapper.CustomMapper;
import com.fjm.service.oauth.OauthClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.MapUtils;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-09-24 上午9:58
 * @Description:
 */
@Slf4j
@Component
public class OauthAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Resource
    private OauthClientService oauthClientService;

    @Resource
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private CustomMapper customMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String type = request.getHeader("Accept");
        log.info("登录成功之后的处理type:{}",type);
        if (!type.contains("text/html")) {
            log.info("我进来了");
            String clientId = request.getParameter(AuthConstant.PARAMETERS_CLIENT_ID);
            String clientSecret = request.getParameter(AuthConstant.PARAMETERS_CLIENT_SECRET);

            ClientDetails clientDetails = oauthClientService.loadClientByClientId(clientId);
            if (null == clientDetails) {
                throw new UnapprovedClientAuthenticationException("clientId不存在" + clientId);
            } else if (!StringUtils.equals(clientDetails.getClientSecret(), passwordEncoder.encode(clientSecret))) {
                throw new UnapprovedClientAuthenticationException("clientSecret不匹配" + clientId);
            }

            TokenRequest tokenRequest = new TokenRequest(new HashMap<>(), clientId, clientDetails.getScope(), "custom");
            OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
            OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
            OAuth2AccessToken token = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
            response.setContentType("application/json;charset=UTF-8");
            log.info("OauthAuthenticationSuccessHandler,登陆成功后返回参数:{}",token);
            response.getWriter().write(customMapper.writeValueAsString(token));
        } else {
            log.info("我出来了");
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
