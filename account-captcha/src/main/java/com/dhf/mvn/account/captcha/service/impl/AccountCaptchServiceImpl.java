package com.dhf.mvn.account.captcha.service.impl;

import com.dhf.mvn.account.captcha.exception.AccountCaptchaException;
import com.dhf.mvn.account.captcha.service.AccountCaptchaService;
import com.dhf.mvn.account.captcha.service.RandomGenerator;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.beans.factory.InitializingBean;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dhf on 2017/6/2.
 */
public class AccountCaptchServiceImpl implements AccountCaptchaService, InitializingBean {
    private DefaultKaptcha producer;
    private Map<String, String> captchaMap = new HashMap<>();
    private List<String> preDefinedTexts;
    private int textCount = 0;

    @Override
    public String generateCaptchaKey() throws AccountCaptchaException {
        String key = RandomGenerator.getRandomString();
        String value = getPreDefinedText();
        captchaMap.put(key, value);
        return key;
    }

    @Override
    public byte[] generateCaptchaImage(String captchaKey) throws AccountCaptchaException {
        String text = captchaMap.get(captchaKey);
        if(null == text)
            throw new AccountCaptchaException("Captcha key : " + captchaKey + " not found!");
        BufferedImage image = producer.createImage(text);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", out);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AccountCaptchaException("Failed to write captcha stream!", e);
        }
        return out.toByteArray();
    }

    @Override
    public boolean validateCaptcha(String captchaKey, String captchaValue) throws AccountCaptchaException {
        String text = captchaMap.get(captchaKey);
        if(null == text)
            throw new AccountCaptchaException("Captcha key : " + captchaKey + " not found!");
        if(text.equals(captchaValue)) {
            captchaMap.remove(captchaKey);
            return true;
        }
        return false;
    }

    @Override
    public List<String> getPreDefinedTexts() {
        return preDefinedTexts;
    }

    @Override
    public void setPreDefinedTexts(List<String> preDefinedTexts) {
        this.preDefinedTexts = preDefinedTexts;
    }

    //如果preDefinedTexts有值则按顺序获取其中的值作为value，而preDefinedTexts的值是为了测试用，通过setPreDefinedTexts设置的，所以在非测试环境下实际上
    //preDefinedTexts实际上都是null，value都是通过producer.createText()创建的
    private String getPreDefinedText() {
        if(null != preDefinedTexts && !preDefinedTexts.isEmpty()) {
            String text = preDefinedTexts.get(textCount);
            textCount = (textCount + 1) % preDefinedTexts.size();
            return text;
        } else
            return producer.createText();
    }

    //在SpringFramework初始化对象时调用，这里初始化producer并提供默认配置
    @Override
    public void afterPropertiesSet() throws Exception {
        producer = new DefaultKaptcha();
        producer.setConfig(new Config(new Properties()));
    }
}
