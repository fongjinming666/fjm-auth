package com.fjm.entity;

import com.fjm.converters.SerializableObjectConverter;
import com.fjm.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 上午11:53
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("oauth_access_token")
public class OauthAccessToken extends BaseEntity {

    public static final String TOKEN_ID = "tokenId";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String AUTHENTICATION_ID = "authenticationId";
    public static final String CLIENT_ID = "clientId";
    public static final String USERNAME = "username";

    /**
     * tokenId
     */
    @Field("tokenId")
    private String tokenId;

    /**
     * 授权令牌
     */
    @Field("accessToken")
    private OAuth2AccessToken accessToken;

    /**
     * 认证信息Id
     */
    @Field("authenticationId")
    private String authenticationId;

    /**
     * 授权的用户名称
     */
    @Field("username")
    private String username;

    /**
     * 发起授权的clientId
     */
    @Field("clientId")
    private String clientId;

    /**
     * 认证信息
     */
    @Field("authentication")
    private String authentication;

    /**
     * 刷新令牌
     */
    @Field("refreshToken")
    private String refreshToken;

    public OAuth2Authentication getAuthentication() {
        return SerializableObjectConverter.deserialize(authentication);
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializableObjectConverter.serialize(authentication);
    }
}
