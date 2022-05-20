package com.fjm.service.oauth;


import com.fjm.dao.IOauthClientDao;
import com.fjm.dao.IOauthUserDao;
import com.fjm.domain.MessageConstant;
import com.fjm.domain.User;
import com.fjm.emun.ApiResult;
import com.fjm.emun.DeleteEnum;
import com.fjm.entity.OauthClient;
import com.fjm.entity.OauthUser;
import com.fjm.exception.BadRequestException;
import com.fjm.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 下午5:26
 * @Description:
 */
@Slf4j
public class SecurityUserService implements UserDetailsService {

    @Resource
    private IOauthUserDao oauthUserDao;

    @Resource
    private IOauthClientDao oauthClientDao;

    @Resource
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        String clientId = requestClientId();
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                //.addCriteria(Criteria.where(Constants.STATUS).is(StatusEnum.ACTIVE_STATUS.getValue()))
                //.addCriteria(Criteria.where(OauthUser.PHONE).is(s))
                .addCriteria(Criteria.where(OauthUser.USERNAME).is(s));
        User user = loadUserByQuery(clientId, query);

        return user;
    }

    /**
     * 获取鉴权用户
     *
     * @param clientId
     * @param query
     * @return
     */
    private User loadUserByQuery(String clientId, Query query) {
        OauthUser oauthUser = oauthUserDao.findOne(query);
        if (oauthUser == null) {
            throw new BadRequestException(ApiResult.NOT_FOUND, MessageConstant.USER_NOT_FOUND_ERROR);
        }
        User user = new User(clientId, oauthUser);

        if (StringUtils.isBlank(clientId)) {
            return user;
        }
        Query clientQuery = new Query();
        clientQuery.addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                .addCriteria(Criteria.where(OauthClient.CLIENT_ID).is(clientId));
        OauthClient client = oauthClientDao.findOne(clientQuery);
        return user;
    }


    public UserDetails loadUserByPhone(String phone) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                //.addCriteria(Criteria.where(Constants.STATUS).is(StatusEnum.ACTIVE_STATUS.getValue()))
                .addCriteria(Criteria.where(OauthUser.PHONE).is(phone));
        OauthUser user = oauthUserDao.findOne(query);
        if (user == null) {
            throw new BadRequestException(ApiResult.NOT_FOUND, "用户不存在");
        }
        Integer status = user.getStatus() == null ? 0 : user.getStatus();
        if (status != 1) {
            throw new BadRequestException(ApiResult.DISABLED, "用户已被禁用");
        }
        OauthUser uacUser = oauthUserDao.findOne(query);

        //todo 增加权限获取逻辑
        return new User(uacUser);
    }

    public UserDetails loadUserByAppleUserId(String appleUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                //.addCriteria(Criteria.where(Constants.STATUS).is(StatusEnum.ACTIVE_STATUS.getValue()))
                .addCriteria(Criteria.where(OauthUser.APPLEUSERID).is(appleUserId));
        OauthUser user = oauthUserDao.findOne(query);
        if (user == null) {
            throw new BadRequestException(ApiResult.NOT_FOUND, "用户不存在");
        }
        Integer status = user.getStatus() == null ? 0 : user.getStatus();
        if (status != 1) {
            throw new BadRequestException(ApiResult.DISABLED, "用户已被禁用");
        }
        OauthUser uacUser = oauthUserDao.findOne(query);

        //todo 增加权限获取逻辑
        return new User(uacUser);
    }


    public UserDetails loadUserByWechatId(String openId) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                //.addCriteria(Criteria.where(Constants.STATUS).is(StatusEnum.ACTIVE_STATUS.getValue()))
                .addCriteria(Criteria.where(OauthUser.WECHATID).is(openId));
        OauthUser user = oauthUserDao.findOne(query);
        if (user == null) {
            throw new BadRequestException(ApiResult.NOT_FOUND, "用户不存在");
        }
        Integer status = user.getStatus() == null ? 0 : user.getStatus();
        if (status != 1) {
            throw new BadRequestException(ApiResult.DISABLED, "用户已被禁用");
        }
        OauthUser uacUser = oauthUserDao.findOne(query);

        //todo 增加权限获取逻辑
        return new User(uacUser);
    }


    private String requestClientId() {
        String clientId = "";
        try {
            clientId = request.getParameter("client_id");
        } catch (Exception e) {
            //e.printStackTrace();
            /** 仅第三方授权出现问题. */
            log.info("SecurityUserService 获取clientId:{}", clientId);
            //log.info("获取clientId失败,不影响主线逻辑:{}", ThrowableUtil.getStackTrace(e));
        }
        return clientId;
    }
}
