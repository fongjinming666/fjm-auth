package com.fjm.emun;

import lombok.Getter;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午3:50
 * @Description:
 */
@Getter
public enum StatusEnum {
    NO_ACTIVE_STATUS(0, "不可以用"),
    ACTIVE_STATUS(1, "可用");

    private Integer value;

    private String desc;

    StatusEnum(Integer value, String desc) {
        this.desc = desc;
        this.value = value;
    }


    public static String getDesc(Integer value) {
        StatusEnum[] statusEnums = values();
        for (StatusEnum statusEnum : statusEnums) {
            if (statusEnum.getValue().equals(value)) {
                return statusEnum.getDesc();
            }
        }
        return null;
    }
}
