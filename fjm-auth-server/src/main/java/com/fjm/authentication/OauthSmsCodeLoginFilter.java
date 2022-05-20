package com.fjm.authentication;

import com.fjm.constants.FromOauthLoginConstant;
import com.fjm.emun.ApiResult;
import com.fjm.mapper.CustomMapper;
import com.fjm.mobile.ValidateOauthCodeException;
import com.fjm.service.RedisService;
import com.fjm.utils.ValidationUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-09-24 上午10:21
 * @Description:
 */
@Slf4j
@Data
public class OauthSmsCodeLoginFilter extends OncePerRequestFilter implements InitializingBean {

    private AuthenticationFailureHandler authenticationFailureHandler;

    private Set<String> urls = new HashSet<>();

    private RedisService redisService;

    private CustomMapper customMapper;

    /**
     * 验证请求url与配置的url是否匹配的工具类
     */
    private AntPathMatcher pathMatcher = new AntPathMatcher();


    public OauthSmsCodeLoginFilter(RedisService redisService) {
        this.redisService = redisService;
    }


    /**
     * 初始化要拦截的url配置信息
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        //urls.add(FromOauthLoginConstant.LOGIN_MOBILE_PROCESSING_URL);
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        boolean action = false;
        for (String url : urls) {
            if (pathMatcher.match(url, request.getRequestURI())) {
                action = true;
            }
        }

        if (action) {
            try {
                redisValidate(new ServletWebRequest(request));
            } catch (ValidateOauthCodeException e) {
                log.error("ValidateOauthCodeException", e);
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                //GolbalMvcExceptionHandlerUtils.writeFailedToResponse(response, e, customMapper);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void redisValidate(ServletWebRequest request) throws ServletRequestBindingException {
        String smsCode = request.getParameter(FromOauthLoginConstant.SMSCODE_KEY);
        smsCode = StringUtils.isBlank(smsCode) ? "86" : smsCode;
        String mobile = request.getParameter(FromOauthLoginConstant.MOBILE_KEY);
        if (StringUtils.isBlank(mobile)) {
            mobile = "";
        }
        log.info("origin smsCode is:{},origin mobile is :{}", smsCode, mobile);
        /** 从请求中拿到verificationCode这个参数. */
        String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), FromOauthLoginConstant.VERIFICATION_CODE);
        checkVerificationCode(smsCode, mobile, codeInRequest);
        /** 校验成功,删除验证码. */
        redisService.del(FromOauthLoginConstant.OAUTH_MOBILE_LOGIN_CODE + smsCode + mobile);
    }


    /**
     * 校验验证码等参数准确性
     *
     * @param smsCode
     * @param phone
     * @param verificationCode
     */
    public void checkVerificationCode(String smsCode, String phone, String verificationCode) {
        /**手机号校验 默认中国大陆手机号校验. */
        boolean isPhonelegal = ValidationUtils.isPhoneNumberValid(phone, smsCode);
        if (!isPhonelegal) {
            throw new ValidateOauthCodeException(ApiResult.INVALID_MOBILE_PHONE_NUMBER);
        }
        /** 验证码判断. */
        String codeInRedis = redisService.getString(FromOauthLoginConstant.OAUTH_MOBILE_LOGIN_CODE + smsCode + phone);
        log.info("oauth real sms verificationCode:{},codeInRedis:{}:", verificationCode, codeInRedis);
        if (StringUtils.isBlank(codeInRedis)) {
            throw new ValidateOauthCodeException(ApiResult.CODE_INVALID);
        } else if (!codeInRedis.equals(verificationCode)) {
            throw new ValidateOauthCodeException(ApiResult.AUTH_CODE_ERROR);
        }
    }
}
