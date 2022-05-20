package com.fjm.utils.validate;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-25 下午4:04
 * @Description: 验证码生成
 */
public interface ValidateCodeGenerator {
    /**
     * 生成验证码
     *
     * @param request 请求
     * @return 生成结果
     */
    String generate(ServletWebRequest request);
}
