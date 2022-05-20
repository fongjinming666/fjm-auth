package com.fjm.handler;

import com.fjm.constants.FromOauthLoginConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-09-24 下午3:12
 * @Description:
 */
@Slf4j
@Component
public class OauthLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        log.info("退出成功");
        httpServletResponse.sendRedirect(FromOauthLoginConstant.AFTER_LOGOUT_PAGE);
    }
}
