package com.fjm.service;


import com.fjm.bo.AuthUserPageQuery;
import com.fjm.dto.PageResult;
import com.fjm.entity.OauthUser;

import java.util.Map;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-05-24 下午3:32
 * @Description:
 */
public interface IOauthUserService {

    /**
     * 创建统一授权用户
     *
     * @param smsCode
     * @param phone
     * @param fullname
     * @param originPwd
     * @return
     */
    OauthUser createOauthUser(String smsCode, String phone, String fullname, String originPwd);

    /**
     * 编辑授权用户信息
     *
     * @param oauthUserId
     * @param fullname
     * @param originPwd
     * @param roleId
     * @return
     */
    OauthUser modifyOauthUser(String oauthUserId, String fullname, String originPwd, String roleId);

    /**
     * 更改授权用户状态 true-禁用 fasle-解除禁用
     *
     * @param oauthUserId
     * @param toDisable
     * @return
     */
    boolean changeOauthUserState(String oauthUserId, Boolean toDisable);

    /**
     * 逻辑删除授权用户
     *
     * @param oauthUserId
     * @return
     */
    boolean deletedOauthUser(String oauthUserId);

    /**
     * 同步授权账号信息
     *
     * @param smsCode
     * @param phone
     * @param parameters
     * @return
     */
    OauthUser syncOauthUser(String smsCode, String phone, Map<String, Object> parameters);

    /**
     * 查询单个授权用户
     *
     * @param oauthUserId
     * @return
     */
    OauthUser queryOauthUser(String oauthUserId);

    /**
     * 通过用户名获取用户
     *
     * @param username
     * @return
     */
    OauthUser queryOauthUserWithUsername(String username);

    /**
     * 分页查询授权用户
     *
     * @param authUserPageQuery
     * @return
     */
    PageResult<OauthUser> queryOauthUser(AuthUserPageQuery authUserPageQuery);
}
