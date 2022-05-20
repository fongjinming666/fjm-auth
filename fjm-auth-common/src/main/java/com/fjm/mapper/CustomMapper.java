package com.fjm.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-17 下午3:05
 * @Description:
 */
@Component
public class CustomMapper extends ObjectMapper {

    public CustomMapper() {
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        /** 设置 SerializationFeature.FAIL_ON_EMPTY_BEANS 为 false. */
        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 解决jackson2无法反序列化LocalDateTime的问题
        this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.registerModule(new JavaTimeModule());
    }
}
