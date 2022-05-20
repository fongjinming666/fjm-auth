package com.fjm.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwk.Jwk;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.client.RestTemplate;

import java.security.PublicKey;

/**
 * @Author 龙宇文
 * @Description Apple相关工具类
 * @Date 下午16:28 2020/9/25
 */
@Slf4j
public class AppleUtil {
    private static final String APPLE_ID_URL = "https://appleid.apple.com";

    /**
     * 获取苹果的公钥
     * l
     *
     * @return
     * @throws Exception
     */
    private static JSONArray getAuthKeys() throws Exception {
        String url = APPLE_ID_URL + "/auth/keys";
        RestTemplate restTemplate = new RestTemplate();
        JSONObject json = restTemplate.getForObject(url, JSONObject.class);
        JSONArray arr = json.getJSONArray("keys");
        return arr;
    }

    public static void main(String[] args) throws Exception {
        String sub = "";
        String identityToken = "";
        if (verify(identityToken, sub)) {
            System.out.println("成功");
        } else {
            System.out.println("失败");
        }
    }

    public static boolean verify(String jwt, String subParam) {
        JSONArray arr = null;
        try {
            arr = getAuthKeys();
            if (arr == null) {
                return false;
            }

            //先取苹果第一个key进行校验
            JSONObject authKey = JSONObject.parseObject(arr.getString(0));
            if (verifyExc(jwt, authKey, subParam)) {
                return true;
            } else {
                //再取第二个key校验
                authKey = JSONObject.parseObject(arr.getString(1));
                return verifyExc(jwt, authKey, subParam);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 对前端传来的identityToken进行验证
     *
     * @param jwt     对应前端传来的 identityToken
     * @param authKey 苹果的公钥 authKey
     * @return
     * @throws Exception
     */
    public static boolean verifyExc(String jwt, JSONObject authKey, String subParam) throws Exception {
        Jwk jwa = Jwk.fromValues(authKey);
        PublicKey publicKey = jwa.getPublicKey();
        String aud = "";
        String sub = "";

        if (jwt.split("\\.").length > 1) {
            String claim = new String(Base64.decodeBase64(jwt.split("\\.")[1]));
            aud = JSONObject.parseObject(claim).get("aud").toString();
            sub = JSONObject.parseObject(claim).get("sub").toString();
        }

        if (!sub.trim().equals(subParam)) {
            log.error("AppleID user参数有误。");
            return false;
        }
        JwtParser jwtParser = Jwts.parser().setSigningKey(publicKey);
        jwtParser.requireIssuer(APPLE_ID_URL);
        jwtParser.requireAudience(aud);
        jwtParser.requireSubject(sub);

        try {
            Jws<Claims> claim = jwtParser.parseClaimsJws(jwt);
            if (claim != null && claim.getBody().containsKey("auth_time")) {
                System.out.println(claim);
                return true;
            }
            return false;
        } catch (ExpiredJwtException e) {
            log.error("apple identityToken expired", e);
            return false;
        } catch (Exception e) {
            log.error("apple identityToken illegal", e);
            return false;
        }
    }

    /**
     * 对前端传来的JWT字符串identityToken的第二部分进行解码
     * 主要获取其中的aud和sub，aud大概对应ios前端的包名，sub大概对应当前用户的授权的openID
     *
     * @param identityToken
     * @return {"aud":"com.xkj.****","sub":"000***.8da764d3f9e34d2183e8da08a1057***.0***","c_hash":"UsKAuEoI-****","email_verified":"true","auth_time":1574673481,"iss":"https://appleid.apple.com","exp":1574674081,"iat":1574673481,"email":"****@qq.com"}
     */
    public static JSONObject parserIdentityToken(String identityToken) {
        String[] arr = identityToken.split("\\.");
        String decode = new String(Base64.decodeBase64(arr[1]));
        String substring = decode.substring(0, decode.indexOf("}") + 1);
        JSONObject jsonObject = JSON.parseObject(substring);
        return jsonObject;
    }
}
