package com.fjm.utils.sms;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.dypnsapi.model.v20170525.GetMobileRequest;
import com.aliyuncs.dypnsapi.model.v20170525.GetMobileResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fjm.emun.ApiResult;
import com.fjm.exception.BadRequestException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: fongjinming
 * @CreateTime: 2020-02-12 15:10
 * @Description:
 */
public class SmsUtil {

    public static DefaultAcsClient initPromoteAcsClient(String accessKeyId, String accessKeySecret) {
        try {
            /** 设置超时时间-可自行调整. */
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
            /** 初始化ascClient,暂时不支持多region（请勿修改）. */
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            /** 短信API产品名称（短信产品名固定，无需修改）. */
            String product = "Dysmsapi";
            /** 短信API产品域名（接口地址固定，无需修改）. */
            String domain = "dysmsapi.aliyuncs.com";
            /** 初始化ascClient,暂时不支持多region（请勿修改）. */
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            return new DefaultAcsClient(profile);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 发送短信
     *
     * @param defaultAcsClient
     * @param phone
     * @param signName
     * @param templateCode
     * @param templateParam
     * @param smsResponse
     * @throws ClientException
     */
    public static void sendMsg(DefaultAcsClient defaultAcsClient, String phone, String signName, String templateCode, String templateParam, SmsResponse smsResponse) throws ClientException {
        /** 组装请求对象. */
        SendSmsRequest request = new SendSmsRequest();
        /** 使用post提交. */
        request.setMethod(MethodType.POST);
        /** 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，
         * 批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,
         * 验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，
         * 接收号码格式为国际区号+号码，如“85200000000”. */
        request.setPhoneNumbers(phone);
        /** 必填:短信签名-可在短信控制台中找到. */
        request.setSignName(signName);
        /**  必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版. */
        request.setTemplateCode(templateCode);

        /**  可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为. */
        /**  友情提示:如果JSON中需要带换行符,
         * 请参照标准的JSON协议对换行符的要求,
         * 比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败. */
        if (StringUtils.isNoneBlank(templateParam)) {
            request.setTemplateParam(templateParam);
        }
        /** 可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段). */
        //request.setSmsUpExtendCode("90997");
        /** 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者. */
        //request.setOutId("yourOutId");

        /** 请求失败这里会抛ClientException异常. */
        SendSmsResponse sendSmsResponse = defaultAcsClient.getAcsResponse(request);

        if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
            //logger.i("aliyun sen sms", "send sms is success :" + sendSmsResponse.getMessage());
            smsResponse.onSuccess(Arrays.asList(phone), sendSmsResponse);
        } else {
            //logger.e("aliyun sen sms", "send sms is fail :" + sendSmsResponse.getMessage());
            smsResponse.onFail(sendSmsResponse);
        }
    }

    /**
     * 批量发送短信
     *
     * @param defaultAcsClient
     * @param phones
     * @param signName
     * @param templateCode
     * @param templateParam
     * @param smsResponse
     * @throws ClientException
     */
    public static void batchSendMsg(DefaultAcsClient defaultAcsClient, List<String> phones, String signName, String templateCode, String templateParam, SmsResponse smsResponse) throws ClientException {
        /** 组装请求对象. */
        SendSmsRequest request = new SendSmsRequest();
        /** 使用post提交. */
        request.setMethod(MethodType.POST);
        /** 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，
         * 批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,
         * 验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，
         * 接收号码格式为国际区号+号码，如“85200000000”. */
        request.setPhoneNumbers(phones.stream().collect(Collectors.joining(",")));
        /** 必填:短信签名-可在短信控制台中找到. */
        request.setSignName(signName);
        /**  必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版. */
        request.setTemplateCode(templateCode);

        /**  可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为. */
        /**  友情提示:如果JSON中需要带换行符,
         * 请参照标准的JSON协议对换行符的要求,
         * 比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败. */
        if (StringUtils.isNoneBlank(templateParam)) {
            request.setTemplateParam(templateParam);
        }
        /** 可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段). */
        //request.setSmsUpExtendCode("90997");
        /** 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者. */
        //request.setOutId("yourOutId");

        /** 请求失败这里会抛ClientException异常. */
        SendSmsResponse sendSmsResponse = defaultAcsClient.getAcsResponse(request);

        if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
            //logger.i("aliyun sen sms", "send sms is success :" + sendSmsResponse.getMessage());
            smsResponse.onSuccess(phones, sendSmsResponse);
        } else {
            //logger.e("aliyun sen sms", "send sms is fail :" + sendSmsResponse.getMessage());
            smsResponse.onFail(sendSmsResponse);
        }
    }

    /**
     * 阿里云的一键登录token
     *
     * @param defaultAcsClient
     * @param mobAccessToken
     * @return
     */
    public static String queyMobile(DefaultAcsClient defaultAcsClient, String mobAccessToken) {
        GetMobileRequest request = new GetMobileRequest();
        request.setAccessToken(mobAccessToken);
        try {
            GetMobileResponse response = defaultAcsClient.getAcsResponse(request);
            if ("OK".equalsIgnoreCase(response.getCode())) {
                return response.getGetMobileResultDTO().getMobile();
            } else {
                throw new BadRequestException(ApiResult.ONEKEY_LOGIN_FAILED, "一键登录获取手机号失败");
            }
        } catch (Exception e) {
            throw new BadRequestException(ApiResult.ONEKEY_LOGIN_FAILED, "一键登录获取手机号失败");
        }

    }

    public static void main(String[] args) {
        List<String> phoneNumList = new ArrayList<>();
        phoneNumList.add("13606487287");
        // String signname="阿里云短信测试专用";
        // //必填:短信模板-可在短信 控制台中找到
        // String smscode="SMS_139231200";
        // Map<String, String> data = new HashMap<>();
        // data.put("code", "test hashmac");
        // Gson gson = new Gson();
        // sendSms("18858139224", gson.toJson(data),"阿里云短信测试专用","SMS_139231200");
    }
}
