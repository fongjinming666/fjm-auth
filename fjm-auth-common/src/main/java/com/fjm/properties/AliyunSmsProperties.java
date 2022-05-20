package com.fjm.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-29 下午5:55
 * @Description:
 */
@ConfigurationProperties(prefix = "ali.sms")
public class AliyunSmsProperties {

    private String signName;

    private String loginTemplateCode;

    private String glogalSignName;

    private String globalLoginTemplateCode;

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getLoginTemplateCode() {
        return loginTemplateCode;
    }

    public void setLoginTemplateCode(String loginTemplateCode) {
        this.loginTemplateCode = loginTemplateCode;
    }

    public String getGlogalSignName() {
        return glogalSignName;
    }

    public void setGlogalSignName(String glogalSignName) {
        this.glogalSignName = glogalSignName;
    }

    public String getGlobalLoginTemplateCode() {
        return globalLoginTemplateCode;
    }

    public void setGlobalLoginTemplateCode(String globalLoginTemplateCode) {
        this.globalLoginTemplateCode = globalLoginTemplateCode;
    }
}
