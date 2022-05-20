package com.fjm.emun;

import lombok.Getter;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午3:49
 * @Description:
 */
@Getter
public enum DeleteEnum {
    NO_DELETED(0, "否"),
    DELETED(1, "是"),
    ;

    private Integer value;

    private String desc;

    DeleteEnum(Integer value, String desc) {
        this.desc = desc;
        this.value = value;
    }

    public static String getDesc(Integer value) {
        DeleteEnum[] deleteEnums = values();
        for (DeleteEnum deleteEnum : deleteEnums) {
            if (deleteEnum.getValue().equals(value)) {
                return deleteEnum.getDesc();
            }
        }
        return null;
    }
}
