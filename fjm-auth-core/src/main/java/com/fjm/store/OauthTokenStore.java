package com.fjm.store;

import com.fjm.dao.IOauthAccessTokenDao;
import com.fjm.dao.IOauthRefreshTokenDao;
import com.fjm.entity.OauthAccessToken;
import com.fjm.entity.OauthRefreshToken;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午2:04
 * @Description:
 */
public class OauthTokenStore implements TokenStore {

    @Resource
    private IOauthAccessTokenDao oauthAccessTokenDao;

    @Resource
    private IOauthRefreshTokenDao oauthRefreshTokenDao;

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken accessToken) {
        return readAuthentication(accessToken.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        Query query = new Query();
        query.addCriteria(Criteria.where(OauthAccessToken.TOKEN_ID).is(extractTokenKey(token)));

        OauthAccessToken oauthAccessToken = oauthAccessTokenDao.findOne(query);
        return oauthAccessToken != null ? oauthAccessToken.getAuthentication() : null;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String refreshToken = null;
        if (accessToken.getRefreshToken() != null) {
            refreshToken = accessToken.getRefreshToken().getValue();
        }

        if (readAccessToken(accessToken.getValue()) != null) {
            this.removeAccessToken(accessToken);
        }

        OauthAccessToken oauthAccessToken = new OauthAccessToken();
        oauthAccessToken.setTokenId(extractTokenKey(accessToken.getValue()));
        oauthAccessToken.setAccessToken(accessToken);
        oauthAccessToken.setAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
        oauthAccessToken.setUsername(authentication.isClientOnly() ? null : authentication.getName());
        oauthAccessToken.setClientId(authentication.getOAuth2Request().getClientId());
        oauthAccessToken.setAuthentication(authentication);
        oauthAccessToken.setRefreshToken(extractTokenKey(refreshToken));
        oauthAccessTokenDao.insert(oauthAccessToken);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        Query query = new Query();
        query.addCriteria(Criteria.where(OauthAccessToken.TOKEN_ID).is(extractTokenKey(tokenValue)));

        OauthAccessToken oauthAccessToken = oauthAccessTokenDao.findOne(query);
        return oauthAccessToken != null ? oauthAccessToken.getAccessToken() : null;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken oAuth2AccessToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where(OauthAccessToken.TOKEN_ID).is(extractTokenKey(oAuth2AccessToken.getValue())));
        oauthAccessTokenDao.delete(query);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        OauthRefreshToken token = new OauthRefreshToken();
        token.setTokenId(extractTokenKey(refreshToken.getValue()));
        token.setToken(refreshToken);
        token.setAuthentication(authentication);
        oauthRefreshTokenDao.insert(token);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        Query query = new Query();
        query.addCriteria(Criteria.where(OauthRefreshToken.TOKEN_ID).is(extractTokenKey(tokenValue)));

        OauthRefreshToken oauthRefreshToken = oauthRefreshTokenDao.findOne(query);
        return oauthRefreshToken != null ? oauthRefreshToken.getToken() : null;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken refreshToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where(OauthRefreshToken.TOKEN_ID).is(extractTokenKey(refreshToken.getValue())));

        OauthRefreshToken oauthRefreshToken = oauthRefreshTokenDao.findOne(query);
        return oauthRefreshToken != null ? oauthRefreshToken.getAuthentication() : null;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where(OauthRefreshToken.TOKEN_ID).is(extractTokenKey(refreshToken.getValue())));
        oauthRefreshTokenDao.delete(query);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where(OauthAccessToken.REFRESH_TOKEN).is(extractTokenKey(refreshToken.getValue())));
        oauthRefreshTokenDao.delete(query);
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;
        String authenticationId = authenticationKeyGenerator.extractKey(authentication);

        Query query = new Query();
        query.addCriteria(Criteria.where(OauthAccessToken.AUTHENTICATION_ID).is(authenticationId));

        OauthAccessToken oauthAccessToken = oauthAccessTokenDao.findOne(query);
        if (oauthAccessToken != null) {
            accessToken = oauthAccessToken.getAccessToken();
            if (accessToken != null && !authenticationId.equals(this.authenticationKeyGenerator.extractKey(this.readAuthentication(accessToken)))) {
                this.removeAccessToken(accessToken);
                this.storeAccessToken(accessToken, authentication);
            }
        }
        return accessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String username) {
        Collection<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
        Query query = new Query();
        query.addCriteria(Criteria.where(OauthAccessToken.CLIENT_ID).is(clientId));
        query.addCriteria(Criteria.where(OauthAccessToken.USERNAME).is(username));
        List<OauthAccessToken> accessTokens = oauthAccessTokenDao.find(query);
        for (OauthAccessToken accessToken : accessTokens) {
            tokens.add(accessToken.getAccessToken());
        }
        return tokens;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        Collection<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
        Query query = new Query();
        query.addCriteria(Criteria.where(OauthAccessToken.CLIENT_ID).is(clientId));
        List<OauthAccessToken> accessTokens = oauthAccessTokenDao.find(query);
        for (OauthAccessToken accessToken : accessTokens) {
            tokens.add(accessToken.getAccessToken());
        }
        return tokens;
    }

    private String extractTokenKey(String value) {
        if (value == null) {
            return null;
        } else {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var5) {
                throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
            }

            try {
                byte[] e = digest.digest(value.getBytes("UTF-8"));
                return String.format("%032x", new Object[]{new BigInteger(1, e)});
            } catch (UnsupportedEncodingException var4) {
                throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
            }
        }
    }
}
