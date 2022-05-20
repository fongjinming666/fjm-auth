package com.fjm.bo;/**
 * @Author: jinmingfong
 * @CreateTime: 2022/5/18 16:09
 * @Description:
 */


import com.fjm.dto.PageQuery;
import lombok.Data;

/**
 * @Author: jinmingfong
 * @CreateTime: 2022-05-18 16:09
 * @Description:
 */
@Data
public class AuthUserPageQuery extends PageQuery {

    /**
     * 真实姓名
     */
    private String fullname;

    /**
     * 区号
     */
    private String smsCode;


    /**
     * 手机号
     */
    private String phone;

    /**
     * 权限Id
     */
    private String roleId;
}
