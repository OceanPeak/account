package com.dhf.mvn.account.service.impl;

import com.dhf.mvn.account.captcha.exception.AccountCaptchaException;
import com.dhf.mvn.account.captcha.service.AccountCaptchaService;
import com.dhf.mvn.account.captcha.service.RandomGenerator;
import com.dhf.mvn.account.email.exception.AccountEmailException;
import com.dhf.mvn.account.email.service.AccountEmailService;
import com.dhf.mvn.account.exception.AccountPersistException;
import com.dhf.mvn.account.exception.AccountServiceException;
import com.dhf.mvn.account.model.Account;
import com.dhf.mvn.account.model.SignUpRequest;
import com.dhf.mvn.account.service.AccountPersistService;
import com.dhf.mvn.account.service.AccountService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhf on 2017/6/2.
 */
public class AccountServiceImpl implements AccountService {
    private AccountPersistService accountPersistService;
    private AccountEmailService accountEmailService;
    private AccountCaptchaService accountCaptchaService;
    private Map<String, String> activationMap = new HashMap<>();

    public AccountPersistService getAccountPersistService() {
        return accountPersistService;
    }

    public void setAccountPersistService(AccountPersistService accountPersistService) {
        this.accountPersistService = accountPersistService;
    }

    public AccountEmailService getAccountEmailService() {
        return accountEmailService;
    }

    public void setAccountEmailService(AccountEmailService accountEmailService) {
        this.accountEmailService = accountEmailService;
    }

    public AccountCaptchaService getAccountCaptchaService() {
        return accountCaptchaService;
    }

    public void setAccountCaptchaService(AccountCaptchaService accountCaptchaService) {
        this.accountCaptchaService = accountCaptchaService;
    }

    @Override
    public String generateCaptchaKey() throws AccountServiceException {
        try {
            return accountCaptchaService.generateCaptchaKey();
        } catch (AccountCaptchaException e) {
            e.printStackTrace();
            throw new AccountServiceException("Unable to generate captcha key.", e);
        }
    }

    @Override
    public byte[] generateCaptchaImage(String captchaKey) throws AccountServiceException {
        try {
            return accountCaptchaService.generateCaptchaImage(captchaKey);
        } catch (AccountCaptchaException e) {
            e.printStackTrace();
            throw new AccountServiceException("Unable to generate captcha image.", e);
        }
    }

    @Override
    public void signUp(SignUpRequest signUpRequest) throws AccountServiceException {
        try {
            if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
                throw new AccountServiceException("confirm password do not match.");
            }

            if (!accountCaptchaService.validateCaptcha(signUpRequest.getCaptchaKey(), signUpRequest.getCaptchaValue()))
                throw new AccountServiceException("Incorrect captcha.");

            Account account = new Account();
            account.setId(signUpRequest.getId());
            account.setName(signUpRequest.getName());
            account.setEmail(signUpRequest.getEmail());
            account.setPassword(signUpRequest.getPassword());
            account.setActivated(false);

            accountPersistService.insertAccount(account);

            String activationId = RandomGenerator.getRandomString();
            activationMap.put(activationId, account.getId());

            String link = signUpRequest.getActivateServiceUrl().endsWith("/") ? signUpRequest.getActivateServiceUrl()
                    + activationId : signUpRequest.getActivateServiceUrl() + "?key=" + activationId;

            accountEmailService.sendMail(account.getEmail(), "Please Activate Your Account.", link);

        } catch (AccountCaptchaException e) {
            e.printStackTrace();
            throw new AccountServiceException("Unable to validate captcha.", e);
        } catch (AccountPersistException e) {
            e.printStackTrace();
            throw new AccountServiceException("Unable to create account.", e);
        } catch (AccountEmailException e) {
            e.printStackTrace();
            throw new AccountServiceException("Unable to send email.", e);
        }

    }

    @Override
    public void active(String activationNumber) throws AccountServiceException {
        String accountId = activationMap.get(activationNumber);
        try {
            if (null == accountId)
                throw new AccountServiceException("Invalid account activation id : " + accountId);

            Account account = accountPersistService.getAccount(accountId);
            account.setActivated(true);
            accountPersistService.updateAccount(account);
        } catch (AccountPersistException e) {
            e.printStackTrace();
            throw new AccountServiceException("Unable to activate account, get account failed, id : " + accountId, e);
        }
    }

    @Override
    public void login(String id, String password) throws AccountServiceException {
        try {
            Account account = accountPersistService.getAccount(id);

            if(null == account)
                throw new AccountServiceException("Account does not exist.");

            if(!account.isActivated())
                throw new AccountServiceException("Account is disabled");

            if(!account.getPassword().equals(password))
                throw new AccountServiceException("Incorrect password");
        } catch (AccountPersistException e) {
            e.printStackTrace();
            throw new AccountServiceException("Unable to login in, get account failed, id : " + id, e);
        }
    }
}
