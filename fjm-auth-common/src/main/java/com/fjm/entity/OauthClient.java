package com.fjm.entity;

import com.fjm.domain.BaseEntity;
import com.fjm.emun.DeleteEnum;
import com.fjm.emun.StatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-17 下午4:20
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("oauth_client")
public class OauthClient extends BaseEntity implements ClientDetails {

    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String RESOURCE_IDS = "resourceIds";
    public static final String SCOPE = "scope";
    public static final String AUTHORIZED_GRANT_TYPES = "authorizedGrantTypes";
    public static final String REGISTERED_REDIRECT_URI = "registeredRedirectUri";
    public static final String AUTHORITIES = "authorities";
    public static final String ACCESS_TOKEN_VALIDITY_SECONDS = "accessTokenValiditySeconds";
    public static final String REFRESH_TOKEN_VALIDITY_SECONDS = "refreshTokenValiditySeconds";
    public static final String ADDITIONAL_INFORMATION = "additionalInformation";

    @Field("clientId")
    @ApiModelProperty(value = "客户端Id")
    private String clientId;

    @Field("clientName")
    @ApiModelProperty(value = "客户端名称")
    private String clientName;

    @Field("secretRequired")
    @ApiModelProperty(value = "该clientId是否需要clientSecret -- 默认true * 如implicit模式不需要clientSecret，则可设为false")
    private boolean secretRequired;

    @Field("clientSecret")
    @ApiModelProperty(value = "客户端钥匙")
    private String clientSecret;

    @Field("resourceIds")
    @ApiModelProperty(value = "该client可以访问的resources")
    private Set<String> resourceIds;

    @Field("scope")
    @ApiModelProperty(value = "支持的权限集")
    private Set<String> scope;

    @Field("scoped")
    @ApiModelProperty(value = "该client是否被限制scope * 若为false，则该client设置的scope参数将被忽略")
    private boolean scoped;

    @Field("authorizedGrantTypes")
    @ApiModelProperty(value = "支持的授权类型 -- 如authorization_code refresh_token implicit client_credentials")
    private Set<String> authorizedGrantTypes;

    @Field("registeredRedirectUri")
    @ApiModelProperty(value = "可支持的回调地址")
    private Set<String> registeredRedirectUri;

    @Field("authorities")
    @ApiModelProperty(value = "拥有的权限--user/admin等")
    private Collection<GrantedAuthority> authorities;

    @Field("accessTokenValiditySeconds")
    @ApiModelProperty(value = "token有效时长")
    private Integer accessTokenValiditySeconds;

    @Field("refreshTokenValiditySeconds")
    @ApiModelProperty(value = "refresh token有效时长")
    private Integer refreshTokenValiditySeconds;

    @Field("autoApprove")
    @ApiModelProperty(value = "对于特定scope，是否需要用户认证 * 该字段保存不需要用户认证的scope")
    private boolean autoApprove;

    @Field("approveAll")
    @ApiModelProperty(value = "是否支持所有scope")
    private boolean approveAll;

    @Field("additionalInformation")
    @ApiModelProperty(value = "额外信息")
    private Map<String, Object> additionalInformation;

    @Field("createTime")
    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @Field("modifyTime")
    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @Field("status")
    @ApiModelProperty(value = "用户状态（0为不可用，1为可用）")
    private Integer status;

    @Field("deleted")
    @ApiModelProperty(value = "删除状态（0为未删除，1已删除）")
    private Integer deleted;

    public OauthClient(String clientId) {
        this.clientId = clientId;
    }


    /**
     * 构造函数
     *
     * @param clientId
     * @param resourceIds
     * @param secretRequired
     * @param clientSecret
     * @param scoped
     * @param scope
     * @param authorizedGrantTypes
     * @param registeredRedirectUri
     * @param authorities
     * @param accessTokenValiditySeconds
     * @param refreshTokenValiditySeconds
     * @param autoApprove
     * @param additionalInformation
     * @param createTime
     */
    public OauthClient(String clientId, Set<String> resourceIds, boolean secretRequired, String clientSecret,
                       boolean scoped, Set<String> scope, Set<String> authorizedGrantTypes,
                       Set<String> registeredRedirectUri, Collection<GrantedAuthority> authorities,
                       Integer accessTokenValiditySeconds, Integer refreshTokenValiditySeconds,
                       boolean autoApprove, Map<String, Object> additionalInformation, Long createTime) {
        this.clientId = clientId;
        this.resourceIds = resourceIds;
        this.secretRequired = secretRequired;
        this.clientSecret = clientSecret;
        this.scoped = scoped;
        this.scope = scope;
        this.authorizedGrantTypes = authorizedGrantTypes;
        this.registeredRedirectUri = registeredRedirectUri;
        this.authorities = authorities;
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
        this.autoApprove = autoApprove;
        this.additionalInformation = additionalInformation;
        this.createTime = createTime;
        this.modifyTime = createTime;
        this.status = StatusEnum.ACTIVE_STATUS.getValue();
        this.deleted = DeleteEnum.NO_DELETED.getValue();
    }

    /**
     * 构造函数
     *
     * @param clientDetails
     */
    public OauthClient(ClientDetails clientDetails) {
        clientId = clientDetails.getClientId();
        clientSecret = clientDetails.getClientSecret();
        resourceIds = clientDetails.getResourceIds();
        scope = clientDetails.getScope();
        authorizedGrantTypes = clientDetails.getAuthorizedGrantTypes();
        registeredRedirectUri = clientDetails.getRegisteredRedirectUri();
        authorities = clientDetails.getAuthorities();
        accessTokenValiditySeconds = clientDetails.getAccessTokenValiditySeconds();
        refreshTokenValiditySeconds = clientDetails.getRefreshTokenValiditySeconds();
        additionalInformation = clientDetails.getAdditionalInformation();
    }

    @Override
    public boolean isAutoApprove(String s) {
        return autoApprove;
    }
}
