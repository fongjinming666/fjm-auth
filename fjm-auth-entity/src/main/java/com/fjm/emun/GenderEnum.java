package com.fjm.emun;

import lombok.Getter;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午3:50
 * @Description:
 */
@Getter
public enum GenderEnum {
    NOT_SET(-1,"未设置"),
    MALE(0,"男"),
    FEMALE(1,"女"),
    ;

    private Integer value;

    private String desc;

    GenderEnum(Integer value, String desc) {
        this.desc = desc;
        this.value = value;
    }

    public static String getDesc(Integer value) {
        GenderEnum[] genderEnums = values();
        for (GenderEnum genderEnum : genderEnums) {
            if (genderEnum.getValue().equals(value)) {
                return genderEnum.getDesc();
            }
        }
        return null;
    }
}
