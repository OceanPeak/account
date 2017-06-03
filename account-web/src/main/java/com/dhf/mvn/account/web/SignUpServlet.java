package com.dhf.mvn.account.web;

import com.dhf.mvn.account.exception.AccountServiceException;
import com.dhf.mvn.account.model.SignUpRequest;
import com.dhf.mvn.account.service.AccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2017/6/3.
 */
public class SignUpServlet extends HttpServlet {
    private ApplicationContext context;
    private AccountService service;

    @Override
    public void init() throws ServletException {
        super.init();
        context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        service = (AccountService) context.getBean("accountService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String email = req.getParameter("email");
        String name = req.getParameter("name");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirm_password");
        String captchaKey = req.getParameter("captcha_key");
        String captchaValue = req.getParameter("captcha_value");

        if (!validParams(id, email, name, password, confirmPassword, captchaKey, captchaValue)) {
            resp.sendError(400, "Parameter Incomplete.");
            return;
        }

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setId(id);
        signUpRequest.setEmail(email);
        signUpRequest.setName(name);
        signUpRequest.setPassword(password);
        signUpRequest.setConfirmPassword(confirmPassword);
        signUpRequest.setCaptchaKey(captchaKey);
        signUpRequest.setCaptchaValue(captchaValue);
        signUpRequest.setActivateServiceUrl(req.getRequestURL().substring(0, req.getRequestURL().lastIndexOf("/")) + "/activate");

        try {
            service.signUp(signUpRequest);
            resp.getWriter().print("Account is created, please check your mail box for activation link");
        } catch (AccountServiceException e) {
            e.printStackTrace();
            resp.sendError(400, e.getMessage());
            return;
        }
    }

    private boolean validParams(String... params) {
        if (null == params || params.length == 0)
            return false;
        for (String param : params)
            if (null == param || param.length() == 0)
                return false;
        return true;
    }
}
