package com.fjm.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fjm.converters.CustomLongConverter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-04-23 上午9:32
 * @Description:
 */
public class DefaultWebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 跨域支持
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }

    /**
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        /** 可解决Long 类型在 前端精度丢失的问题， 如不想全局 直接添加注解 @JsonSerialize(using= ToStringSerializer.class) 到相应的字段. */
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //自定义Long类型转换 超过12个数字用String格式返回，由于js的number只能表示15个数字
        builder.serializerByType(BigInteger.class, ToStringSerializer.instance);
        builder.serializerByType(Long.class, new CustomLongConverter());
        builder.serializerByType(Long.TYPE, new CustomLongConverter());
        //converters.add(0, new MappingJackson2HttpMessageConverter(builder.build()));
        // converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));

        /** 字符串转换器 .*/
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();

        /** json转换器. */
        List<MediaType> list = new ArrayList<MediaType>();
        list.add(MediaType.APPLICATION_JSON_UTF8);
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter(builder.build());
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);

        converters.add(mappingJackson2HttpMessageConverter);
        converters.add(stringHttpMessageConverter);
        super.configureMessageConverters(converters);
    }

    /**
     * 增加自定义参数解析器.
     *
     * @param argumentResolvers
     */
    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
    }

    /**
     * swagger 页面配置.
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/resources/");
        registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }
}
