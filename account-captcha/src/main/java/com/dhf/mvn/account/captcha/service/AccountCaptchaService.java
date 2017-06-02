package com.dhf.mvn.account.captcha.service;

import com.dhf.mvn.account.captcha.exception.AccountCaptchaException;

import java.util.List;

/**
 * Created by dhf on 2017/6/1.
 */
public interface AccountCaptchaService {
    String generateCaptchaKey() throws AccountCaptchaException;

    byte[] generateCaptchaImage(String captchaKey) throws AccountCaptchaException;

    boolean validateCaptcha(String captchaKey, String captchaValue) throws AccountCaptchaException;

    List<String> getPreDefinedTexts();

    //因为生成的验证码都是随机的，所以需要预定义验证码图片内容，这样才能测试
    void setPreDefinedTexts(List<String> preDefinedTexts);
}
