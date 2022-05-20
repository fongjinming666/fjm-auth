package com.fjm.mobile;

import com.fjm.emun.ApiResult;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-09-24 上午10:30
 * @Description:
 */
@Getter
public class ValidateOauthCodeException extends AuthenticationException {

    public ApiResult result;

    public ValidateOauthCodeException(ApiResult result) {
        super(result.getMessage());
        this.result = result;
    }

    public ValidateOauthCodeException(ApiResult result, String message) {
        super(message);
        this.result = result;
    }
}
