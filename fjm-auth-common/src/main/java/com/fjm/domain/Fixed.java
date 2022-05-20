package com.fjm.domain;

import java.lang.annotation.*;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 上午10:13
 * @Description: 标识字段是否可控
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface Fixed {
}
