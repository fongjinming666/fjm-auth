package com.fjm.translator;

import com.fjm.exception.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-30 下午4:59
 * @Description:
 */
public class OauthExceptionTranslator extends DefaultWebResponseExceptionTranslator {

    //@Resource
    //private CustomMapper customMapper;

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
        if (e instanceof BadRequestException) {
            throw e;
        }
        return super.translate(e);
    }
}
