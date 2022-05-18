package com.fjm.emun;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @Author: jinmingfong
 * @CreateTime: 2020-04-15 19:46
 * @Description:
 */
public enum AdminResult {

    /**
     * 操作结果--成功
     */
    SUCCESS(200, "操作结果"),
    /**
     * 操作失败，数据参数错误
     */
    PARAMETERERROR(300, "操作失败，数据参数错误"),
    /**
     * 数据已存在(用于数据唯一性)
     */
    DATA_EXIST(305, "数据已存在"),

    /**
     * 数据不存在(用于数据唯一性)
     */
    DATA_NO_EXIST(306, "数据不存在"),
    /**
     * 服务器错误
     */
    ERROR(500, "服务器错误"),
    /**
     * 超时
     */
    RUNTIME_ERROR(501,"超时"),
    /**
     * 文件上传失败
     */
    UPLOAD_FILE_ERROR(502,"文件上传失败");

    @JsonValue
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private int value;

    @JsonValue
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private String desc;

    private AdminResult(int value) {
        this.value = value;
    }

    AdminResult(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator
    public static AdminResult forValue(int value) {
        for (AdminResult rs : values()) {
            if (rs.value == value) {
                return rs;
            }
        }
        throw new IllegalArgumentException("Invalid Status type code: " + value);
    }
}
