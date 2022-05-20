package com.fjm.dao.impl;

import com.fjm.dao.IOauthClientDao;
import com.fjm.emun.DeleteEnum;
import com.fjm.entity.OauthClient;
import com.fjm.mongo.AuthDaoAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-17 下午4:25
 * @Description:
 */
@Repository
public class OauthClientDaoImpl extends AuthDaoAdapter<OauthClient> implements IOauthClientDao {

    @Override
    public OauthClient queryOauthClientByClientId(String clientId) {
        if (StringUtils.isBlank(clientId)) {
            return null;
        }
        OauthClient queryClient = new OauthClient();
        queryClient.setClientId(clientId);
        queryClient.setDeleted(DeleteEnum.NO_DELETED.getValue());
        List<OauthClient> queryClients = queryList(queryClient);
        if (!CollectionUtils.isEmpty(queryClients)) {
            return queryClients.get(0);
        }
        return null;
    }
}
