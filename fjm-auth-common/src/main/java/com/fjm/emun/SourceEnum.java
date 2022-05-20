package com.fjm.emun;

import lombok.Getter;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午3:49
 * @Description:
 */
@Getter
public enum SourceEnum {
    NOT_SET(-1, "未设置"),
    IOS(0, "IOS客户端"),
    ANDROID(1, "Android客户端"),
    ;

    private Integer value;

    private String desc;

    SourceEnum(Integer value, String desc) {
        this.desc = desc;
        this.value = value;
    }

    public static String getDesc(Integer value) {
        SourceEnum[] sourceEnums = values();
        for (SourceEnum sourceEnum : sourceEnums) {
            if (sourceEnum.getValue().equals(value)) {
                return sourceEnum.getDesc();
            }
        }
        return null;
    }

}
