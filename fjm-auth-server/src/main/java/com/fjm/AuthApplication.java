package com.fjm;
/**
 * @Author: jinmingfong
 * @CreateTime: 2022/5/20 14:51
 * @Description:
 */

import com.fjm.config.DefaultRedisConfig;
import com.fjm.config.DefaultWebMvcConfig;
import com.fjm.context.ApplicationContextUtils;
import com.fjm.properties.AliyunSmsProperties;
import com.fjm.properties.AuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Author: jinmingfong
 * @CreateTime: 2022-05-20 14:51
 * @Description:
 */
@EnableAsync
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@Import({ApplicationContextUtils.class, AliyunSmsProperties.class, DefaultWebMvcConfig.class, AuthProperties.class, DefaultRedisConfig.class})
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
