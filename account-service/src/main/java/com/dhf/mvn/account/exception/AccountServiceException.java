package com.dhf.mvn.account.exception;

/**
 * Created by dhf on 2017/6/2.
 */
public class AccountServiceException extends Exception {
    public AccountServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountServiceException(String message) {
        super(message);
    }
}
