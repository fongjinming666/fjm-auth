package com.fjm.domain;


import java.util.Arrays;
import java.util.List;

public interface AuthConstant {

    /**
     * 认证信息Http请求头
     */
    String JWT_TOKEN_HEADER = "Authorization";

    /**
     * JWT令牌前缀
     */
    String JWT_TOKEN_PREFIX = "Bearer ";

    String API_REQUEST_START = "start_";

    String API_REQUEST_END = "end_t";

    String JWT_CLIENT_ID_KEY = "clientId";

    String JWT_USER_ID_KEY = "id";

    String JWT_USER_NAME_KEY = "username";

    String JWT_USER_PHONE_KEY = "phone";

    String JWT_USER_SMSCODE_KEY = "smsCode";

    String JWT_USER_SOURCE_KEY = "source";

    String JWT_USER_ROLE_KEY = "roleId";

    String JWT_APPUSER_NICKNAME_KEY = "nickname";

    String JWT_APPUSER_ID_KEY = "appUserId";

    String JWT_APPUSER_NAME_KEY = "appUsername";

    String PARAMETERS_CLIENT_ID = "client_id";

    String PARAMETERS_CLIENT_SECRET = "client_secret";

    String PARAMETERS_CLIENT_GRANTTYPE = "grant_type";

    String PARAMETERS_CLIENT_SCOPE = "scope";

    String PARAMETERS_CLIENT_PASSWORD = "password";

    /**
     * 用户信息Http请求头
     */
    String USER_TOKEN_HEADER = "user";

    /**
     * JWT存储权限前缀
     */
    String AUTHORITY_PREFIX = "ROLE_";

    /**
     * JWT存储权限属性
     */
    String AUTHORITY_CLAIM_NAME = "authorities";

    /**
     * Redis缓存权限规则key
     */
    String RESOURCE_ROLES_MAP_KEY = "auth:resourceRolesMap";
}
