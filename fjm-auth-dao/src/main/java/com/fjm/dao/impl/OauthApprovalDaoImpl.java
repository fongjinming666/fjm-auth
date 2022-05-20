package com.fjm.dao.impl;

import com.fjm.dao.IOauthApprovalDao;
import com.fjm.entity.OauthApproval;
import com.fjm.mongo.AuthDaoAdapter;
import org.springframework.stereotype.Repository;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午3:31
 * @Description:
 */
@Repository
public class OauthApprovalDaoImpl extends AuthDaoAdapter<OauthApproval> implements IOauthApprovalDao {
}
