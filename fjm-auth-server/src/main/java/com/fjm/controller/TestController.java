package com.fjm.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-09-22 上午10:29
 * @Description:
 */
@Slf4j
@RestController
@Api(tags = "TestController", description = "授权中心测试方法")
public class TestController {

    /**
     * 第三方授权模式调试demo
     *
     * @param code
     * @param httpServletRequest
     */
    @ApiOperation(value = "第三方授权模式调试demo")
    @GetMapping("/oauth/authorize_token")
    public String authorizeToken(@ApiIgnore @RequestParam(value = "code", required = false, defaultValue = "") String code, HttpServletRequest httpServletRequest) {
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(1);
        log.info("receive code {}", code);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("client_id", "fjm-admin");
        params.add("client_secret", "fjm123456");
        params.add("redirect_uri", "http://127.0.0.1:8021/oauth/authorize_token");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://127.0.0.1:8013/oauth/token", requestEntity, String.class);
        String token = response.getBody();
        log.info("token => {}", token);
        return token;
    }
}
