package com.fjm.authentication;

import com.fjm.constants.FromOauthLoginConstant;
import com.fjm.emun.ApiResult;
import com.fjm.exception.BadRequestException;
import com.fjm.mobile.SmsCodeAuthenticationToken;
import com.fjm.utils.ValidationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-09-24 上午10:06
 * @Description:
 */
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * 请求中，参数为smsCode
     */

    private String smsCodeParameter = FromOauthLoginConstant.SMSCODE_KEY;
    /**
     * 请求中，参数为mobile
     */
    private String mobileParameter = FromOauthLoginConstant.MOBILE_KEY;

    /**
     * 是否只处理post请求
     */
    private boolean postOnly = true;

    public SmsCodeAuthenticationFilter() {
        //要拦截的请求
        super(new AntPathRequestMatcher("/authentication/mobile", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            /** 默认中国大陆. */
            String username = "";
            String smsCode = this.obtainSmsCode(request);
            smsCode = StringUtils.isBlank(smsCode) ? "86" : smsCode;
            String mobile = this.obtainMobile(request);
            if (StringUtils.isBlank(mobile)) {
                mobile = "";
            }
            /**手机号校验 默认中国大陆手机号校验. */
            boolean isPhonelegal = ValidationUtils.isPhoneNumberValid(mobile, smsCode);
            if (!isPhonelegal) {
                throw new BadRequestException(ApiResult.INVALID_MOBILE_PHONE_NUMBER);
            }

            username = smsCode.trim() + mobile.trim();
            /** 把username传进SmsCodeAuthenticationToken. */
            SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(username);
            this.setDetails(request, authRequest);
            /**调用AuthenticationManager. */
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    /**
     * 获取区号
     *
     * @param request request
     * @return String
     */
    private String obtainSmsCode(HttpServletRequest request) {
        return request.getParameter(this.smsCodeParameter);
    }

    /**
     * 获取手机号
     *
     * @param request request
     * @return String
     */
    private String obtainMobile(HttpServletRequest request) {
        return request.getParameter(this.mobileParameter);
    }

    private void setDetails(HttpServletRequest request, SmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setSmsCodeParameter(String smsCodeParameter) {
        Assert.hasText(mobileParameter, "SmsCode parameter must not be empty or null");
        this.smsCodeParameter = smsCodeParameter;
    }

    public void setMobileParameter(String mobileParameter) {
        Assert.hasText(mobileParameter, "Mobile parameter must not be empty or null");
        this.mobileParameter = mobileParameter;
    }


    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getUsernameParameter() {
        return this.mobileParameter;
    }
}
