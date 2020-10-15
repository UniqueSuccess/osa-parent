package com.goldencis.osa.core.security.impl;

import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.core.entity.LogSystem;
import com.goldencis.osa.core.entity.UserRole;
import com.goldencis.osa.core.mapper.UserRoleMapper;
import com.goldencis.osa.core.service.ILogSystemService;
import com.goldencis.osa.core.utils.SecurityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 重写成功登陆后跳转实现， 不同角色跳转到不同页面
 * @author
 *
 */
@Component("authenticationSuccessHandlerImpl")
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    Logger log = Logger.getLogger(AuthenticationSuccessHandlerImpl.class);

    @Autowired
    private ILogSystemService systemService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
        boolean isNoraml = SecurityUtils.isNormalUser();
        LogSystem log = new LogSystem(isNoraml ? LogType.OPERATION_LOGININ.getValue() : LogType.SYSTEM_LOGININ.getValue(), isNoraml ? "运维-登录" : "操作-登录", "登录堡垒机系统", "AuthenticationSuccessHandlerImpl.onAuthenticationSuccess(..) invoke");
        systemService.saveLog(log);

        //用户在线维护
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        String username = request.getParameter("username");
        List onlineUserList = (List) servletContext.getAttribute("onlineUserList");
        if (!StringUtils.isEmpty(username)) {
            if (null == onlineUserList) {
                onlineUserList = new ArrayList();
                servletContext.setAttribute("onlineUserList", onlineUserList);
            }
            UserRole userRole = userRoleMapper.findNormalRoleRelationByUsername(username);
            if (userRole != null) {
                // 把登录用户保存至session
                session.setAttribute("username", username);
                onlineUserList.add(username);
            }

        }
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        String url = "/login?error=ROLE_NOACCESS";
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            if ("ROLE_USER".equals(grantedAuthority.getAuthority())) {
                url = "/loginsuccess";
                break;
            }
        }
        return url;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }
}
