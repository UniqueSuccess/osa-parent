package com.goldencis.osa.core.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.common.config.Config;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.core.aop.OsaSystemLog;
import com.goldencis.osa.core.entity.Role;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.entity.Usergroup;
import com.goldencis.osa.core.service.IRoleService;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.core.service.IUsergroupService;
import com.goldencis.osa.core.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信息表-定义用户基本信息 前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
@Api(tags = "用户信息管理")
@RestController
@RequestMapping("/user")
public class UserController {

    Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private Config config;
    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUsergroupService usergroupService;

    @ApiOperation("获取用户分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer"),
            @ApiImplicitParam(name = "usergroupPid", value = "父级用户组", dataType = "Integer"),
            @ApiImplicitParam(name = "searchStr", value = "用户名/姓名查询", dataType = "String"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String"),
            @ApiImplicitParam(name = "roleType", value = "角色类型(normal用户，admin管理员)", dataType = "String"),
            @ApiImplicitParam(name = "authenticationMethod", value = "认证方式的筛选条件", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态的筛选条件", dataType = "String")
    })
//    @OsaSystemLog(module = "查看用户列表", template = "查询条件为：%s，排序字段为：%s", args = "0.searchStr,0.orderColumn", type = LogType.SYSTEM_SELECT)
    @GetMapping(value = "/getUsersInPage")
    public ResultMsg getUsersInPage(@RequestParam Map<String, String> params) {
        try {
            //分页查询
            IPage<User> page = userService.getUsersInPage(params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "通过guid获取用户信息")
    @ApiImplicitParam(name = "guid", value = "用户guid", required = true, paramType = "path", dataType = "String")
    @Cacheable(value = "users", key = "#guid")
    @GetMapping(value = "/user/{guid}")
    public ResultMsg findUserByGuid(@PathVariable("guid") String guid) {
        try {
            //查询用户信息
            User user = userService.findUserByGuid(guid);

            //查询关联角色集合
            List<Role> roleList = roleService.getRoleListByUserguid(guid);
            user.setRoles(roleList);

            //查询关联用户组集合
            List<Usergroup> usergroupsList = usergroupService.getUsergroupListByUserGuid(guid);
            user.setUsergroups(usergroupsList);

            return ResultMsg.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "通过用户名获取用户登录方式信息")
    @ApiImplicitParam(name = "guid", value = "用户guid", required = true, paramType = "path", dataType = "String")
    @GetMapping(value = "/checkUserLogonMethodByUsername/{username}")
    public ResultMsg checkUserLogonMethodByUsername(@PathVariable("username") String username) {
        try {
            //通过用户名获取用户登录方式信息
            Map<String, Object> data = userService.checkUserLogonMethodByUsername(username);
            if (data == null) {
                return ResultMsg.False("false");
            }

            return ResultMsg.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @GetMapping(value = "/getCurrentUser")
    public ResultMsg getCurrentUser() {
        try {
            //查询用户信息
            User user = SecurityUtils.getCurrentUser();

            //查询关联角色集合
            List<Role> roleList = roleService.getRoleListByUserguid(user.getGuid());
            user.setRoles(roleList);

            //查询关联用户组集合
            List<Usergroup> usergroupsList = usergroupService.getUsergroupListByUserGuid(user.getGuid());
            user.setUsergroups(usergroupsList);

            return ResultMsg.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "新增用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "name", value = "真实姓名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "authenticationMethod", value = "认证方式", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "strategy", value = "用户策略Id", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "账号状态", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "phone", value = "电话号码", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "email", value = "邮箱", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "defaultPassword", value = "默认密码是否开启(on开、off关)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "roleType", value = "用户角色（normal用户，admin管理员）", paramType = "query", dataType = "String")
    })
    @OsaSystemLog(module = "新建用户", template = "用户名：%s，用户名称：%s", args = "0.username,0.name", type = LogType.SYSTEM_ADD)
    @PostMapping(value = "/user")
    public ResultMsg save(User user) {
        try {
            //用户名不得能为空
            if (StringUtils.isEmpty(user.getUsername())) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "用户名不能为空！");
            }

            //检查用户名是否重复
            boolean flag = userService.checkUserNameDuplicate(user);

            if (!flag) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "用户名重复！");
            }

            userService.saveUser(user);

            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "编辑用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "guid", value = "用户guid", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "name", value = "真实姓名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "email", value = "email", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "phone", value = "电话号码", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "账号状态", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "strategy", value = "用户策略Id", paramType = "query", dataType = "String"),
    })
    @PutMapping(value = "/user")
    @OsaSystemLog(module = "编辑用户", template = "用户名：%s，用户名称：%s", args = "0.username,0.name", type = LogType.SYSTEM_UPDATE)
    public ResultMsg edit(User user) {
        try {
            //用户名不得能为空
            if (StringUtils.isEmpty(user.getUsername())) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "用户名不能为空！");
            }

