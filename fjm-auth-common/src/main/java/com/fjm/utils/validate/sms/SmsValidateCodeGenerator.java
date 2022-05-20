package com.fjm.utils.validate.sms;

import com.fjm.utils.validate.RandomCode;
import com.fjm.utils.validate.ValidateCodeGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-25 下午4:07
 * @Description: 手机验证码生成器
 */
@Component
public class SmsValidateCodeGenerator implements ValidateCodeGenerator {

    @Override
    public String generate(ServletWebRequest request) {
        // 定义手机验证码生成策略，可以使用 request 中从请求动态获取生成策略
        // 可以从配置文件中读取生成策略
        return RandomCode.random(4, true);
    }

}
