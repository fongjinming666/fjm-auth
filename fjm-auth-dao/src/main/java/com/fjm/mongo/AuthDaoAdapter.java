package com.fjm.mongo;

import com.fjm.domain.BaseEntity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author lyw
 * @version 1.0
 * @date 2021-6-10 17:29
 */
@Lazy
@Repository
public class AuthDaoAdapter<T extends BaseEntity> extends BaseDaoAdapter<T> implements InitializingBean {

    @Autowired
    @Qualifier("authMongoTemplate")
    public MongoTemplate authMongoTemplate;

    @Override
    public void afterPropertiesSet() {
        super.mongoTemplate = this.authMongoTemplate;
    }
}
