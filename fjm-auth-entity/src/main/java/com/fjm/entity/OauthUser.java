package com.fjm.entity;/**
 * @Author: jinmingfong
 * @CreateTime: 2022/5/18 16:05
 * @Description:
 */

import com.fjm.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @Author: jinmingfong
 * @CreateTime: 2022-05-18 16:05
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("oauth_user")
public class OauthUser extends BaseEntity {

    public static final String USERNAME = "username";
    public static final String SMSCODE = "smsCode";
    public static final String PHONE = "phone";
    public static final String WECHATID = "openId";
    public static final String APPLEUSERID = "appleUserId";

    /**
     * username目前规则 区号+手机号
     */
    @Field("username")
    @ApiModelProperty(value = "用户名称")
    private String username;

    @Field("fullname")
    @ApiModelProperty(value = "真实姓名")
    private String fullname;

    @Field("gender")
    @ApiModelProperty(value = "性别")
    private Integer gender;

    @Field("password")
    @ApiModelProperty(value = "密码")
    private String password;

    @Field("originPwd")
    @ApiModelProperty(value = "原始密码")
    private String originPwd;

    @Field("smsCode")
    @ApiModelProperty(value = "区号")
    private String smsCode;

    @Field("phone")
    @ApiModelProperty(value = "手机号")
    private String phone;

    @Field("openId")
    @ApiModelProperty(value = "微信openId")
    private String openId;

    @Field("appleUserId")
    @ApiModelProperty(value = "苹果用户Id")
    private String appleUserId;

    @Field("source")
    @ApiModelProperty(value = "来源")
    private String source;

    @Field("roleId")
    @ApiModelProperty(value = "权限Id")
    private String roleId;

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

    /**
     * 构造器
     *
     * @param id
     * @param username
     * @param fullname
     * @param gender
     * @param password
     * @param originPwd
     * @param smsCode
     * @param phone
     * @param source
     * @param roleId
     * @param createTime
     * @param modifyTime
     * @param status
     * @param deleted
     */
    public OauthUser(String id, String username, String fullname, Integer gender, String password, String originPwd, String smsCode, String phone, String source, String roleId, Long createTime, Long modifyTime, Integer status, Integer deleted) {
        super(id);
        this.username = username;
        this.fullname = fullname;
        this.gender = gender;
        this.password = password;
        this.originPwd = originPwd;
        this.smsCode = smsCode;
        this.phone = phone;
        this.source = source;
        this.roleId = roleId;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.status = status;
        this.deleted = deleted;
    }
}
