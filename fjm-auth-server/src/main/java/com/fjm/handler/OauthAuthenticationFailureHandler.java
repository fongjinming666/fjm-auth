package com.fjm.handler;


import com.fjm.emun.ApiResult;
import com.fjm.mapper.CustomMapper;
import com.fjm.mobile.ValidateOauthCodeException;
import com.fjm.model.ApiData;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-09-24 上午11:23
 * @Description:
 */
@Component
public class OauthAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Resource
    private CustomMapper customMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        if (e instanceof ValidateOauthCodeException) {
            ValidateOauthCodeException exception = (ValidateOauthCodeException) e;
            httpServletResponse.setStatus((int) exception.getResult().getValue());
            httpServletResponse.getOutputStream().println(customMapper.writeValueAsString(ApiData.error(exception.getResult(), exception.getMessage())));
        } else {
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.getOutputStream().println(customMapper.writeValueAsString(ApiData.error(ApiResult.SERVER_ERROR, e.getMessage())));
        }
    }
}
