package com.fjm.filter;

import com.fjm.mapper.CustomMapper;
import com.fjm.utils.validate.ValidateCodeProcessorHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-25 下午4:21
 * @Description: 验证码过滤器 确保在一次请求只通过一次filter
 */
@Slf4j
@Component
public class ValidateCodeGranterFilter extends OncePerRequestFilter {

    @Resource
    private CustomMapper customMapper;

    @Resource
    private ValidateCodeProcessorHolder validateCodeProcessorHolder;

    private RequestMatcher requestMatcherGet = new AntPathRequestMatcher("/oauth/token", HttpMethod.GET.name());
    private RequestMatcher requestMatcherPost = new AntPathRequestMatcher("/oauth/token", HttpMethod.POST.name());

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            if (requestMatcherGet.matches(request) || requestMatcherPost.matches(request)) {
                String grantType = getGrantType(request);
                if ("sms".equalsIgnoreCase(grantType) || "email".equalsIgnoreCase(grantType)) {
                    log.info("请求需要验证！验证请求：" + request.getRequestURI() + " 验证类型：" + grantType);
                    validateCodeProcessorHolder.findValidateCodeProcessor(grantType)
                            .validate(new ServletWebRequest(request, response));
                }
            }
        } catch (Exception e) {
            log.error("doFilterInternal error", e);
        }
        filterChain.doFilter(request, response);
    }


    private String getGrantType(HttpServletRequest request) {
        return request.getParameter("grant_type");
    }
}
