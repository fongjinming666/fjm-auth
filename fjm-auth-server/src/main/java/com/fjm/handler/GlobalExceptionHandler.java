package com.fjm.handler;

import com.fjm.emun.ApiResult;
import com.fjm.exception.BadRequestException;
import com.fjm.mobile.ValidateOauthCodeException;
import com.fjm.model.ApiData;
import com.fjm.utils.ThrowableUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * controller全局错误拦截器
 *
 * @Author: jinmingfong
 * @CreateTime: 2021-03-26 下午3:17
 * @Description:
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.fjm.controller")
public class GlobalExceptionHandler {
    /**
     * 处理所自定义异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BadRequestException.class)
    public ApiData handleException(BadRequestException e) {
        /** 打印堆栈信息. */
        return ApiData.error(e.getResult(), e.getMessage());
    }

    /**
     * 处理所有不可知的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public ApiData handleException(Throwable e) {
        /** 打印堆栈信息. */
        log.error("Exception异常:result{}", ThrowableUtil.getStackTrace(e));
        return ApiData.error(ApiResult.SERVER_ERROR, ThrowableUtil.getStackTrace(e));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    public <T> ApiData<T> resolveConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        String msg;
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            StringBuilder msgBuilder = new StringBuilder();
            Iterator var5 = constraintViolations.iterator();

            while (var5.hasNext()) {
                ConstraintViolation constraintViolation = (ConstraintViolation) var5.next();
                msgBuilder.append(constraintViolation.getMessage()).append(",");
            }

            String errorMessage = msgBuilder.toString();
            if (errorMessage.length() > 1) {
                errorMessage = errorMessage.substring(0, errorMessage.length() - 1);
            }

            msg = errorMessage;
        } else {
            msg = ex.getMessage();
        }

        log.info("bean参数检验不通过:{}", msg);
        return ApiData.error(ApiResult.REQUEST_PARAMETERS_ERROR, msg);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public <T> ApiData<T> resolveMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ObjectError> objectErrors = ex.getBindingResult().getAllErrors();
        String msg;
        if (!CollectionUtils.isEmpty(objectErrors)) {
            StringBuilder msgBuilder = new StringBuilder();
            Iterator var5 = objectErrors.iterator();

            while (var5.hasNext()) {
                ObjectError objectError = (ObjectError) var5.next();
                msgBuilder.append(objectError.getDefaultMessage()).append(",");
            }

            String errorMessage = msgBuilder.toString();
            if (errorMessage.length() > 1) {
                errorMessage = errorMessage.substring(0, errorMessage.length() - 1);
            }

            msg = errorMessage;
        } else {
            msg = ex.getMessage();
        }

        log.info("method参数检验不通过:{}", msg);
        return ApiData.error(ApiResult.REQUEST_PARAMETERS_ERROR, msg);
    }

    /**
     * 处理账号权限问题
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AuthenticationException.class)
    public ApiData handleAutheticationException(Throwable e) {
        log.error("Exception异常:result{}", ThrowableUtil.getStackTrace(e));
        return ApiData.error(ApiResult.FORBIDDEN, ThrowableUtil.getStackTrace(e));
    }

    /**
     * 处理第三方授权拦截等信息
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ValidateOauthCodeException.class)
    public ApiData handleValidateOauthCodeException(ValidateOauthCodeException e) {
        log.error("Exception异常:result{}", ThrowableUtil.getStackTrace(e));
        return ApiData.error(e.getResult(), e.getMessage());
    }
}
