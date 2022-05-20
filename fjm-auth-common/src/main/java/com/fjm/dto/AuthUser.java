package com.fjm.dto;/**
 * @Author: jinmingfong
 * @CreateTime: 2022/5/19 18:14
 * @Description:
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @Author: jinmingfong
 * @CreateTime: 2022-05-19 18:14
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser implements Serializable {

    @ApiModelProperty(value = "用户Id")
    private String id;

    @ApiModelProperty(value = "用户名称")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    private String fullname;

    @Field("gender")
    @ApiModelProperty(value = "性别")
    private Integer gender;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "原始密码")
    private String originPwd;

    @ApiModelProperty(value = "区号")
    private String smsCode;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "微信openId")
    private String openId;

    @ApiModelProperty(value = "苹果用户Id")
    private String appleUserId;

    @ApiModelProperty(value = "来源")
    private String source;

    @ApiModelProperty(value = "角色Id")
    private String roleId;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "用户状态（0为不可用，1为可用）")
    private Integer status;
}
