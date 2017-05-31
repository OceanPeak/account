package com.dhf.mvn.account.email.service;

import com.dhf.mvn.account.email.exception.AccountEmailException;

/**
 * Created by Administrator on 2017/5/30.
 */
public interface AccountEmailService {
    void sendMail(String to, String subject, String htmlText) throws AccountEmailException;
}
