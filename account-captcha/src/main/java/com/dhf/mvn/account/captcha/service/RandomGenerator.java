package com.dhf.mvn.account.captcha.service;

import java.util.Random;

/**
 * Created by dhf on 2017/6/1.
 */
//生成随机验证码
public class RandomGenerator {
    private static String range = "0123456789abcdefghijklmnopqrstuvwxyz";

    public static synchronized String getRandomString() {
        Random random = new Random();

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            stringBuilder.append(range.charAt(random.nextInt(range.length())));
        }

        return stringBuilder.toString();
    }
}
