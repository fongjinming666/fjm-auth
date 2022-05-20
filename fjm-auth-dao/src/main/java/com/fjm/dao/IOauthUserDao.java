package com.fjm.dao;

import com.fjm.bo.AuthUserPageQuery;
import com.fjm.dto.PageResult;
import com.fjm.entity.OauthUser;
import com.fjm.mongo.BaseDao;

import java.util.List;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 上午9:30
 * @Description:
 */
public interface IOauthUserDao extends BaseDao<OauthUser> {

    Long getCount(AuthUserPageQuery queryBo);

    List<OauthUser> getList(AuthUserPageQuery queryBo);

    PageResult<OauthUser> getPages(AuthUserPageQuery queryBo);
}
