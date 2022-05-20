package com.fjm.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 下午3:53
 * @Description:
 */
public class CustomLongConverter extends StdSerializer<Long> {

    /**
     * 构造器.
     */
    public CustomLongConverter() {
        super(Long.class);
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (value.toString().length() > 13) {
            gen.writeString(value.toString());
        } else {
            gen.writeNumber(value);
        }
    }
}
