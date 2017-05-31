package com.dhf.mvn.account.service;

import com.dhf.mvn.account.exception.AccountPersistException;
import com.dhf.mvn.account.model.Account;

/**
 * Created by Administrator on 2017/5/31.
 */
public interface AccountPersistService {
    Account insertAccount(Account account) throws AccountPersistException;

    Account getAccount(String id) throws AccountPersistException;

    Account updateAccount(Account account) throws AccountPersistException;

    void deleteAccount(String id) throws AccountPersistException;
}