            //检查用户名是否重复
            boolean flag = userService.checkUserNameDuplicate(user);

            if (!flag) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "用户名重复！");
            }

            userService.updateUserByGuid(user);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "删除用户")
    @ApiImplicitParam(name = "guid", value = "用户guid", required = true, paramType = "path", dataType = "String")
    @OsaSystemLog(module = "删除用户", template = "用户名：%ret，用户名称：%ret", ret = "data.username,data.name", type = LogType.SYSTEM_DELETE)
    @DeleteMapping(value = "/user/{guid}")
    public ResultMsg delete(@PathVariable(value = "guid") String guid) {
        try {
            if(guid == null || "".equals(guid)){
                return ResultMsg.error("用户id为空");
            }

            User user = userService.deleteUserByGuid(guid);
            return ResultMsg.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }


    @ApiOperation(value = "通过用户guids批量删除用户")
    @ApiImplicitParam(name = "userIds",value = "批量用户id，多个用户id用英文 ,分割",required = true,paramType = "query",dataType = "String")
    @OsaSystemLog(module = "批量删除用户", template = "批量删除的用户名为：%ret", ret = "data", type = LogType.SYSTEM_DELETE)
    @DeleteMapping(value = "/user/deleteUsersByIds")
    public ResultMsg deleteUsersByIds(String userIds){
        try {
            if(userIds == null || "".equals(userIds)){
                return ResultMsg.error("用户id为空");
            }
            String userNames = userService.deleteUsersByIds(Arrays.stream(userIds.split(",")).collect(Collectors.toList()));
            return ResultMsg.ok(userNames);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ModelAttribute
    public void getModel(@RequestParam(value = "guid", required = false) String guid, Map<String, Object> map) {
        if (guid != null) {
            User user = userService.getById(guid);
            map.put("user", user);
        }
    }

    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "guid", value = "用户guid", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "oldPwd", value = "原密码", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "newPwd", value = "新密码", paramType = "query", dataType = "String"),
    })
    @PutMapping(value = "/updateUserPwd")
    @OsaSystemLog(module = "修改密码", template = "用户信息：%ret", args = "data", type = LogType.SYSTEM_UPDATE)
    public ResultMsg updateUserPwd(String guid, String oldPwd, String newPwd) {
        try {
            String content =  userService.updateUserPwd( guid, oldPwd,newPwd);
            return ResultMsg.ok(content);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "查询用户的在线状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "argv", value = "userGuid 运维用户标识；devUnique：运维用户所在设备的标识；传参示例：{\"userGuid\":\"f7fb3f2f-68ab-4bad-9d3d-3f3cba54a328\",\"devUnique\":\"aaaa-bbbb-3333-4444\"}", paramType = "query", dataType = "String")
    })
    @PostMapping(value = "/getUserStat")
    public JSONObject getUserStat(String argv) {
        JSONObject result = new JSONObject();
        try {
            if (StringUtils.isEmpty(argv)){
                result.put("msg", "argv can not be null!");
                return result;
            }

            JSONObject jsonObject = JSONObject.parseObject(argv);
            String guid = jsonObject.getString("userGuid");
            String devUnique = jsonObject.getString("devUnique");

            if (StringUtils.isEmpty(guid) || StringUtils.isEmpty(devUnique)){
                result.put("msg", "userGuid or devUnique can not be null!");
                return result;
            }

            //查询对应用户guid和设备唯一标示的用户在线状态
            if (userService.queryUserStatus(guid, devUnique)) {
                //根据用户guid，查询对应的策略并返回
                result = userService.findUserPolicyByUserguid(guid);
                result.put("userStat", 0);
            } else {
                result.put("userStat", 1);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "error");
            return result;
        }
    }
}