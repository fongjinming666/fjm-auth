package com.fjm.vo;/**
 * @Author: jinmingfong
 * @CreateTime: 2022/5/20 11:24
 * @Description:
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Author: jinmingfong
 * @CreateTime: 2022-05-20 11:24
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppCodeLoginVo {

    /**
     * 区号
     */
    @Pattern(regexp = "^[0-9]*$", message = "区号格式有误")
    @ApiModelProperty(value = "区号")
    private String smsCode;

    /**
     * 手机号码
     */
    @NotEmpty(message = "手机号码不能为空")
    @ApiModelProperty(value = "手机号码")
    private String phone;

    /**
     * 短信验证码
     */
    @NotEmpty(message = "短信验证码不能为空")
    @Length(min = 6, max = 6, message = "短信验证码必须是6位")
    @ApiModelProperty(value = "短信验证码")
    private String verificationCode;

    /**
     * 是否一键登录
     */
    @ApiModelProperty(value = "是否一键登录")
    private boolean isMob;
}
