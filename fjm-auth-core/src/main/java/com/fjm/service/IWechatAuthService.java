package com.fjm.service;


import com.fjm.entity.WechatAuth;

import java.util.Map;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-05-24 下午5:20
 * @Description:
 */
public interface IWechatAuthService {

    WechatAuth syncWechatAuth(String openId, Map<String, Object> parameters);
}
