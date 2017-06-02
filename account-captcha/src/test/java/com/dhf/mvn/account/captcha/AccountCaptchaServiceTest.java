package com.dhf.mvn.account.captcha;

import com.dhf.mvn.account.captcha.service.AccountCaptchaService;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhf on 2017/6/2.
 */
public class AccountCaptchaServiceTest {
    private AccountCaptchaService accountCaptchaService;

    @Before
    public void before() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("account-captcha.xml");
        accountCaptchaService = (AccountCaptchaService) ctx.getBean("accountCaptchaService");
    }

    @Test
    public void testGeneratorCaptcha() throws Exception {
        String captchaKey = accountCaptchaService.generateCaptchaKey();
        assertNotNull(captchaKey);

        byte[] captchaImage = accountCaptchaService.generateCaptchaImage(captchaKey);
        assertTrue(captchaImage.length > 0);

        File image = new File("target/" + captchaKey + ".jpg");
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(image);
            outputStream.write(captchaImage);
        } finally {
            if(null != outputStream)
                outputStream.close();
            assertTrue(image.exists() && image.length() > 0);
        }
    }

    @Test
    public void testValidateCaptchaCorrect() throws Exception {
        List<String> preDefinedTexts = new ArrayList<>();
        preDefinedTexts.add("12345");
        accountCaptchaService.setPreDefinedTexts(preDefinedTexts);

        String captchaKey = accountCaptchaService.generateCaptchaKey();
        accountCaptchaService.generateCaptchaImage(captchaKey);
        assertTrue(accountCaptchaService.validateCaptcha(captchaKey, "12345"));
    }
}
