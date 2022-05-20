package com.fjm.converters;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-09-30 下午4:16
 * @Description:
 */
public class CustomMessageConverter extends MappingJackson2HttpMessageConverter {
    public CustomMessageConverter(){
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        setSupportedMediaTypes(mediaTypes);
    }
}
