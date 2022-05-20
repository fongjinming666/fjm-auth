package com.fjm.utils.validate.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.fjm.emun.ApiResult;
import com.fjm.exception.BadRequestException;
import com.fjm.mapper.CustomMapper;
import com.fjm.properties.AliyunSmsProperties;
import com.fjm.utils.Constants;
import com.fjm.utils.sms.SmsResponse;
import com.fjm.utils.sms.SmsUtil;
import com.fjm.utils.validate.impl.AbstractValidateCodeProcessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-25 下午4:02
 * @Description: 手机验证码处理器
 */
@Slf4j
@Component
public class SmsValidateCodeProcessor extends AbstractValidateCodeProcessor {

    @Resource
    private CustomMapper customMapper;

    @Resource
    private DefaultAcsClient defaultAcsClient;

    @Resource
    private AliyunSmsProperties aliyunSmsProperties;

    @SneakyThrows
    @Override
    protected void send(ServletWebRequest request, String validateCode) {
        String smsCode = Constants.syncSmsCode(request.getParameter("smsCode"));
        String phone = smsCode + request.getParameter("sms");
        Map codeMap = new HashMap<>();
        codeMap.put("code", validateCode);

        String signName = aliyunSmsProperties.getSignName();
        String loginTemplateCode = aliyunSmsProperties.getLoginTemplateCode();
        if (!Constants.DEFAULT_SMS_CODE.equalsIgnoreCase(smsCode)) {
            signName = aliyunSmsProperties.getGlogalSignName();
            loginTemplateCode = aliyunSmsProperties.getGlobalLoginTemplateCode();
        }
        //todo 发送验证码
        SmsUtil.sendMsg(defaultAcsClient, phone, signName, loginTemplateCode, customMapper.writeValueAsString(codeMap), new SmsResponse() {

            @Override
            public void onSuccess(List<String> phones, SendSmsResponse sendSmsResponse) {
                log.info("aliyun send sms,{}", "send sms is success :" + sendSmsResponse.getMessage());
                log.info("aliyun send sms,{}", request.getParameter("sms") +
                        "手机验证码发送成功，验证码为：" + validateCode);
            }

            @Override
            public void onFail(SendSmsResponse sendSmsResponse) {
                log.info("aliyun send sms,{}", "send sms is failed :" + sendSmsResponse.getMessage());
                throw new BadRequestException(ApiResult.SERVER_ERROR, "云短信发送失败");
            }
        });
    }
}
