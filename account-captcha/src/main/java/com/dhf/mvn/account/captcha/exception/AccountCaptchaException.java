package com.dhf.mvn.account.captcha.exception;

/**
 * Created by dhf on 2017/6/1.
 */
public class AccountCaptchaException extends Exception {
    public AccountCaptchaException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountCaptchaException(String message) {
        super(message);
    }
}
