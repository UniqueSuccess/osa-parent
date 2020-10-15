package com.goldencis.osa.core.security.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.core.entity.LogSystem;
import com.goldencis.osa.core.service.ILogSystemService;
import com.goldencis.osa.core.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by limingchao on 2018/10/31.
 */
@Component("logoutHandlerImpl")
public class LogoutHandlerImpl extends SecurityContextLogoutHandler {

    @Autowired
    private ILogSystemService systemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        try {
            boolean isNoraml = SecurityUtils.isNormalUser();
            LogSystem log = new LogSystem(isNoraml ? LogType.OPERATION_LOGINOUT.getValue() : LogType.SYSTEM_LOGINOUT.getValue(), isNoraml ? "运维-退出" : "操作-退出", "退出堡垒机系统", "LogoutHandlerImpl.logout(..) invoke");
            systemService.saveLog(log);

            super.logout(request, response, authentication);
            //以Json格式返回
            String res = objectMapper.writeValueAsString(ResultMsg.ok());

            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(res);
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
