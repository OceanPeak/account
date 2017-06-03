package com.dhf.mvn.account.web;

import com.dhf.mvn.account.exception.AccountServiceException;
import com.dhf.mvn.account.service.AccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Administrator on 2017/6/3.
 */
public class CaptchaImageServlet extends HttpServlet {
    private ApplicationContext context;
    private AccountService service;

    @Override
    public void init() throws ServletException {
        super.init();
        context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        service = (AccountService) context.getBean("accountService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String key = req.getParameter("key");
        if (null == key || key.length() == 0)
            resp.sendError(400, "No Captcha Key Found");

        try {
            resp.setContentType("image/jpeg");
            OutputStream outputStream = resp.getOutputStream();
            outputStream.write(service.generateCaptchaImage(key));
        } catch (AccountServiceException e) {
            e.printStackTrace();
            resp.sendError(404, e.getMessage());
        }
    }
}
