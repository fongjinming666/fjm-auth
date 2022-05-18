package com.fjm.emun;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-17 下午3:10
 * @Description:
 */
public enum ApiResult {
    /**
     * 操作结果--成功
     */
    SUCCESS(200, "成功"),
    /**
     * 请求参数有误
     */
    REQUEST_PARAMETERS_ERROR(400, "请求参数有误"),
    /**
     * 服务器拒绝请求
     */
    FORBIDDEN(403, "服务器拒绝请求"),
    /**
     * 数据不存在
     */
    NOT_FOUND(404, "数据不存在"),
    /**
     * 数据已失效,暂时停用
     */
    DISABLED(430, "数据已失效,暂时停用"),
    /**
     * 微信授权失败
     */
    WECHAT_AUTH_FAILED(450, "微信授权失败"),
    /**
     * 苹果授权失败
     */
    APPLE_AUTH_FAILED(451, "苹果授权失败"),
    /**
     * 一键登录失败
     */
    ONEKEY_LOGIN_FAILED(452, "一键登录失败"),
    /**
     * 服务器错误
     */
    SERVER_ERROR(500, "服务器错误");


    @JsonValue
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @JsonValue
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private int value;

    private String message;

    private ApiResult(int value) {
        this.value = value;
    }

    private ApiResult(int value, String message) {
        this.value = value;
        this.message = message;
    }


    @JsonCreator
    public static ApiResult forValue(int value) {
        for (ApiResult rs : ApiResult.values()) {
            if (rs.value == value) {
                return rs;
            }
        }
        throw new IllegalArgumentException("Invalid Status type code: " + value);
    }
}
