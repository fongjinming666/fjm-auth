package com.fjm.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Author: jinmingfong
 * @CreateTime: 2022-05-18 11:45
 * @Description:
 */
public class WechatAuthUtil {

    /**
     * wechat appId
     */
    public static final String APPID = "";

    /**
     * wechat appSecret
     */
    public static final String APPSECRET = "";

    /**
     * 微信第三方授权Url
     */
    public static final String authUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /**
     * 微信用户信息Url
     */
    public static final String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo";

    /**
     * 微信第三方oauth授权
     *
     * @param code
     * @return
     * @throws Exception
     */
    public static JSONObject requestAuthCode(String code) throws Exception {
        String url = authUrl + "?appid=" + APPID + "&secret=" + APPSECRET
                + "&code=" + code + "&grant_type=authorization_code";
        return doGetJson(url);
    }

    /**
     * 获取微信用户信息
     *
     * @param accessToken
     * @param openId
     * @return
     * @throws Exception
     */
    public static JSONObject requestWechatInfo(String accessToken, String openId) throws Exception {
        String url = userInfoUrl + "?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
        return doGetJson(url);
    }

    public static JSONObject doGetJson(String URL) throws IOException {
        JSONObject jsonObject = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        try {
            //创建远程url连接对象
            java.net.URL url = new URL(URL);
            //通过远程url连接对象打开一个连接，强转成HTTPURLConnection类
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //设置连接超时时间和读取超时时间
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(60000);
            conn.setRequestProperty("Accept", "application/json");
            //发送请求
            conn.connect();
            //通过conn取得输入流，并使用Reader读取
            if (200 == conn.getResponseCode()) {
                is = conn.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line);
                    System.out.println(line);
                }
            } else {
                System.out.println("ResponseCode is an error code:" + conn.getResponseCode());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            conn.disconnect();
        }
        jsonObject = JSONObject.parseObject(result.toString());
        return jsonObject;
    }
}
