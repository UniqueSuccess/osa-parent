package com.goldencis.osa.core.controller;

import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.DateUtil;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.core.entity.Navigation;
import com.goldencis.osa.core.entity.Resource;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.INavigationService;
import com.goldencis.osa.core.service.IPermissionService;
import com.goldencis.osa.core.utils.AuthUtils;
import com.goldencis.osa.core.utils.ResourceType;
import com.goldencis.osa.core.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by limingchao on 2018/9/25.
 */
@Api(tags = "页面跳转管理")
@Controller
public class PageController implements ServletContextAware {

    private final static Integer SYSTEM_NAVIGATION = 9;
    private final static Integer ABOUT_NAVIGATION = 19;
    private ServletContext servletContext;
    @Autowired
    private INavigationService navigationService;

    @Autowired
    private IPermissionService permissionService;


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


    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @ApiOperation(value = "登录页面")
    @GetMapping(value = "/login")
    @ResponseStatus(HttpStatus.CREATED)
    public String toLogin() {
        return "login";
    }

    @ApiOperation(value = "用户访问系统根目录根据权限跳转第一个带URL的导航页")
    @GetMapping(value = "/")
    public String index() {

        User user = SecurityUtils.getCurrentUser();
        //获取页面权限
        List<Resource> navigationList = permissionService.findUserPermissionsByResourceType(user, ResourceType.NAVIGATION.getValue());

        if (ConstantsDto.CONST_TRUE.equals(ConstantsDto.ISCHECKAUTH)) {
            // 检查授权点数 以及授权期限
            if (!checkAuthInvalid()) {
                navigationList.removeIf(resource -> {
                    if (!(resource instanceof Navigation)) {
                        return true;
                    }
                    Navigation navigation = (Navigation) resource;
                    return !SYSTEM_NAVIGATION.equals(navigation.getId())
                            && !ABOUT_NAVIGATION.equals(navigation.getId());
                });
            }
        }

        if (!ListUtils.isEmpty(navigationList)) {
            for (Resource resource : navigationList) {
                if (resource instanceof Navigation && !StringUtils.isEmpty(((Navigation) resource).getHref())) {
                    return "redirect:" +((Navigation) resource).getHref();
                }
            }
        }
        return "";
    }

    @ApiOperation(value = "系统首页")
    @GetMapping(value = "/homepage/index")
    public String homePage() {
        return "index";
    }

    @ApiOperation(value = "通用导航栏页面")
    @GetMapping(value = "/common/topLeft")
    public String topLeftIndex() {
        return "common/topLeft";
    }

    @ApiOperation(value = "用户列表页面")
    @GetMapping(value = "/user/userList")
    public String userIndex() {
        return "user/userList";
    }

    @ApiOperation(value = "用户组列表页面")
    @GetMapping(value = "/user/userGroupList")
    public String userGroupIndex() {
        return "user/userGroupList";
    }

    @ApiOperation(value = "设备列表页面")
    @GetMapping(value = "/asset/assetList")
    public String assetIndex() {
        return "asset/assetList";
    }

    @ApiOperation(value = "设备组列表页面")
    @GetMapping(value = "/asset/assetGroupList")
    public String assetGroupIndex() {
        return "asset/assetGroupList";
    }

    @ApiOperation(value = "监控--实时页面")
    @GetMapping(value = "/monitor/monitorRelTimeList")
    public String monitorRelTimeIndex() {
        return "monitor/monitorRelTimeList";
    }

    @ApiOperation(value = "监控--历史页面")
    @GetMapping(value = "/monitor/monitorHistoryList")
    public String monitorHistoryIndex() {
        return "monitor/monitorHistoryList";
    }

    @ApiOperation(value = "系统--账号配置")
    @GetMapping(value = "/system/accountConfig")
    public String accountConfigIndex() {
        return "system/accountConfig";
    }

    @ApiOperation(value = "系统--系统配置")
    @GetMapping(value = "/system/systemConfig")
    public String systemConfigIndex() {
        return "system/systemConfig";
    }

    @ApiOperation(value = "系统--日志")
    @GetMapping(value = "/system/logList")
    public String logIndex() {
        return "system/logList";
    }

    @ApiOperation(value = "系统--关于")
    @GetMapping(value = "/system/about")
    public String systemAboutIndex() {
        return "system/about";
    }

    @ApiOperation(value = "审批授权页面")
    @GetMapping(value = "/approval/approvalGrantedList")
    public String approvalGrantedIndex() {
        return "approval/approvalGrantedList";
    }

    @ApiOperation(value = "审批命令页面")
    @GetMapping(value = "/approval/approvalCommandList")
    public String approvalCommandIndex() {
        return "approval/approvalCommandList";
    }

    @ApiOperation(value = "策略页面")
    @GetMapping(value = "/strategy/strategyList")
    public String strategyIndex() {
        return "strategy/strategyList";
    }

    @ApiOperation(value = "策略页面")
    @GetMapping(value = "/task/taskList")
    public String taskIndex() {
        return "task/taskList";
    }

    @ApiOperation(value = "计划页面")
    @GetMapping(value = "/plan/planList")
    public String planIndex() {
        return "plan/planList";
    }

    @ApiOperation(value = "报表--授权资源页面")
    @GetMapping(value = "/report/resource")
    public String reportResourceIndex() {
        return "report/resource";
    }

    @ApiOperation(value = "报表--违规命令页面")
    @GetMapping(value = "/report/command")
    public String reportCommandIndex() {
        return "report/command";
    }

    @ApiOperation(value = "报表--授权用户页面")
    @GetMapping(value = "/report/user")
    public String reportUserIndex() {
        return "report/user";
    }

    @ApiOperation(value = "报表--行为审计页面")
    @GetMapping(value = "/report/action")
    public String reportActionIndex() {
        return "report/action";
    }

    @ApiOperation(value = "单点登录页面")
    @GetMapping(value = "/granted/grantedList")
    public String ssoIndex() {
        return "granted/grantedList";
    }

    @ApiOperation(value = "远程监控")
    @GetMapping(value = "/granted/remote")
    public String remoteIndex() {
        return "granted/remote";
    }

    @ApiOperation(value = "远程桌面")
    @GetMapping(value = "/sso/remote")
    public String remoteSsoIndex() {
        return "sso/remote";
    }

    @ApiOperation(value = "视频回放")
    @GetMapping(value = "/replay/player")
    public String playerIndex() {
        return "/replay/player";
    }

    @ApiOperation(value = "命令回放")
    @GetMapping(value = "/monitor/commandPlay")
    public String commandPlayIndex() {
        return "/monitor/commandPlay";
    }
}
