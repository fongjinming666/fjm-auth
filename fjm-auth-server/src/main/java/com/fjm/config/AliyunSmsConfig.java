package com.fjm.config;

import com.aliyuncs.DefaultAcsClient;
import com.fjm.utils.sms.SmsUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-29 下午5:51
 * @Description: 阿里云短信服务
 */
@Configuration
public class AliyunSmsConfig {

    @Value("${ali.accessKeyId:''}")
    private String accessKeyId;

    @Value("${ali.accessKeySecret:''}")
    private String accessKeySecret;

    @Bean("defaultAcsClient")
    public DefaultAcsClient initDefaultAcsClient() {
        return SmsUtil.initPromoteAcsClient(accessKeyId, accessKeySecret);
    }
}
