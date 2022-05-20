package com.fjm.vo;/**
 * @Author: jinmingfong
 * @CreateTime: 2022/5/20 11:01
 * @Description:
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Author: jinmingfong
 * @CreateTime: 2022-05-20 11:01
 * @Description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCodeVo {

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
     * 短信类型
     */
    @ApiModelProperty(value = "短信类型")
    private Integer smsType;
}
