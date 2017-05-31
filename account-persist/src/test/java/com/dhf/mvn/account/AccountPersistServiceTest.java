package com.dhf.mvn.account;

import com.dhf.mvn.account.exception.AccountPersistException;
import com.dhf.mvn.account.model.Account;
import com.dhf.mvn.account.service.AccountPersistService;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

/**
 * Created by Administrator on 2017/5/31.
 */
public class AccountPersistServiceTest {
    private AccountPersistService accountPersistService;

    @Before
    public void before() throws AccountPersistException {
        File dataFile = new File("target/test-classes/persist-data.xml");

        if(dataFile.exists())
            dataFile.delete();

        ApplicationContext ctx = new ClassPathXmlApplicationContext("account-persist.xml");
        accountPersistService = (AccountPersistService) ctx.getBean("accountPersistService");

        Account account = new Account();
        account.setId("001");
        account.setName("dhf");
        account.setEmail("donghf@yeah.net");
        account.setPassword("123456");
        account.setActivated(true);

        accountPersistService.insertAccount(account);
    }

    @Test
    public void testReadAccount() throws AccountPersistException {
        Account account = accountPersistService.getAccount("001");
        assertNotNull(account);
        assertEquals("dhf", account.getName());
        assertEquals("donghf@yeah.net", account.getEmail());
        assertEquals("123456", account.getPassword());
        assertTrue(account.isActivated());
    }

    @Test
    public void testUpdateAccount() throws AccountPersistException {
        Account account = accountPersistService.getAccount("001");
        account.setPassword("654321");
        accountPersistService.updateAccount(account);
        account = accountPersistService.getAccount("001");
        assertNotNull(account);
        assertEquals("654321", account.getPassword());
    }
}
