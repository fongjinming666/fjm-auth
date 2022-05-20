package com.fjm.domain;

import com.fjm.entity.OauthClient;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.provider.ClientDetails;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-17 下午4:32
 * @Description:
 */
@Data
@NoArgsConstructor
public class OauthClientDetail extends OauthClient implements ClientDetails {
    private static final long serialVersionUID = 1L;

    /**
     * 构造器
     *
     * @param client
     */
    public OauthClientDetail(OauthClient client) {
        super(client.getClientId(), client.getClientName(), client.isSecretRequired(),
                client.getClientSecret(), client.getResourceIds(), client.getScope(),
                client.isScoped(), client.getAuthorizedGrantTypes(), client.getRegisteredRedirectUri(),
                client.getAuthorities(), client.getAccessTokenValiditySeconds(), client.getRefreshTokenValiditySeconds(),
                client.isAutoApprove(), client.isApproveAll(), client.getAdditionalInformation(),
                client.getCreateTime(), client.getModifyTime(), client.getStatus(), client.getDeleted());
    }

    @Override
    public String getClientId() {
        return this.getId();
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return this.isApproveAll();
    }
}
