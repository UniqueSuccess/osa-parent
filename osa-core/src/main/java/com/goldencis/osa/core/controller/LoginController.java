package com.goldencis.osa.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.HttpKit;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.core.entity.Navigation;
import com.goldencis.osa.core.entity.Resource;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.IPermissionService;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.core.utils.ResourceType;
import com.goldencis.osa.core.utils.SecurityUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by limingchao on 2018/10/29.
 */
@RestController
public class LoginController {

    @Autowired
    private IPermissionService permissionService;
    @Autowired
    private IUserService userService;

    @GetMapping(value = "/loginsuccess")
    public ResultMsg loginsuccess() {
        User user = SecurityUtils.getCurrentUser();
        if (user != null) {
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userService.updateLastLoginTime(user);
        }
        HttpKit.getRequest().getSession().setAttribute("url", getRedirectUrl(user));

        //登录成功之后，增加返回值 （用户id，策略id）,用于前端 与 客户端交互
        JSONObject jsonObject = new JSONObject();
        //      {
        //    "userGuid":"abcdef-1111-2222-3333-4444",            //运维用户session
        //	"userName":"zs",           						//用户名
        //	"userTrueName":"张三",       					//用户姓名
        //	"usergroupName":"运维一组，运维2组",          //用户组名
        //    "policyGuid":"1111-2222-abcdef"					       //用户对应的策略标识
        //}
        jsonObject.put("userGuid",user.getGuid());
        jsonObject.put("userName",user.getUsername());
        jsonObject.put("userTrueName",user.getName());
        jsonObject.put("usergroupName",user.getUsergroupsName());
        jsonObject.put("policyGuid",user.getStrategy());
        return ResultMsg.ok(jsonObject);
    }

    private String getRedirectUrl(User user) {
        //获取页面权限
        List<Resource> navigationList = permissionService.findUserPermissionsByResourceType(user, ResourceType.NAVIGATION.getValue());

        //寻找第一个URL不为空的菜单
        if (!ListUtils.isEmpty(navigationList)) {
            for (Resource resource : navigationList) {
                if (resource instanceof Navigation && !StringUtils.isEmpty(((Navigation) resource).getHref())) {
                    return ((Navigation) resource).getHref();
                }
            }
        }
        return null;
    }

    /**
     * 更新当前登录session所关联的唯一设备标识
     * @param devUnique 唯一设备标识
     * @param session 当前登录session
     */
    @ApiOperation(value = "更新当前登录session所关联的唯一设备标识")
    @ApiImplicitParam(name = "devUnique", value = "客户端唯一设备标识", paramType = "path", dataTypeClass = String.class)
    @PostMapping(value = "/onlineSession/{devUnique}")
    public ResultMsg updateOnlineSessionDevunique(@PathVariable(value = "devUnique") String devUnique, HttpSession session) {
        try {
            if (StringUtils.isEmpty(devUnique)) {
                return ResultMsg.False("devunique can not be null!");
            }
            //更新当前登录session所关联的唯一设备标识
            userService.updateOnlineSessionDevunique(devUnique, session);

            return ResultMsg.ok();
        } catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }
}
