package com.fjm.mongo;

import com.mongodb.client.MongoClients;
import lombok.Data;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-04-21 下午3:45
 * @Description:
 */
@Data
public abstract class AbstractMongoConfigure {

    private String uri;

    private String database;

    public SimpleMongoClientDatabaseFactory mongoDatabaseFactory() throws Exception {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(this.uri), this.database);
    }

    abstract public MongoTemplate getMongoTemplate() throws Exception;
}
