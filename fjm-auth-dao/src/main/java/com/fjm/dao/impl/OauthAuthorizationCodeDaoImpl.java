package com.fjm.dao.impl;

import com.fjm.dao.IOauthAuthorizationCodeDao;
import com.fjm.entity.OauthAuthorizationCode;
import com.fjm.mongo.AuthDaoAdapter;
import org.springframework.stereotype.Repository;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午3:31
 * @Description:
 */
@Repository
public class OauthAuthorizationCodeDaoImpl extends AuthDaoAdapter<OauthAuthorizationCode> implements IOauthAuthorizationCodeDao {
}
