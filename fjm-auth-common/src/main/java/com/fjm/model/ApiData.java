package com.fjm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.fjm.emun.ApiResult;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-17 下午3:08
 * @Description:
 */
@Data
public class ApiData<T> implements Serializable {

    //@JsonView(common.class)
    private Integer code;

    //@JsonView(common.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    //@JsonView(common.class)
    private T data;

    //@JsonView(common.class)
    private String message;

    public ApiData() {
        timestamp = LocalDateTime.now();
    }

    public ApiData(Integer code) {
        this();
        this.code = code;
    }

    public ApiData(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public ApiData(T data) {
        this();
        this.data = data;
    }

    public ApiData(Integer code, T data) {
        this();
        this.code = code;
        this.data = data;
    }

    public ApiData(Integer code, T data, String message) {
        this();
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static ApiData ok() {
        return new ApiData<>(ApiResult.SUCCESS.getValue(),null,ApiResult.SUCCESS.getMessage());
    }

    public static <T> ApiData<T> ok(T data) {
        return new ApiData<>(ApiResult.SUCCESS.getValue(),data,ApiResult.SUCCESS.getMessage());
    }

    public static <T> ApiData<T> error(ApiResult resultCode, String message) {
        return new ApiData<>(resultCode.getValue(), message);
    }

    public static <T> ApiData<T> error(int code, String message) {
        return new ApiData<>(code, message);
    }
}
