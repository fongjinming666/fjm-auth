package com.fjm.utils.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;

import java.util.List;

/**
 * @Author: fongjinming
 * @CreateTime: 2019-08-26 15:10
 * @Description:
 */
public abstract interface SmsResponse {

    public void onSuccess(List<String> phones, SendSmsResponse sendSmsResponse);

    public void onFail(SendSmsResponse sendSmsResponse);

}
