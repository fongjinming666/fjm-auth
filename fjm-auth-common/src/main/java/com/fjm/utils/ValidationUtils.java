package com.fjm.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.commons.lang3.StringUtils;

/**
 * 验证工具
 *
 * @Author: fongjinming
 * @CreateTime: 2019-08-23 16:33
 * @Description:
 */
public class ValidationUtils {

    /**
     * 验证是否为手机
     *
     * @param mobile
     * @return
     */
    public static Boolean isNumber(String mobile) {
        return StringUtils.isBlank(mobile)
                || !(mobile.matches(Constants.IS_NUMBER_REG));
    }

    /**
     * 根据区号判断是否是正确的电话号码
     *
     * @param phoneNumber :不带国家码的电话号码
     * @param countryCode :默认国家码
     *                    return ：true 合法  false：不合法
     */
    public static boolean isPhoneNumberValid(String phoneNumber, String countryCode) {

        //System.out.println("isPhoneNumberValid: " + phoneNumber + "/" + countryCode);
        String fullMobileNumber = "+" + countryCode + phoneNumber;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(fullMobileNumber, countryCode);
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            System.err.println("isPhoneNumberValid NumberParseException was thrown: " + e.toString());
        }
        return false;
    }

    /**
     * 模糊化手机号
     *
     * @param smsCode
     * @param phone
     * @return
     */
    public static String syncBlurryPhone(String smsCode, String phone) {
        if (StringUtils.isBlank(smsCode) || StringUtils.isBlank(phone)) {
            return phone;
        }
        String blurryPhone = "";
        if (isPhoneNumberValid(phone, smsCode)) {
            if (phone.length() == 11) {
                blurryPhone = phone.substring(0, 3) + "****" + phone.substring(7, 11);
            } else {
                for (int i = 0; i < phone.length(); i++) {
                    if (i < 3) {
                        blurryPhone += phone.substring(i, i + 1);
                    } else if (i >= 3 && i < 7) {
                        blurryPhone += "*";
                    } else {
                        blurryPhone += phone.substring(i, i + 1);
                    }
                }
            }
        }
        return blurryPhone;
    }
}
