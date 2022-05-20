package com.fjm.utils.validate;

import java.util.Random;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-25 下午3:46
 * @Description: 随机生成 验证码
 */
public class RandomCode {

    private static final char[] MORE_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final Random RANDOM = new Random();

    /**
     * 随机生成验证码
     *
     * @param length 长度
     * @param end    结束长度
     * @return 结果
     */
    private static String random(Integer length, Integer end) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(MORE_CHAR[RANDOM.nextInt(end)]);
        }
        return result.toString();
    }

    /**
     * 随机生成验证码
     *
     * @param length  长度
     * @param onlyNum 是否只要数字
     * @return 结果
     */
    public static String random(Integer length, Boolean onlyNum) {
        return onlyNum ? random(length, 10) : random(length, MORE_CHAR.length);
    }

    /**
     * 随机验证码
     *
     * @param length 长度
     * @return 结果
     */
    public static String random(Integer length) {
        return random(length, false);
    }
}
