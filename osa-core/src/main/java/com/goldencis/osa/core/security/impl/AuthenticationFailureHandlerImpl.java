package com.goldencis.osa.core.security.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldencis.osa.common.entity.ResultMsg;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by limingchao on 2018/10/30.
 */
@Component("authenticationFailureHandlerImpl")
public class AuthenticationFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    Logger log = Logger.getLogger(AuthenticationFailureHandlerImpl.class);

    @Autowired
    private ObjectMapper objectMapper;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //以Json格式返回
        String res = objectMapper.writeValueAsString(ResultMsg.False(exception.getMessage()));

        if ("Bad credentials".equals(exception.getMessage())) {
            res = objectMapper.writeValueAsString(ResultMsg.False("用户名或密码不正确！"));
        }

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(res);
        response.getWriter().flush();
    }

    private static String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
        if (uri == null) {
            uri = request.getRequestURI();
        }
        return uri;
    }
}
