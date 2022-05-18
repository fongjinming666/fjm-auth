package com.fjm.entity;

import com.alibaba.fastjson.JSONObject;
import com.fjm.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-17 下午4:20
 * @Description: 微信授权
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "wechat_auth")
public class WechatAuth extends BaseEntity {

    public static final String OPENID = "openId";

    @Field("openId")
    @ApiModelProperty(value = "用户的标识")
    private String openId;

    @Field("unionId")
    @ApiModelProperty(value = "微信unionId")
    private String unionId;

    @Field("nickname")
    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "性别")
    private Integer sex;

    @Field("province")
    @ApiModelProperty(value = "省份")
    private String province;

    @Field("city")
    @ApiModelProperty(value = "城市")
    private String city;

    @Field("country")
    @ApiModelProperty(value = "国家")
    private String country;

    @Field("headimgurl")
    @ApiModelProperty(value = "头像")
    private String headimgurl;

    @Field("createTime")
    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @Field("modifyTime")
    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    /**
     * 用户状态（0为不可用，1为可用）
     */
    //@Field("status")
    //private Integer status;

    /**
     * 同步微信对象
     *
     * @param wechatAuth
     * @param userObject
     * @return
     */
    public static WechatAuth syncWechat(WechatAuth wechatAuth, JSONObject userObject) {
        long nowTime = System.currentTimeMillis();
        if (wechatAuth == null) {
            wechatAuth = new WechatAuth();
            wechatAuth.setCreateTime(nowTime);
        }
        if (userObject != null) {
            String openId = userObject.getString("openid");
            String nickname = userObject.getString("nickname");
            String headimgurl = userObject.getString("headimgurl");
            headimgurl = headimgurl.replace("\\", "");
            /** 微信性别 1-男 2-女 本地性别 -1 -未设置 0-男 1-女. */
            Integer sex = userObject.get("sex") == null ? -1 : ("0".equalsIgnoreCase(userObject.get("sex").toString()) ? -1 : ("1".equalsIgnoreCase(userObject.getInteger("sex").toString()) ? 0 : 1));
            wechatAuth.setOpenId(openId);
            wechatAuth.setUnionId(userObject.getString("unionid"));
            wechatAuth.setNickname(nickname);
            wechatAuth.setHeadimgurl(headimgurl);
            wechatAuth.setSex(sex);
            wechatAuth.setCountry(userObject.getString("country"));
            wechatAuth.setProvince(userObject.getString("province"));
            wechatAuth.setCity(userObject.getString("city"));
            wechatAuth.setModifyTime(nowTime);
        }
        return wechatAuth;
    }
}
