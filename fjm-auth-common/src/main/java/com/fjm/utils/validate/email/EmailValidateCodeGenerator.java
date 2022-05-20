package com.fjm.utils.validate.email;

import com.fjm.utils.validate.RandomCode;
import com.fjm.utils.validate.ValidateCodeGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-25 下午4:08
 * @Description: 邮箱验证码生成器
 */
@Component
public class EmailValidateCodeGenerator implements ValidateCodeGenerator {

    @Override
    public String generate(ServletWebRequest request) {
        return RandomCode.random(6);
    }

}
