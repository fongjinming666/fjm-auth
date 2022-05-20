package com.fjm.service.oauth;


import com.fjm.constants.FromOauthLoginConstant;
import com.fjm.converters.SerializableObjectConverter;
import com.fjm.entity.OauthAuthorizationCode;
import com.fjm.service.RedisService;
import com.fjm.utils.Constants;
import com.fjm.utils.ThrowableUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午4:01
 * @Description:
 */
@Slf4j
public class OauthAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

    /**
     * 通过存储到授权doe到database、暂code存储无时间限制.
     */
    //@Resource
    //private IOauthAuthorizationCodeDao oauthAuthorizationCodeDao;

    /**
     * 通过存储授权code到redis、code可自定义redis时间，暂定10分钟.
     */
    @Resource
    private RedisService redisService;

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        OauthAuthorizationCode authorizationCode = new OauthAuthorizationCode();
        authorizationCode.setCode(code);
        authorizationCode.setAuthentication(authentication);
        //oauthAuthorizationCodeDao.insert(authorizationCode);
        String jsonAuthorizationCode = SerializableObjectConverter.serialize(authentication);
        if (StringUtils.isBlank(jsonAuthorizationCode)) {
            return;
        }
        log.info("OauthAuthorizationCodeServices,第三方授权已请求。请求信息:{}，请求code:{}",
                authentication == null ? "" : authentication, code);
        redisService.set(FromOauthLoginConstant.AUTHORIZATION_LOGIN_CODE + code, jsonAuthorizationCode,
                Duration.ofMinutes(Constants.RANDOMCODE_MINUTES).getSeconds());
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        OAuth2Authentication authentication = null;
        /*Query query = new Query();
        query.addCriteria(Criteria.where(OauthAuthorizationCode.CODE).is(code));
        OauthAuthorizationCode authorizationCode = oauthAuthorizationCodeDao.findOne(query);
        if (authorizationCode != null) {
            authentication = authorizationCode.getAuthentication();
            oauthAuthorizationCodeDao.deleteById(authorizationCode.getId());
        }*/
        String authenticationJson = redisService.getString(FromOauthLoginConstant.AUTHORIZATION_LOGIN_CODE + code);
        try {
            authentication = SerializableObjectConverter.deserialize(authenticationJson);
        } catch (Exception e) {
            log.error("OAuth2Authentication.remove解析authentication失败:{}", ThrowableUtil.getStackTrace(e));
        } finally {
            if (authentication != null) {
                log.info("OauthAuthorizationCodeServices,第三方授权已消费。请求code:{}", code);
                redisService.del(FromOauthLoginConstant.AUTHORIZATION_LOGIN_CODE + code);
            }
        }

        return authentication;
    }
}
