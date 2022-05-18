package com.fjm.entity;

import com.fjm.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.oauth2.provider.approval.Approval;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午3:30
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("oauth_approval")
public class OauthApproval extends BaseEntity {

    public static final String EXPIRE_AT = "expireAt";
    public static final String STATUS = "status";
    public static final String LAST_MODIFIED_AT = "lastModifiedAt";
    public static final String USER_ID = "userId";
    public static final String CLIENT_ID = "clientId";
    public static final String SCOPE = "scope";

    @Field("expireAt")
    private Long expireAt;

    @Field("status")
    private Approval.ApprovalStatus status;

    @Field("lastModifiedAt")
    private Long lastModifiedAt;

    @Field("userId")
    private String userId;

    @Field("clientId")
    private String clientId;

    @Field("scope")
    private String scope;
}
