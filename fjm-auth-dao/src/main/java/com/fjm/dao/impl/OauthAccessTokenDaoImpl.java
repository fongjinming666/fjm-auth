package com.fjm.dao.impl;

import com.fjm.dao.IOauthAccessTokenDao;
import com.fjm.entity.OauthAccessToken;
import com.fjm.mongo.AuthDaoAdapter;
import org.springframework.stereotype.Repository;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午2:07
 * @Description:
 */
@Repository
public class OauthAccessTokenDaoImpl extends AuthDaoAdapter<OauthAccessToken> implements IOauthAccessTokenDao {
}
