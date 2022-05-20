package com.fjm.exception;

import com.fjm.emun.ApiResult;
import lombok.Getter;

/**
 * @Author: jinmingfong
 * @CreateTime: 2020-04-15 23:39
 * @Description:
 */
@Getter
public class BadRequestException extends RuntimeException {

    public ApiResult result;

    public BadRequestException(ApiResult result) {
        this.result = result;
    }

    public BadRequestException(ApiResult result, String message) {
        super(message);
        this.result = result;
    }
}

