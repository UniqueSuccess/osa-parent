package com.goldencis.osa.core.controller;


import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.DateUtil;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.core.entity.Navigation;
import com.goldencis.osa.core.entity.Resource;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.INavigationService;
import com.goldencis.osa.core.service.IPermissionService;
import com.goldencis.osa.core.service.IUserRoleService;
import com.goldencis.osa.core.utils.AuthUtils;
import com.goldencis.osa.core.utils.ResourceType;
import com.goldencis.osa.core.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 页签-导航信息表 前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/navigation")
public class NavigationController implements ServletContextAware {

    private final static Integer SYSTEM_NAVIGATION = 9;
    private final static Integer ABOUT_NAVIGATION = 19;

    @Autowired
    private INavigationService navigationService;
    @Autowired
    private IUserRoleService userRoleService;
    @Autowired
    private IPermissionService permissionService;
    private ServletContext servletContext;

    @ApiOperation(value = "获取用户权限的全部菜单")
    @GetMapping(value = "/getNavigationTreeByUser")
    public ResultMsg getNavigationTreeByUser() {
        try {
            User user = SecurityUtils.getCurrentUser();
            if (Objects.isNull(user)) {
                return ResultMsg.False("获取当前登录用户失败");
            }

            //获取页面权限
            List<Resource> resourceList = permissionService.findUserPermissionsByResourceType(user, ResourceType.NAVIGATION.getValue());

            List<Navigation> navigationList = new ArrayList<>();
            for (Resource resource : resourceList) {
                if (resource instanceof Navigation && ((Navigation) resource).isVisible()) {
                    navigationList.add((Navigation) resource);
                }
            }

            //将菜单集合转化为菜单树
            if (!ListUtils.isEmpty(navigationList)) {
                navigationList = navigationService.formatNavigationTree(navigationList);
            }

            if (ConstantsDto.CONST_TRUE.equals(ConstantsDto.ISCHECKAUTH)) {
                // 检查授权点数 以及授权期限
                if (!checkAuthInvalid()) {
                    return ResultMsg.ok(about(navigationList));
                }
            }

            //菜单子集 按 compositor排序，默认升序排列
            navigationList.stream()
                    .filter(navigation -> navigation.getSub() != null)
                    .forEach(navigation -> navigation.getSub()
                            .sort((r1, r2) -> {
                                if (r1 instanceof Navigation && (r2 instanceof Navigation)) {
                                    return Integer.compare(((Navigation) r1).getCompositor(), ((Navigation) r2).getCompositor());
                                }
                                return 0;
                            }));

            return ResultMsg.ok(navigationList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(500, e.getMessage());
        }
    }

    /**
     * 关于页面
     * @return
     * @param list
     */
    private List<Navigation> about(List<Navigation> list) {
        list.removeIf(navigation -> !SYSTEM_NAVIGATION.equals(navigation.getId()));
        list.forEach(item -> item.getSub().removeIf(resource -> {
            if (!(resource instanceof Navigation)) {
                return true;
            }
            Navigation navigation = (Navigation) resource;
            return !ABOUT_NAVIGATION.equals(navigation.getId());
        }));
        return list;
    }

    /**
     * 检查授权是否有效
     * @return
     */
    private boolean checkAuthInvalid() {
        Map<String, Object> authInfo = AuthUtils.getAuthInfo(servletContext);
        if (!StringUtils.isEmpty(authInfo.get("authmsg"))) {
            return false;
        }
        // 授权有效期
        int expiry = 1;
        String currentDate = DateUtil.getNowDate();
        String endDate = authInfo.get("endDate").toString();
        if (!ConstantsDto.LONG_TIME_LIMIT.equals(endDate)) {
            try {
                expiry = DateUtil.daysBetween(currentDate, endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return expiry > 0;
    }

    /**
     * Set the {@link ServletContext} that this object runs in.
     * <p>Invoked after population of normal bean properties but before an init
     * callback like InitializingBean's {@code afterPropertiesSet} or a
     * custom init-method. Invoked after ApplicationContextAware's
     * {@code setApplicationContext}.
     *
     * @param servletContext ServletContext object to be used by this object
     * @see InitializingBean#afterPropertiesSet
     * @see ApplicationContextAware#setApplicationContext
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
