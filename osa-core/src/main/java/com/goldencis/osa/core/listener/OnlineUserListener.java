package com.goldencis.osa.core.listener;

import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.List;
import java.util.Map;

public class OnlineUserListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        ServletContext application = session.getServletContext();
        // 登录账户
        String username = (String) session.getAttribute("username");
        // 从在线列表中删除登录账户名
        List onlineUserList = (List) application.getAttribute("onlineUserList");
        if (!StringUtils.isEmpty(username) && !ListUtils.isEmpty(onlineUserList)){
            onlineUserList.remove(username);
        }

        Map<String, HttpSession> onlineDevuniqueMap = (Map<String, HttpSession>) application.getAttribute("onlineDevuniqueMap");
        if (onlineDevuniqueMap != null) {
            String devunique = (String) session.getAttribute("devunique");
            onlineDevuniqueMap.remove(devunique);
        }
    }
}
