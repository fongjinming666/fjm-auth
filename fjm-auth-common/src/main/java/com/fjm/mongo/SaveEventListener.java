package com.fjm.mongo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 上午9:42
 * @Description:
 */
@Slf4j
//@Component
public class SaveEventListener extends AbstractMongoEventListener<Object> {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        final Object source = event.getSource();
        if (source != null) {
            ReflectionUtils.doWithFields(source.getClass(), new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException,
                        IllegalAccessException {
                    //将一个字段设置为可读写，主要针对private字段；
                    ReflectionUtils.makeAccessible(field);
                    // 如果字段添加了我们自定义的AutoValue注解Vee
                    if (field.isAnnotationPresent(AutoIncKey.class)
                            && field.get(source) instanceof Number
                            && field.getLong(source) == 0) {
                        // 设置自增ID
                        field.set(source, getNextAutoId(source.getClass().getSimpleName()));
                    }

                    //如果字段名称为modifyTimeForSyn ，则设置当前时间
                    if ("modifyTimeForSyn".equals(field.getName())) {
                        field.set(source, new Date());
                    }
                }
            });
        }
    }


    /**
     * 获取下一个自增ID
     *
     * @param collName
     * @return
     */
    public Long getNextAutoId(String collName) {
        Query query = new Query(Criteria.where("collName").is(collName));
        Update update = new Update();
        update.inc("seqId", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);
        options.returnNew(true);
        MongoSequence seq = mongoTemplate
                .findAndModify(query, update, options, MongoSequence.class);
        return seq.getSeqId();
    }
}
