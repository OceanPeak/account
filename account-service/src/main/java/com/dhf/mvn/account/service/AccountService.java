package com.dhf.mvn.account.service;

import com.dhf.mvn.account.exception.AccountServiceException;
import com.dhf.mvn.account.model.SignUpRequest;

/**
 * Created by dhf on 2017/6/2.
 */
public interface AccountService {
    String generateCaptchaKey() throws AccountServiceException;

    byte[] generateCaptchaImage(String captchaKey) throws AccountServiceException;

    void signUp(SignUpRequest signUpRequest) throws AccountServiceException;

    void active(String activationNumber) throws AccountServiceException;

    void login(String id, String password) throws AccountServiceException;
}
