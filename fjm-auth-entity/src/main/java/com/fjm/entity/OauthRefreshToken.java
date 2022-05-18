package com.fjm.entity;

import com.fjm.converters.SerializableObjectConverter;
import com.fjm.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午3:12
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("oauth_refresh_token")
public class OauthRefreshToken extends BaseEntity {

    public static final String TOKEN_ID = "tokenId";

    @Field("tokenId")
    private String tokenId;

    @Field("token")
    private OAuth2RefreshToken token;

    @Field("authentication")
    private String authentication;

    public OAuth2Authentication getAuthentication() {
        return SerializableObjectConverter.deserialize(authentication);
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializableObjectConverter.serialize(authentication);
    }
}
