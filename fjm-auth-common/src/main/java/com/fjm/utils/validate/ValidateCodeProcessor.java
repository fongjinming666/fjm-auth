package com.fjm.utils.validate;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-25 下午4:03
 * @Description: 验证码处理器
 */
public interface ValidateCodeProcessor {
    /**
     * 创建验证码
     *
     * @param request 请求
     * @throws Exception 异常
     */
    void create(ServletWebRequest request) throws Exception;

    /**
     * 验证验证码
     *
     * @param request 请求
     */
    void validate(ServletWebRequest request);
}
