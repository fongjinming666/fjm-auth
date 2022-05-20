package com.fjm.granter;

import com.alibaba.fastjson.JSONObject;
import com.fjm.dao.IWechatAuthDao;
import com.fjm.emun.ApiResult;
import com.fjm.entity.WechatAuth;
import com.fjm.exception.BadRequestException;
import com.fjm.service.oauth.SecurityUserService;
import com.fjm.utils.WechatAuthUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

import static com.fjm.entity.WechatAuth.syncWechat;


/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-29 上午9:16
 * @Description: 微信授权模式
 */
public class WechatTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "wechat";

    private SecurityUserService securityUserService;

    private IWechatAuthDao wechatAuthDao;


    /**
     * 构造方法提供一些必要的注入的参数
     * 通过这些参数来完成我们父类的构建
     *
     * @param tokenServices
     * @param clientDetailsService
     * @param oAuth2RequestFactory
     * @param securityUserService
     */
    public WechatTokenGranter(AuthorizationServerTokenServices tokenServices,
                              ClientDetailsService clientDetailsService,
                              OAuth2RequestFactory oAuth2RequestFactory,
                              SecurityUserService securityUserService,
                              IWechatAuthDao wechatAuthDao) {
        super(tokenServices, clientDetailsService, oAuth2RequestFactory, GRANT_TYPE);
        this.securityUserService = securityUserService;
        this.wechatAuthDao = wechatAuthDao;
    }

    /**
     * 在这里查询我们用户，构建用户的授权信息
     *
     * @param client       客户端
     * @param tokenRequest tokenRequest
     * @return OAuth2Authentication
     */
    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> params = tokenRequest.getRequestParameters();
        String code = params.getOrDefault("code", "");
        String openId = "", accessToken = "";
        WechatAuth wechatAuth;
        try {
            JSONObject authCode = WechatAuthUtil.requestAuthCode(code);
            if (authCode != null) {
                openId = authCode.getString("openid");
                accessToken = authCode.getString("access_token");
            }
            if (StringUtils.isBlank(openId) || StringUtils.isBlank(accessToken)) {
                throw new BadRequestException(ApiResult.WECHAT_AUTH_FAILED, "微信授权登录失败");
            }

            JSONObject userInfo = WechatAuthUtil.requestWechatInfo(accessToken, openId);
            if (userInfo != null) {
                if (userInfo.containsKey("errcode")) {
                    throw new BadRequestException(ApiResult.WECHAT_AUTH_FAILED, "微信授权登录失败");
                }
                Query query = new Query();
                query.addCriteria(Criteria.where(WechatAuth.OPENID).is(openId));
                wechatAuth = wechatAuthDao.findOne(query);
                wechatAuth = syncWechat(wechatAuth, userInfo);
                if (StringUtils.isBlank(wechatAuth.getId())) {
                    wechatAuthDao.insert(wechatAuth);
                } else {
                    wechatAuthDao.updateAll(wechatAuth);
                }
            } else {
                throw new BadRequestException(ApiResult.WECHAT_AUTH_FAILED, "微信授权登录失败");
            }
        } catch (Exception e) {
            throw new BadRequestException(ApiResult.WECHAT_AUTH_FAILED, "微信授权登录失败");
        }
        UserDetails userDetails = securityUserService.loadUserByWechatId(openId);

        Authentication user = new UsernamePasswordAuthenticationToken(userDetails,
                userDetails.getPassword(), userDetails.getAuthorities());
        return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), user);
    }
}
