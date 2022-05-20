package com.fjm.utils.validate.impl;

import com.fjm.emun.ApiResult;
import com.fjm.exception.BadRequestException;
import com.fjm.utils.Constants;
import com.fjm.utils.validate.ValidateCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-25 下午4:05
 * @Description: redis验证码操作
 */
@Component
@RequiredArgsConstructor
public class ValidateCodeRepositoryImpl implements ValidateCodeRepository {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(ServletWebRequest request, String code, String type) {
        redisTemplate.opsForValue().set(buildKey(request, type), code,
                //  有效期可以从配置文件中读取或者请求中读取
                Duration.ofMinutes(Constants.RANDOMCODE_MINUTES).getSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public String get(ServletWebRequest request, String type) {
        return redisTemplate.opsForValue().get(buildKey(request, type));
    }

    @Override
    public void remove(ServletWebRequest request, String type) {
        redisTemplate.delete(buildKey(request, type));
    }

    /**
     * 构建 redis 存储时的 key
     *
     * @param request 请求
     * @param type    类型
     * @return key
     */
    private String buildKey(ServletWebRequest request, String type) {
        String deviceId = request.getParameter(type);
        if (StringUtils.isEmpty(deviceId)) {
            throw new BadRequestException(ApiResult.REQUEST_PARAMETERS_ERROR, "请求中不存在 " + type);
        }
        if ("sms".equalsIgnoreCase(type)) {
            /** 处理短信登录的手机号. */
            String smsCode = Constants.syncSmsCode(request.getParameter("smsCode"));
            deviceId = smsCode + deviceId;
        }
        return "code:" + type + ":" + deviceId;
    }
}
