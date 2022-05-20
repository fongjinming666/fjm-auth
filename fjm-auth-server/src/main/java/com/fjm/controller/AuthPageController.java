package com.fjm.controller;

import com.fjm.constants.FromOauthLoginConstant;
import com.fjm.emun.ApiResult;
import com.fjm.model.ApiData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-09-23 下午5:50
 * @Description:
 */
@Slf4j
@Controller
@SessionAttributes({"authorizationRequest"})
@Api(tags = "AuthPageController",description = "oauth2.0协议的页面跳转")
public class AuthPageController {

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /**
     * 当用户没登录的时候，会经过这个请求，在这个请求中可以处理一些逻辑
     *
     * @param request  request
     * @param response response
     * @return ResultModel
     * @throws IOException IOException
     */
    @ApiOperation(value = "第三授权登陆页面跳转")
    @RequestMapping(FromOauthLoginConstant.LOGIN_PAGE)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ApiData requireAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (null != savedRequest) {
            String targetUrl = savedRequest.getRedirectUrl();
            log.info("引发跳转的请求是:" + targetUrl);
            redirectStrategy.sendRedirect(request, response, FromOauthLoginConstant.AFTER_LOGING_PAGE);
        }
        /** 如果访问的是接口资源. */
        return ApiData.error(ApiResult.FORBIDDEN, "访问的服务需要身份认证，请引导用户到登录页");
    }

    @RequestMapping(FromOauthLoginConstant.AFTER_LOGING_URL)
    public String login() {
        return FromOauthLoginConstant.AFTER_LOGING_PAGE;
    }

    @RequestMapping(FromOauthLoginConstant.AFTER_GRANT_URL)
    public String grant() {
        return FromOauthLoginConstant.AFTER_GRANT_PAGE;
    }

    @RequestMapping(FromOauthLoginConstant.AFTER_LOGOUT_URL)
    public String logout() {
        return FromOauthLoginConstant.AFTER_LOGOUT_PAGE;
    }

    /**
     * 自定义授权页面，注意：一定要在类上加@SessionAttributes({"authorizationRequest"})
     *
     * @param model   model
     * @param request request
     * @return String
     * @throws Exception Exception
     */
    @ApiOperation(value = "自定义授权页面")
    @RequestMapping("/oauth/confirm_access")
    public String getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, String> scopes = (Map<String, String>) (model.containsKey("scopes") ? model.get("scopes") : request.getAttribute("scopes"));
        List<String> scopeList = new ArrayList<>();
        if (scopes != null) {
            scopeList.addAll(scopes.keySet());
        }
        model.put("scopeList", scopeList);
        return FromOauthLoginConstant.AFTER_GRANT_PAGE;
    }
}
