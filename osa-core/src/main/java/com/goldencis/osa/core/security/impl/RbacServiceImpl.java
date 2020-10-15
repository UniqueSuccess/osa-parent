package com.goldencis.osa.core.security.impl;

import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.core.entity.Navigation;
import com.goldencis.osa.core.entity.Operation;
import com.goldencis.osa.core.entity.Resource;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.security.IRbacService;
import com.goldencis.osa.core.service.IPermissionService;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.core.utils.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by limingchao on 2018/10/16.
 */
@Component("rbacService")
public class RbacServiceImpl implements IRbacService {

    private Logger logger = LoggerFactory.getLogger(RbacServiceImpl.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IPermissionService permissionService;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        //获取用户的认证信息
        Object principal = authentication.getPrincipal();
        boolean hasPermission = false;
        if (principal instanceof UserDetails) { //首先判断先当前用户是否是我们UserDetails对象。
            //获取用户对象
            String userName = ((UserDetails) principal).getUsername();
            User user = userService.findUserByUserName(userName);

            //权限的URL集合，用于存放用户所拥有权限的所有URL
            List<RequestMatcher> matchers = getUrlListByUserPermissions(user);

            //增加默认的首页index权限
            matchers.add(new AntPathRequestMatcher("/", HttpMethod.GET.toString()));
            for (RequestMatcher matcher : matchers) {
                if (matcher.matches(request)) {
                    hasPermission = true;
                    break;
                }
            }
        }
        return hasPermission;
    }

    @Cacheable(value = "RbacServiceCache", key = "#user.guid")
    public List<RequestMatcher> getUrlListByUserPermissions(User user) {
        List<RequestMatcher> matchers = new ArrayList<>();
        //获取页面权限
        List<Resource> navigationList = permissionService.findUserPermissionsByResourceType(user, ResourceType.NAVIGATION.getValue());
        if (!ListUtils.isEmpty(navigationList)) {
            navigationList.forEach(resource -> {
                Navigation navigation = (Navigation) resource;
                if (!StringUtils.isEmpty(navigation.getHref())) {
                    if ((navigation).getHref().startsWith("/osa")) {
                        matchers.add(new AntPathRequestMatcher((navigation).getHref().replaceAll("/osa", ""), HttpMethod.GET.toString()));
                    } else {
                        matchers.add(new AntPathRequestMatcher((navigation).getHref(), HttpMethod.GET.toString()));
                    }
                }
            });
        }

        //获取功能权限
        List<Resource> operationList = permissionService.findUserPermissionsByResourceType(user, ResourceType.OPERATION.getValue());
        if (!ListUtils.isEmpty(operationList)) {
            operationList.forEach(resource -> {
                if (!StringUtils.isEmpty(((Operation) resource).getUrlPartten())) {
                    matchers.add(new AntPathRequestMatcher(((Operation) resource).getUrlPartten(), StringUtils.isEmpty(((Operation) resource).getMethod()) ? null : ((Operation) resource).getMethod()));
                }
            });
        }
        return matchers;
    }

    public static List<RequestMatcher> antMatchers(HttpMethod httpMethod, String... antPatterns) {
        String method = httpMethod == null ? null : httpMethod.toString();
        List<RequestMatcher> matchers = new ArrayList<>();
        for (String pattern : antPatterns) {
            matchers.add(new AntPathRequestMatcher(pattern, method));
        }
        return matchers;
    }
}
