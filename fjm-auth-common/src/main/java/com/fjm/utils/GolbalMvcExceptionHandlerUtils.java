package com.fjm.utils;

import cn.hutool.json.JSONUtil;
import com.fjm.emun.ApiResult;
import com.fjm.exception.BadRequestException;
import com.fjm.mapper.CustomMapper;
import com.fjm.model.ApiData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-05-02 下午4:42
 * @Description:
 */
@Slf4j
public class GolbalMvcExceptionHandlerUtils {

    /**
     * 通用错误处理 httpServlet
     *
     * @param response
     * @param exception
     * @param customMapper
     * @throws Exception
     */
    public static void writeFailedToResponse(HttpServletResponse response, Exception exception, CustomMapper customMapper) throws Exception {
        try {
            if (exception instanceof BadRequestException) {
                writeFailedToResponse(response, ((BadRequestException) exception).getResult(), exception.getMessage());
            }
        } catch (Exception ex) {
            writeFailedToResponse(response, ApiResult.SERVER_ERROR, ex.getMessage());
        }
        writeFailedToResponse(response, ApiResult.SERVER_ERROR, ApiResult.SERVER_ERROR.getMessage());
    }

    public static void writeFailedToResponse(HttpServletResponse response, ApiResult resultCode, String message) throws Exception {
        response.setStatus(HttpStatus.OK.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        String body = JSONUtil.toJsonStr(ApiData.error(resultCode, message));
        response.reset();
        response.getWriter().write(body);
        response.getWriter().flush();
    }
}
