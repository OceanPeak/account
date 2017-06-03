package com.dhf.mvn.account.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dhf.mvn.account.exception.AccountServiceException;
import com.dhf.mvn.account.service.AccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class LoginServlet
        extends HttpServlet {
    private static final long serialVersionUID = 929160785365121624L;

    private ApplicationContext context;
    private AccountService service;

    @Override
    public void init()
            throws ServletException {
        super.init();
        context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        service = (AccountService) context.getBean("accountService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException,
            IOException {
        String id = req.getParameter("id");
        String password = req.getParameter("password");

        if (id == null || id.length() == 0 || password == null || password.length() == 0) {
            resp.sendError(400, "incomplete parameter");
            return;
        }

        try {
            service.login(id, password);
            resp.getWriter().print("Login Successful!");
        } catch (AccountServiceException e) {
            resp.sendError(400, e.getMessage());
        }
    }
}
