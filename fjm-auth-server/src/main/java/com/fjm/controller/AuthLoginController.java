package com.fjm.controller;

import cn.hutool.core.util.StrUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.fasterxml.jackson.annotation.JsonView;
import com.fjm.bo.AuthUserPageQuery;
import com.fjm.dao.IOauthUserDao;
import com.fjm.domain.AuthConstant;
import com.fjm.emun.DeleteEnum;
import com.fjm.emun.GenderEnum;
import com.fjm.emun.StatusEnum;
import com.fjm.entity.OauthUser;
import com.fjm.mobile.ValidateOauthCodeException;
import com.fjm.service.IOauthUserService;
import com.fjm.vo.AppCodeLoginVo;
import com.fjm.vo.VerificationCodeVo;
import com.fjm.constants.FromOauthLoginConstant;
import com.fjm.emun.ApiResult;
import com.fjm.exception.BadRequestException;
import com.fjm.mapper.CustomMapper;
import com.fjm.model.ApiData;
import com.fjm.properties.AliyunSmsProperties;
import com.fjm.service.RedisService;
import com.fjm.store.OauthTokenStore;
import com.fjm.utils.Constants;
import com.fjm.utils.ValidationUtils;
import com.fjm.utils.sms.SmsResponse;
import com.fjm.utils.sms.SmsUtil;
import com.fjm.utils.validate.RandomCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.management.Query;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-04-23 上午11:08
 * @Description:
 */
@Api(tags = "AuthLoginController", description = "第三方登陆授权相关方法")
@RestController
@AllArgsConstructor
@Slf4j
public class AuthLoginController {

    @Resource
    private OauthTokenStore oauthTokenStore;

    @Resource
    private RedisService redisService;

    @Resource
    private CustomMapper customMapper;

    @Resource
    private DefaultAcsClient defaultAcsClient;

    @Resource
    private AliyunSmsProperties aliyunSmsProperties;

    @Resource
    private IOauthUserService oauthUserService;

    @ApiOperation(value = "获取第三方登陆授权的短信")
    @PostMapping("/oauth/login/code")
    public ApiData<Boolean> sendOauthLoginCode(@Valid() @RequestBody VerificationCodeVo verificationCodeVo) throws Exception {
        /** 默认中国大陆. */
        String smsCode = StringUtils.isBlank(verificationCodeVo.getSmsCode()) ? "86" : verificationCodeVo.getSmsCode();
        /**手机号校验 默认中国大陆手机号校验. */
        boolean isPhonelegal = ValidationUtils.isPhoneNumberValid(verificationCodeVo.getPhone(), smsCode);
        if (!isPhonelegal) {
            throw new BadRequestException(ApiResult.INVALID_MOBILE_PHONE_NUMBER);
        }
        String code = RandomCode.random(6, true);
        Map codeMap = new HashMap<>();
        codeMap.put("code", code);
        boolean sendState = false;
        SmsUtil.sendMsg(defaultAcsClient, verificationCodeVo.getPhone(), aliyunSmsProperties.getSignName(), aliyunSmsProperties.getLoginTemplateCode(), customMapper.writeValueAsString(codeMap), new SmsResponse() {

            @Override
            public void onSuccess(List<String> phones, SendSmsResponse sendSmsResponse) {
                log.info("aliyun send sms,{}", "send sms is success :" + sendSmsResponse.getMessage());
                redisService.set(FromOauthLoginConstant.OAUTH_MOBILE_LOGIN_CODE + smsCode + verificationCodeVo.getPhone(), code,
                        Duration.ofMinutes(Constants.RANDOMCODE_MINUTES).getSeconds());
            }

            @Override
            public void onFail(SendSmsResponse sendSmsResponse) {
                log.info("aliyun send sms,{}", "send sms is failed :" + sendSmsResponse.getMessage());
                throw new BadRequestException(ApiResult.SERVER_ERROR, "云短信发送失败");
            }
        });
        return ApiData.ok(sendState);
    }

    @ApiOperation(value = "检测第三方授权的用户登陆信息")
    @PostMapping("/oauth/login/check")
    public ApiData<Boolean> oauthLoginCheck(@Valid() @RequestBody AppCodeLoginVo appCodeLoginVo) {
        /** 默认中国大陆. */
        String smsCode = StringUtils.isBlank(appCodeLoginVo.getSmsCode()) ? "86" : appCodeLoginVo.getSmsCode();
        /** 同步生成用户资源. */
        oauthUserService.syncOauthUser(appCodeLoginVo.getSmsCode(), appCodeLoginVo.getPhone(), new HashMap<>());
        /**手机号校验 默认中国大陆手机号校验. */
        boolean isPhonelegal = ValidationUtils.isPhoneNumberValid(appCodeLoginVo.getPhone(), smsCode);
        if (!isPhonelegal) {
            throw new BadRequestException(ApiResult.INVALID_MOBILE_PHONE_NUMBER);
        }
        /** 验证码判断. */
        String codeInRedis = redisService.getString(FromOauthLoginConstant.OAUTH_MOBILE_LOGIN_CODE + smsCode + appCodeLoginVo.getPhone());
        log.info("oauth real sms verificationCode:{},codeInRedis:{}", appCodeLoginVo.getVerificationCode(), codeInRedis);
        if (StringUtils.isBlank(codeInRedis)) {
            throw new ValidateOauthCodeException(ApiResult.CODE_INVALID);
        } else if (!codeInRedis.equals(appCodeLoginVo.getVerificationCode())) {
            throw new ValidateOauthCodeException(ApiResult.AUTH_CODE_ERROR);
        }
        return ApiData.ok();
    }

    @ApiOperation(value = "退出登陆")
    @GetMapping("/oauth/revokeToken")
    public void revokeToken(HttpServletRequest httpServletRequest) {
        String authAccessToken = httpServletRequest.getHeader(AuthConstant.JWT_TOKEN_HEADER);
        if (StrUtil.isEmpty(authAccessToken)) {
            throw new BadRequestException(ApiResult.FORBIDDEN);
        }
        authAccessToken = authAccessToken.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
        OAuth2AccessToken existingAccessToken = oauthTokenStore.readAccessToken(authAccessToken);
        OAuth2RefreshToken refreshToken;
        if (existingAccessToken != null) {
            if (existingAccessToken.getRefreshToken() != null) {
                //LOGGER.info("remove refreshToken!", existingAccessToken.getRefreshToken());
                refreshToken = existingAccessToken.getRefreshToken();
                oauthTokenStore.removeRefreshToken(refreshToken);
            }
            //LOGGER.info("remove existingAccessToken!", existingAccessToken);
            oauthTokenStore.removeAccessToken(existingAccessToken);
        }
        return;
    }
}
