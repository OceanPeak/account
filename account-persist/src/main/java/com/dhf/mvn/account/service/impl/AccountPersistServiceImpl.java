package com.dhf.mvn.account.service.impl;

import com.dhf.mvn.account.exception.AccountPersistException;
import com.dhf.mvn.account.model.Account;
import com.dhf.mvn.account.service.AccountPersistService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */
public class AccountPersistServiceImpl implements AccountPersistService {
    private static final String ELEMENT_ROOT = "account-persist";
    private static final String ELEMENT_ACCOUNTS = "accounts";
    private static final String ELEMENT_ACCOUNT = "account";
    private static final String ELEMENT_ACCOUNT_ID = "id";
    private static final String ELEMENT_ACCOUNT_NAME = "name";
    private static final String ELEMENT_ACCOUNT_EMAIL = "email";
    private static final String ELEMENT_ACCOUNT_PASSWORD = "password";
    private static final String ELEMENT_ACCOUNT_ACTIVATED = "activated";

    private String file;
    private SAXReader reader = new SAXReader();

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Account insertAccount(Account account) throws AccountPersistException {
        Document document = readDocument();
        Element root = document.getRootElement().element(ELEMENT_ACCOUNTS);
        root.add(buildAccountElement(account));
        writeDocument(document);
        return account;
    }


    public Account getAccount(String id) throws AccountPersistException {
        Document document = readDocument();
        Element accountsElement = document.getRootElement().element(ELEMENT_ACCOUNTS);
        for (Element accountElement : (List<Element>) accountsElement.elements()) {
            if (accountElement.elementText(ELEMENT_ACCOUNT_ID).equals(id))
                return buildAccount(accountElement);
        }
        return null;
    }

    public Account updateAccount(Account account) throws AccountPersistException {
        if (getAccount(account.getId()) != null) {
            deleteAccount(account.getId());
            insertAccount(account);
            return account;
        }
        return null;
    }

    public void deleteAccount(String id) throws AccountPersistException {
        Document document = readDocument();
        Element root = document.getRootElement().element(ELEMENT_ACCOUNTS);
        for (Element element : (List<Element>) root.elements()) {
            if (element.elementText(ELEMENT_ACCOUNT_ID).equals(id)) {
                element.detach();
                writeDocument(document);
                return;
            }
        }
    }

    private Account buildAccount(Element element) {
        Account account = new Account();
        account.setId(element.elementText(ELEMENT_ACCOUNT_ID));
        account.setName(element.elementText(ELEMENT_ACCOUNT_NAME));
        account.setEmail(element.elementText(ELEMENT_ACCOUNT_EMAIL));
        account.setPassword(element.elementText(ELEMENT_ACCOUNT_PASSWORD));
        account.setActivated("true".equals(element.elementText(ELEMENT_ACCOUNT_ACTIVATED)) ? true : false);
        return account;
    }

    private Element buildAccountElement(Account account) {
        Element element = DocumentFactory.getInstance().createElement(ELEMENT_ACCOUNT);
        element.addElement(ELEMENT_ACCOUNT_ID).setText(account.getId());
        element.addElement(ELEMENT_ACCOUNT_NAME).setText(account.getName());
        element.addElement(ELEMENT_ACCOUNT_EMAIL).setText(account.getEmail());
        element.addElement(ELEMENT_ACCOUNT_PASSWORD).setText(account.getPassword());
        element.addElement(ELEMENT_ACCOUNT_ACTIVATED).setText(account.isActivated() ? "true" : "false");
        return element;
    }

    private Document readDocument() throws AccountPersistException {
        File dataFile = new File(file);
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            Document doc = DocumentFactory.getInstance().createDocument();
            Element root = doc.addElement(ELEMENT_ROOT);
            root.addElement(ELEMENT_ACCOUNTS);
            writeDocument(doc);
        }
        try {
            return reader.read(dataFile);
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new AccountPersistException("Unable to read persist data xml", e);
        }
    }

    private void writeDocument(Document document) throws AccountPersistException {
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
            XMLWriter writer = new XMLWriter(out, OutputFormat.createPrettyPrint());
            writer.write(document);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AccountPersistException("Unable to write persist data xml", e);
        } finally {
            try {
                if (null != out)
                    out.close();
            } catch (IOException e) {
                throw new AccountPersistException("Unable to clase persist data xml write", e);
            }
        }
    }
}
