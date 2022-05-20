package com.fjm.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjm.dao.IWechatAuthDao;
import com.fjm.entity.WechatAuth;
import com.fjm.service.IWechatAuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-05-24 下午5:20
 * @Description:
 */
@Service
public class WechatAuthServiceImpl implements IWechatAuthService {

    @Resource
    private IWechatAuthDao wechatAuthDao;

    @Override
    public WechatAuth syncWechatAuth(String openId, Map<String, Object> parameters) {
        if (parameters.isEmpty()) {
            return null;
        }
        JSONObject wechatObject = JSONObject.parseObject(JSON.toJSONString(parameters));
        Query query = new Query();
        query.addCriteria(Criteria.where(WechatAuth.OPENID).is(openId));
        WechatAuth wechatAuth = wechatAuthDao.findOne(query);
        wechatAuth = WechatAuth.syncWechat(wechatAuth, wechatObject);
        if (StringUtils.isBlank(wechatAuth.getId())) {
            wechatAuthDao.insert(wechatAuth);
        } else {
            wechatAuthDao.updateById(wechatAuth);
        }
        return wechatAuth;
    }
}
