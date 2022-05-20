package com.fjm.dao.impl;

import com.fjm.dao.IOauthRefreshTokenDao;
import com.fjm.entity.OauthRefreshToken;
import com.fjm.mongo.AuthDaoAdapter;
import org.springframework.stereotype.Repository;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午2:07
 * @Description:
 */
@Repository
public class OauthRefreshTokenDaoImpl extends AuthDaoAdapter<OauthRefreshToken> implements IOauthRefreshTokenDao {
}
