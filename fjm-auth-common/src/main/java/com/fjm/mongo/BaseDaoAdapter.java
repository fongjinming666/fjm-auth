package com.fjm.mongo;

import com.fjm.domain.BaseEntity;
import com.fjm.domain.Fixed;
import com.fjm.domain.PageList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 上午10:08
 * @Description:
 */
public class BaseDaoAdapter<T extends BaseEntity> implements BaseDao<T> {
    protected Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    protected MongoTemplate mongoTemplate;

    @Override
    public T queryById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return this.mongoTemplate.findOne(query, entityClass);
    }

    @Override
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, entityClass);
    }

    @Override
    public List<T> find(Query query) {
        return mongoTemplate.find(query, entityClass);
    }

    @Override
    public PageList<T> find(Query query, Pageable pageable) {
        PageList<T> pageList = new PageList<>();
        if (pageable != null) {
            long totalCount = count(query);
            int pageCount = (int) (totalCount / pageable.getPageSize());
            if (totalCount % pageable.getPageSize() != 0) {
                pageCount += 1;
            }
            query.with(pageable);
            pageList.makePageList(null, pageable.getPageSize(), totalCount, pageable.getPageNumber(), pageCount);
        }
        pageList.setPage(mongoTemplate.find(query, entityClass));
        return pageList;
    }

    /**
     * 根据条件查询集合
     *
     * @param object object
     * @return List<T>
     */
    @Override
    public List<T> queryList(T object) {
        Query query = getQueryByObject(object);
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * 通过反射将对象的值设置到update中
     *
     * @param obj
     * @param cur_class
     * @param update
     */
    private void setClassFieldToUpdate(Object obj, Class cur_class, Update update) {
        Field[] obj_fields = cur_class.getDeclaredFields();
        try {
            for (Field field : obj_fields) {
                if (Modifier.isFinal(field.getModifiers()) || Modifier.isPublic(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                boolean isFixed = false;
                Annotation annotations[] = field.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.getClass() == Fixed.class) {
                        isFixed = true;
                    }
                }
                if (isFixed) {//如果字段是不变的则不添加在update中
                    continue;
                }
                update.set(field.getName(), field.get(obj));
            }
            if (cur_class.getSuperclass() != null) {
                setClassFieldToUpdate(obj, cur_class.getSuperclass(), update);//递归获取父类的字段值
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long updateAll(T t) {
        Update update = new Update();
        setClassFieldToUpdate(t, entityClass, update);
        return mongoTemplate.updateFirst(Query.query(Criteria.where(BaseEntity.ID).is(t.getId())), update, entityClass).getModifiedCount();
    }

    @Override
    public void updateById(T object) {
        Query query = new Query(Criteria.where(BaseEntity.ID).is(object.getId()));
        //增加自定义字段注入
        try {
            Field field = object.getClass().getDeclaredField("modifyTimeForSyn");
            if (field != null) {
                field.setAccessible(true);
                field.set(object, new Date());
            }
        } catch (Exception e) {
        }
        Update update = getUpdateByObject(object);
        this.mongoTemplate.updateFirst(query, update, entityClass);
    }

    @Override
    public long update(Query query, Update update) {
        return mongoTemplate.updateFirst(query, update, entityClass).getModifiedCount();
    }

    @Override
    public long updateMulti(Query query, Update update) {
        return mongoTemplate.updateMulti(query, update, entityClass).getModifiedCount();
    }

    @Override
    public void updateInsert(T srcObj, T targetObj) {
        Query query = getQueryByObject(srcObj);
        Update update = getUpdateByObject(targetObj);
        this.mongoTemplate.upsert(query, update, entityClass);
    }

    @Override
    public T findAndModify(Query query, Update update, boolean isNew) {
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.returnNew(isNew);
        findAndModifyOptions.upsert(true);
        return mongoTemplate.findAndModify(query, update, findAndModifyOptions, entityClass);
    }

    @Override
    public void insert(T t) {
        mongoTemplate.insert(t);
    }

    @Override
    public void deleteById(String id) {
        Criteria criteria = Criteria.where(BaseEntity.ID).is(id);
        Query query = new Query(criteria);
        T obj = this.mongoTemplate.findOne(query, entityClass);
        if (obj != null) {
            this.mongoTemplate.remove(obj).getDeletedCount();
        }
    }

    @Override
    public long delete(Query query) {
        return mongoTemplate.remove(query, entityClass).getDeletedCount();
    }

    @Override
    public void insert(List<T> tList) {
        mongoTemplate.insertAll(tList);
    }

    @Override
    public long count(Query query) {
        return mongoTemplate.count(query, entityClass);
    }

    @Override
    public boolean isExist(Query query) {
        return mongoTemplate.exists(query, entityClass);
    }

    /**
     * 将查询条件对象转换为query
     *
     * @param object object
     * @return Query
     */
    private Query getQueryByObject(T object) {
        Query query = new Query();
        String[] fields = getFiledName(object);
        Criteria criteria = new Criteria();
        for (String field : fields) {
            Object filedValue = getFieldValueByName(field, object);
            if (filedValue != null) {
                criteria.and(field).is(filedValue);
            }
        }
        query.addCriteria(criteria);
        return query;
    }

    /**
     * 将查询条件对象转换为update.
     *
     * @param object
     * @return
     * @author Jason
     */
    private Update getUpdateByObject(T object) {
        Update update = new Update();
        String[] fileds = getFiledName(object);
        for (int i = 0; i < fileds.length; i++) {
            String filedName = (String) fileds[i];
            Object filedValue = getFieldValueByName(filedName, object);
            if (filedValue != null) {
                update.set(filedName, filedValue);
            }
        }
        return update;
    }

    /***
     * 获取对象属性返回字符串数组
     * @param o o
     * @return String[]
     */
    protected static String[] getFiledName(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];

        for (int i = 0; i < fields.length; ++i) {
            fieldNames[i] = fields[i].getName();
        }

        return fieldNames;
    }

    /***
     * 根据属性获取对象属性值
     * @param fieldName fieldName
     * @param o o
     * @return Object
     */
    protected static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String e = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + e + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (Exception var6) {
            return null;
        }
    }
}
