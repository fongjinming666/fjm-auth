package com.fjm.utils.validate;

import com.fjm.emun.ApiResult;
import com.fjm.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-25 下午4:16
 * @Description: 验证码处理分发
 */
@Component
@RequiredArgsConstructor
public class ValidateCodeProcessorHolder {

    @Resource
    private Map<String, ValidateCodeProcessor> validateCodeProcessors;

    /**
     * 通过验证码类型查找
     *
     * @param type 验证码类型
     * @return 验证码处理器
     */
    public ValidateCodeProcessor findValidateCodeProcessor(String type) {
        String name = type.toLowerCase() + ValidateCodeProcessor.class.getSimpleName();
        ValidateCodeProcessor processor = validateCodeProcessors.get(name);
        if (Objects.isNull(processor)) {
            throw new BadRequestException(ApiResult.SERVER_ERROR, "验证码处理器" + name + "不存在");
        }
        return processor;
    }

}
