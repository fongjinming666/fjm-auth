package com.fjm.mongo;

import com.fjm.dto.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MongoDB分页插件，需要结合Spring-data使用.
 * https://github.com/Ryan-Miao/mongo-page-helper
 *
 * @author Ryan Miao at 2018-06-07 15:19
 **/
@Slf4j
public class MongoPageHelper {

    private static final int FIRST_PAGE_NUM = 1;
    //    private static final String ID = "_id";
    private static final String ID = "id";
    private final MongoTemplate mongoTemplate;

    public MongoPageHelper(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    /**
     * 分页查询，直接返回集合类型的结果.
     *
     * @see MongoPageHelper#pageQuery(Query,
     * Class, Integer, Integer, Function,
     * String)
     */
    public <T> PageResult<T> pageQuery(Query query, Class<T> entityClass, Integer pageSize,
                                       Integer pageNum) {
        return pageQuery(query, entityClass, pageSize, pageNum, Function.identity(), null);
    }

    /**
     * 分页查询，不考虑条件分页，直接使用skip-limit来分页.
     *
     * @see MongoPageHelper#pageQuery(Query,
     * Class, Integer, Integer, Function,
     * String)
     */
    public <T, R> PageResult<R> pageQuery(Query query, Class<T> entityClass,
                                          Integer pageSize, Integer pageNum, Function<T, R> mapper) {
        return pageQuery(query, entityClass, pageSize, pageNum, mapper, null);
    }

    /**
     * 分页查询.
     *
     * @param query       Mongo Query对象，构造你自己的查询条件.
     *                    <p>
     *                    //     * @param criteria,  hw add by 2020.03.31 .解决：Due to limitations of the com.mongodb.BasicDocument, you can't add a second 'null' criteria.
     * @param entityClass Mongo collection定义的entity class，用来确定查询哪个集合.
     * @param mapper      映射器，你从db查出来的list的元素类型是entityClass, 如果你想要转换成另一个对象，比如去掉敏感字段等，可以使用mapper来决定如何转换.
     * @param pageSize    分页的大小.
     * @param pageNum     当前页.
     * @param lastId      条件分页参数, 区别于skip-limit，采用find(_id>lastId).limit分页.
     *                    如果不跳页，像朋友圈，微博这样下拉刷新的分页需求，需要传递上一页的最后一条记录的ObjectId。 如果是null，则返回pageNum那一页.
     * @param <T>         collection定义的class类型.
     * @param <R>         最终返回时，展现给页面时的一条记录的类型。
     * @return PageResult，一个封装page信息的对象.
     */
    public <T, R> PageResult<R> pageQuery(Query query, Class<T> entityClass,
                                          Integer pageSize, Integer pageNum, Function<T, R> mapper, String lastId) {

        log.debug("query.getQueryObject()==>" + query.getQueryObject());
        //分页逻辑
        long total = mongoTemplate.count(query, entityClass);
        final Integer pages = (int) Math.ceil(total / (double) pageSize);
        if (pageNum <= 0 || pageNum > pages) {
            pageNum = FIRST_PAGE_NUM;
        }
        final Criteria criteria = new Criteria();
        if (!StringUtils.isBlank(lastId)) {
            if (pageNum != FIRST_PAGE_NUM) {
                criteria.and(ID).gt(new ObjectId(lastId));
            }
            query.addCriteria(criteria);// 注意：可能会出现 Due to limitations of the com.mongodb.BasicDocument, you can't add a second 'null' criteria
            query.limit(pageSize);
        } else {
            int skip = pageSize * (pageNum - 1);
            log.debug("分页参数===》pageNum:" + pageNum + ",pageSize:" + pageSize + "--> skip:" + skip + ",limit:" + pageSize);
            query.skip(skip).limit(pageSize);
        }

       /* final List<T> entityList = mongoTemplate
                .find(query.addCriteria(criteria)
                                .with(new Sort(Collections.singletonList(new Order(Direction.ASC, ID)))),
                        entityClass);*/

        // todo  版本差异，Sort的构造方法变成Protect ，不开直接new，改成：Sort by （List<Sort.Order> orders )
        List<Order> orders = Collections.singletonList(new Order(Direction.ASC, ID));
        final List<T> entityList = mongoTemplate.find(query.with(Sort.by(orders)), entityClass);

        final PageResult<R> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPages(pages);
        pageResult.setPageSize(pageSize);
        pageResult.setPageNum(pageNum);
        if (mapper != null) {
            pageResult.setList(entityList.stream().map(mapper).collect(Collectors.toList()));
        } else {
            pageResult.setList((List<R>) entityList);
        }

        return pageResult;
    }

    /**
     * 分页查询.
     *
     * @param query       Mongo Query对象，构造你自己的查询条件.
     *                    <p>
     *                    //     * @param criteria,  hw add by 2020.03.31 .解决：Due to limitations of the com.mongodb.BasicDocument, you can't add a second 'null' criteria.
     * @param entityClass Mongo collection定义的entity class，用来确定查询哪个集合.
     * @param mapper      映射器，你从db查出来的list的元素类型是entityClass, 如果你想要转换成另一个对象，比如去掉敏感字段等，可以使用mapper来决定如何转换.
     * @param pageSize    分页的大小.
     * @param pageNum     当前页.
     * @param lastId      条件分页参数, 区别于skip-limit，采用find(_id>lastId).limit分页.
     *                    如果不跳页，像朋友圈，微博这样下拉刷新的分页需求，需要传递上一页的最后一条记录的ObjectId。 如果是null，则返回pageNum那一页.
     * @param <T>         collection定义的class类型.
     * @param <R>         最终返回时，展现给页面时的一条记录的类型。
     * @return PageResult，一个封装page信息的对象.
     */
    public <T, R> PageResult<R> pageQuery4Recommend(Criteria criteria,Query query, Class<T> entityClass,
                                                    Integer pageSize, Integer pageNum, Function<T, R> mapper, String lastId) {

        log.debug("query.getQueryObject()==>" + query.getQueryObject());
        //分页逻辑
        long total = mongoTemplate.count(query, entityClass);
        final Integer pages = (int) Math.ceil(total / (double) pageSize);
        if (pageNum <= 0 || pageNum > pages) {
            pageNum = FIRST_PAGE_NUM;
        }
        //final Criteria criteria = new Criteria();
        if (!StringUtils.isBlank(lastId)) {
            if (pageNum != FIRST_PAGE_NUM) {
                criteria.and(ID).gt(new ObjectId(lastId));
            }
            query.addCriteria(criteria);// 注意：可能会出现 Due to limitations of the com.mongodb.BasicDocument, you can't add a second 'null' criteria
            query.limit(pageSize);
        } else {
            int skip = pageSize * (pageNum - 1);
            log.debug("分页参数===》pageNum:" + pageNum + ",pageSize:" + pageSize + "--> skip:" + skip + ",limit:" + pageSize);
            query.skip(skip).limit(pageSize);
        }

       /* final List<T> entityList = mongoTemplate
                .find(query.addCriteria(criteria)
                                .with(new Sort(Collections.singletonList(new Order(Direction.ASC, ID)))),
                        entityClass);*/

        // todo  版本差异，Sort的构造方法变成Protect ，不开直接new，改成：Sort by （List<Sort.Order> orders
        List<Order> orders = Collections.singletonList(new Order(Direction.ASC, ID));
        //final List<T> entityList = mongoTemplate.find(query.with(Sort.by(orders)), entityClass);
        //final Criteria criteria = new Criteria();
        Aggregation aggregation1 = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.sample(pageSize));
        AggregationResults<T> outputTypeCount1 =
                mongoTemplate.aggregate(aggregation1, "sr_recipe", entityClass);

        final List<T> entityList1 = new ArrayList<>();
        for (Iterator<T> iterator = outputTypeCount1.iterator(); iterator.hasNext(); ) {
            //Object obj = iterator.next();
            entityList1.add(iterator.next());
            //System.out.println(JSONObject.toJSONString(obj));
        }

        final PageResult<R> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPages(pages);
        pageResult.setPageSize(pageSize);
        pageResult.setPageNum(pageNum);
        if (mapper != null) {
            pageResult.setList(entityList1.stream().map(mapper).collect(Collectors.toList()));
        } else {
            pageResult.setList((List<R>) entityList1);
        }

        return pageResult;
    }


    /**
     * 分页查询，直接返回集合类型的结果.
     *
     * @see MongoPageHelper#pageQuery(Query,
     * Class, Integer, Integer, Function,
     * String)
     */
    public <T> PageResult<T> pageQuery4Recommend(Criteria criteria, Query query, Class<T> entityClass, Integer pageSize,
                                                 Integer pageNum) {
        return pageQuery4Recommend(criteria, query, entityClass, pageSize, pageNum, Function.identity(), null);
    }

}
