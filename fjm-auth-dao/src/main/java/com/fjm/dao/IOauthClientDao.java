package com.fjm.dao;

import com.fjm.entity.OauthClient;
import com.fjm.mongo.BaseDao;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-17 下午4:25
 * @Description:
 */
public interface IOauthClientDao extends BaseDao<OauthClient> {

    OauthClient queryOauthClientByClientId(String clientId);
}
