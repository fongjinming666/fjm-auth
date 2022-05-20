package com.fjm.utils;

import org.springframework.util.StringUtils;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午3:55
 * @Description:
 */
public class Constants {

    public static final String phoneReg = "\"?0?(13|14|15|16|18|17|19)[0-9]{9}\"?";
    public static final String IS_NUMBER_REG = "^[0-9]*$";
    public static final String HK_PHONE_REG = "^(5|6|8|9)\\d{7}$";
    public static final String MO_PHONE_REG = "^6\\d{7}$";
    public static final String TW_PHONE_REG = "^09\\d{8}$";
    public static final String EMAIL_REG = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";

    public static String BEARER_AUTHENTICATION = "Bearer";
    public static String HEADER_AUTHENTICATION = "authorization";

    public static final String STATUS = "status";
    public static final String DELETED = "deleted";

    /**
     * 验证码redis持续时间 10分钟.
     */
    public static final int RANDOMCODE_MINUTES = 10;

    public static final String SERVER_ERROR_MSG = "服务稍不留神开了小差,请凭logId为凭证联系服务方";

    /**
     * 默认区号
     */
    public static final String DEFAULT_SMS_CODE = "86";

    /**
     * 根据入参内容定位smsCode
     *
     * @param smsCode
     * @return
     */
    public static String syncSmsCode(String smsCode) {
        smsCode = StringUtils.isEmpty(smsCode) ? Constants.DEFAULT_SMS_CODE : smsCode;
        return smsCode;
    }
}
