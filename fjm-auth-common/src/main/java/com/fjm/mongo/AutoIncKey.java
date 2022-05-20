package com.fjm.mongo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 上午9:42
 * @Description: 标识注解：标识主键ID需要自动增长
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoIncKey {
}
