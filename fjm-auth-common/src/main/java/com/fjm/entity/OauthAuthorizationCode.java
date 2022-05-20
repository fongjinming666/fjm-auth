package com.fjm.entity;

import com.fjm.converters.SerializableObjectConverter;
import com.fjm.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午3:28
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("oauth_authorization_code")
public class OauthAuthorizationCode extends BaseEntity {

    public static final String CODE = "code";

    @Field("code")
    private String code;

    @Field("authentication")
    private String authentication;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OAuth2Authentication getAuthentication() {
        return SerializableObjectConverter.deserialize(authentication);
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializableObjectConverter.serialize(authentication);
    }
}
