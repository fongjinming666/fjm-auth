package com.fjm.dao.impl;

import com.fjm.dao.IWechatAuthDao;
import com.fjm.entity.WechatAuth;
import com.fjm.mongo.AuthDaoAdapter;
import org.springframework.stereotype.Repository;


/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 上午10:38
 * @Description:
 */
@Repository
public class WechatAuthDaoImpl extends AuthDaoAdapter<WechatAuth> implements IWechatAuthDao {
}
