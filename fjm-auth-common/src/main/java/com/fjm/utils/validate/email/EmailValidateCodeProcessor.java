package com.fjm.utils.validate.email;

import com.fjm.utils.validate.impl.AbstractValidateCodeProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-25 下午4:09
 * @Description: 邮箱验证码处理器
 */
@Component
public class EmailValidateCodeProcessor extends AbstractValidateCodeProcessor {

    @Override
    protected void send(ServletWebRequest request, String validateCode) {
        System.out.println(request.getParameter("email") +
                "邮箱验证码发送成功，验证码为：" + validateCode);
    }

}
