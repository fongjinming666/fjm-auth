package com.fjm.config;

import com.fjm.mongo.AbstractMongoConfigure;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-04-20 下午5:44
 * @Description:
 */
@Configuration
@EnableMongoRepositories(basePackages = {"com.fjm.dao"}, mongoTemplateRef = "authMongoTemplate")
@ConfigurationProperties(prefix = "spring.data.mongodb.auth")
public class AuthMongoConfig extends AbstractMongoConfigure {

    @Primary
    @Override
    public @Bean(name = "authMongoTemplate")
    MongoTemplate getMongoTemplate() throws Exception {
        return new MongoTemplate(mongoDatabaseFactory());
    }
}
