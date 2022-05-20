package com.fjm.service.impl;

import com.fjm.bo.AuthUserPageQuery;
import com.fjm.dao.IOauthAccessTokenDao;
import com.fjm.dao.IOauthUserDao;
import com.fjm.dto.AuthUser;
import com.fjm.dto.PageResult;
import com.fjm.emun.*;
import com.fjm.entity.OauthAccessToken;
import com.fjm.entity.OauthUser;
import com.fjm.exception.BadRequestException;
import com.fjm.service.IOauthUserService;
import com.fjm.store.OauthTokenStore;
import com.fjm.utils.Constants;
import com.fjm.utils.RedisLock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-05-24 下午3:33
 * @Description:
 */
@Service
public class OauthUserServiceImpl implements IOauthUserService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private OauthTokenStore oauthTokenStore;

    @Resource
    private IOauthAccessTokenDao oauthAccessTokenDao;

    @Resource
    private IOauthUserDao oauthUserDao;

    @Resource
    private RedisLock redisLock;

    public static final String UAC_USER_REGISTER_LOCK = "uac_user_register_lock_";

    public static final int UAC_USER_REGISTER_LOCK_EXPIRE_TIME = 3000;

    private final String defaultOriginPwd = "admin123456";

    @Override
    public OauthUser createOauthUser(String smsCode, String phone, String fullname, String originPwd) {
        if (StringUtils.isBlank(smsCode) || StringUtils.isBlank(phone)) {
            return null;
        }
        String username = (StringUtils.isBlank(smsCode) ? "" : smsCode.trim())
                + (StringUtils.isBlank(phone) ? "" : phone.trim());
        Query query = new Query();
        query
                .addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                .addCriteria(Criteria.where(OauthUser.SMSCODE).is(smsCode))
                .addCriteria(Criteria.where(OauthUser.PHONE).is(phone))
                /** username永远不变为区号+手机号. */
                .addCriteria(Criteria.where(OauthUser.USERNAME).is(username));
        long existCount = oauthUserDao.count(query);
        if (existCount > 0) {
            throw new BadRequestException(ApiResult.EXIST, "用户已存在");
        }
        OauthUser oauthUser = null;
        Long time = System.currentTimeMillis();
        final String defaultPassword = passwordEncoder.encode(originPwd);
        boolean getLock = redisLock.lock(UAC_USER_REGISTER_LOCK + smsCode + phone, smsCode + phone, UAC_USER_REGISTER_LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
        if (getLock) {
            oauthUser = new OauthUser(username, fullname, GenderEnum.NOT_SET.getValue(), defaultPassword,
                    originPwd, smsCode, phone, SourceEnum.NOT_SET.getValue().toString(), time, time,
                    StatusEnum.ACTIVE_STATUS.getValue(), DeleteEnum.NO_DELETED.getValue());
            oauthUserDao.insert(oauthUser);
        }
        redisLock.unlock(UAC_USER_REGISTER_LOCK + smsCode + phone, smsCode + phone);
        return oauthUser;
    }

    @Override
    public OauthUser modifyOauthUser(String oauthUserId, String fullname, String originPwd, String roleId) {
        Query query = new Query();
        query
                .addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                .addCriteria(Criteria.where(OauthUser.ID).is(oauthUserId));
        OauthUser oauthUser = oauthUserDao.findOne(query);
        if (oauthUser == null) {
            throw new BadRequestException(ApiResult.NOT_FOUND, "用户不存在");
        }
        boolean toUpgrade = false;
        if (!StringUtils.isBlank(fullname)) {
            oauthUser.setFullname(fullname);
            toUpgrade = true;
        }
        if (!StringUtils.isBlank(originPwd)) {
            oauthUser.setOriginPwd(originPwd);
            final String defaultPassword = passwordEncoder.encode(originPwd);
            oauthUser.setPassword(defaultPassword);
            toUpgrade = true;
        }
        /*if (!StringUtils.isBlank(roleId)) {
            oauthUser.setRoleId(roleId);
            toUpgrade = true;
        }*/
        if (toUpgrade) {
            oauthUser.setModifyTime(System.currentTimeMillis());
            oauthUserDao.updateById(oauthUser);
        }
        return oauthUser;
    }

    @Override
    public boolean changeOauthUserState(String oauthUserId, Boolean toDisable) {
        if (toDisable == null) {
            return false;
        }
        Query query = new Query();
        query
                .addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                .addCriteria(Criteria.where(OauthUser.ID).is(oauthUserId));
        OauthUser oauthUser = oauthUserDao.findOne(query);
        if (oauthUser == null) {
            throw new BadRequestException(ApiResult.NOT_FOUND, "用户不存在");
        }
        if (toDisable) {
            oauthUser.setStatus(StatusEnum.NO_ACTIVE_STATUS.getValue());
        } else {
            oauthUser.setStatus(StatusEnum.ACTIVE_STATUS.getValue());
        }
        oauthUser.setModifyTime(System.currentTimeMillis());
        oauthUserDao.updateById(oauthUser);
        /** 下线被禁用账号. */
        if (StatusEnum.NO_ACTIVE_STATUS.getValue() == oauthUser.getStatus().intValue()) {
            syncOauthUserLogout(oauthUser.getUsername());
        }
        return true;
    }

    @Override
    public boolean deletedOauthUser(String oauthUserId) {
        Query query = new Query();
        query
                .addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                .addCriteria(Criteria.where(OauthUser.ID).is(oauthUserId));
        OauthUser oauthUser = oauthUserDao.findOne(query);
        oauthUser.setDeleted(DeleteEnum.DELETED.getValue());
        oauthUser.setModifyTime(System.currentTimeMillis());
        oauthUserDao.updateById(oauthUser);
        /** 下线被删除账号. */
        syncOauthUserLogout(oauthUser.getUsername());
        return true;
    }

    @Override
    public OauthUser syncOauthUser(String smsCode, String phone, Map<String, Object> parameters) {
        if (StringUtils.isBlank(smsCode) || StringUtils.isBlank(phone)) {
            return null;
        }
        String username = (StringUtils.isBlank(smsCode) ? "" : smsCode.trim())
                + (StringUtils.isBlank(phone) ? "" : phone.trim());
        Query query = new Query();
        query
                .addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                .addCriteria(Criteria.where(OauthUser.SMSCODE).is(smsCode))
                .addCriteria(Criteria.where(OauthUser.PHONE).is(phone))
                /** username永远不变为区号+手机号. */
                .addCriteria(Criteria.where(OauthUser.USERNAME).is(username));
        OauthUser oauthUser = oauthUserDao.findOne(query);
        Long time = System.currentTimeMillis();
        if (oauthUser == null) {
            final String defaultPassword = passwordEncoder.encode(defaultOriginPwd);
            boolean getLock = redisLock.lock(UAC_USER_REGISTER_LOCK + smsCode + phone, smsCode + phone, UAC_USER_REGISTER_LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
            if (getLock) {
                oauthUser = new OauthUser(username, "", GenderEnum.NOT_SET.getValue(), defaultPassword,
                        defaultOriginPwd, smsCode, phone,
                        parameters.get("source") == null ? SourceEnum.NOT_SET.getValue().toString() : parameters.get("source").toString(), time, time,
                        StatusEnum.ACTIVE_STATUS.getValue(), DeleteEnum.NO_DELETED.getValue());
                oauthUserDao.insert(oauthUser);
            }
            redisLock.unlock(UAC_USER_REGISTER_LOCK + smsCode + phone, smsCode + phone);
        }
        if (oauthUser.getStatus() != null) {
            if (oauthUser.getStatus().intValue() == StatusEnum.NO_ACTIVE_STATUS.getValue()) {
                throw new BadRequestException(ApiResult.DISABLED);
            }
        }
        if (StringUtils.isBlank(oauthUser.getOriginPwd())) {
            //oauthUser.setOriginPwd(defaultOriginPwd);
        }
        oauthUser.setGender(parameters.get("gender") == null ? GenderEnum.NOT_SET.getValue() : Integer.valueOf(parameters.get("gender").toString()));
        //oauthUser.setOpenId(StringUtils.isBlank(parameters.get("openId")) ? "" : parameters.get("openId").toString());
        //oauthUser.setAppleUserId(StringUtils.isBlank(parameters.get("appleId")) ? "" : parameters.get("appleId").toString());
        if (oauthUser.getId() != null) {
            oauthUserDao.updateById(oauthUser);
        }
        return oauthUser;
    }

    @Override
    public OauthUser queryOauthUser(String oauthUserId) {
        OauthUser oauthUser = oauthUserDao.queryById(oauthUserId);
        if (oauthUser == null) {
            throw new BadRequestException(ApiResult.NOT_FOUND, "用户不存在");
        }
        return oauthUser;
    }

    @Override
    public OauthUser queryOauthUserWithUsername(String username) {
        Query query = new Query();
        query
                .addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                /** username永远不变为区号+手机号. */
                .addCriteria(Criteria.where(OauthUser.USERNAME).is(username));
        OauthUser oauthUser = oauthUserDao.findOne(query);
        if (oauthUser == null) {
            throw new BadRequestException(ApiResult.NOT_FOUND, "用户不存在");
        }
        return oauthUser;
    }

    @Override
    public PageResult<OauthUser> queryOauthUser(AuthUserPageQuery authUserPageQuery) {
        PageResult<OauthUser> userPageResult = oauthUserDao.getPages(authUserPageQuery);
        return userPageResult;
    }

    /**
     * 同步账号强迫下线
     *
     * @param username
     */
    private void syncOauthUserLogout(String username) {
        Query query = new Query();
        /** username永远不变为区号+手机号. */
        query.addCriteria(Criteria.where(OauthAccessToken.USERNAME).is(username));
        long oauthAccessTokenCount = oauthAccessTokenDao.count(query);
        if (oauthAccessTokenCount > 0) {
            oauthAccessTokenDao.delete(query);
        }
    }
}
