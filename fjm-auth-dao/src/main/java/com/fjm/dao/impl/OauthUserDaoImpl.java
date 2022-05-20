package com.fjm.dao.impl;

import com.fjm.bo.AuthUserPageQuery;
import com.fjm.dao.IOauthUserDao;
import com.fjm.dto.PageResult;
import com.fjm.emun.DeleteEnum;
import com.fjm.entity.OauthUser;
import com.fjm.mongo.AuthDaoAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 上午10:38
 * @Description:
 */
@Repository
public class OauthUserDaoImpl extends AuthDaoAdapter<OauthUser> implements IOauthUserDao {

    /**
     * @param queryBo
     * @return
     */
    @Override
    public Long getCount(AuthUserPageQuery queryBo) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("deleted").is(DeleteEnum.NO_DELETED.getValue());
        if (StringUtils.isNotBlank(queryBo.getFullname())) {
            String pattern = ".*" + queryBo.getFullname() + ".*";
            Criteria fullName = new Criteria();
            fullName.and("fullname").regex(pattern);
            criteria.orOperator(fullName);
        }
        if (StringUtils.isNotEmpty(queryBo.getSmsCode())) {
            criteria.and("smsCode").is(queryBo.getSmsCode());
        }

        if (StringUtils.isNotEmpty(queryBo.getPhone())) {
            criteria.and("phone").is(queryBo.getPhone());
        }

        if (StringUtils.isNotEmpty(queryBo.getRoleId())) {
            criteria.and("roleId").is(queryBo.getRoleId());
        }
        query.addCriteria(criteria);
        return this.mongoTemplate.count(query, OauthUser.class);
    }


    /**
     * 根据参数获取设备列表
     *
     * @param queryBo
     * @return
     */
    @Override
    public List<OauthUser> getList(AuthUserPageQuery queryBo) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("deleted").is(DeleteEnum.NO_DELETED.getValue());
        if (StringUtils.isNotBlank(queryBo.getFullname())) {
            String pattern = ".*" + queryBo.getFullname() + ".*";
            Criteria fullName = new Criteria();
            fullName.and("fullname").regex(pattern);
            criteria.orOperator(fullName);
        }
        if (StringUtils.isNotEmpty(queryBo.getSmsCode())) {
            criteria.and("smsCode").is(queryBo.getSmsCode());
        }

        if (StringUtils.isNotEmpty(queryBo.getPhone())) {
            criteria.and("phone").is(queryBo.getPhone());
        }

        if (StringUtils.isNotEmpty(queryBo.getRoleId())) {
            criteria.and("roleId").is(queryBo.getRoleId());
        }

        if (null != queryBo.getCurrentPage()) {
            Sort sort;
            sort = Sort.by(Sort.Direction.DESC, "modifyTime");
            //sort = Sort.by(Sort.Direction.DESC, "createTime");

            //当前页码，每页条数，排序方式
            Pageable pageable = PageRequest.of(queryBo.getCurrentPage() - 1, queryBo.getPageSize(), sort);
            query.with(pageable);
        }
        query.addCriteria(criteria);

        return mongoTemplate.find(query, OauthUser.class);
    }

    @Override
    public PageResult<OauthUser> getPages(AuthUserPageQuery queryBo) {
        PageResult<OauthUser> pageResult = new PageResult<>();
        List<OauthUser> list = new ArrayList<>();
        Long total = this.getCount(queryBo);
        if (total > 0) {
            List<OauthUser> uacUsers = this.getList(queryBo);
            if (!(uacUsers == null || uacUsers.size() == 0)) {
                list.addAll(uacUsers);
            }
        }
        pageResult.setList(list);
        pageResult.setPageNum(queryBo.getCurrentPage());
        pageResult.setPageSize(queryBo.getPageSize());
        pageResult.setTotal(total.longValue());
        return pageResult;
    }
}
