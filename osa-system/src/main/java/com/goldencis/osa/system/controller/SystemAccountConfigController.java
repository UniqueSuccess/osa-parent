package com.goldencis.osa.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.core.aop.OsaSystemLog;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.core.utils.SecurityUtils;
import com.goldencis.osa.system.entity.AuditorOperator;
import com.goldencis.osa.system.entity.UserAssetAssetgroup;
import com.goldencis.osa.system.service.IAuditorOperatorService;
import com.goldencis.osa.system.service.IUserAssetAssetgroupService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-18 16:17
 **/
@RestController
@RequestMapping(value = "/system/accountConfig")
public class SystemAccountConfigController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IUserAssetAssetgroupService userAssetAssetgroupService;
    @Autowired
    private IAuditorOperatorService auditorOperatorService;

    @ApiOperation(value = "新增系统用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "name", value = "真实姓名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "roleType", value = "角色类型", paramType = "query", dataType = "String")
    })
    @OsaSystemLog(module = "新建系统账号", template = "用户名：%s，用户名称：%s", args = "0.username,0.name", type = LogType.SYSTEM_ADD)
    @PostMapping(value = "/saveSystemUser")
    public ResultMsg saveSystemUser(User user) {
        try {
            //用户名不得能为空
            if (StringUtils.isEmpty(user.getUsername())) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "账号不能为空！");
            }

            //检查用户名是否重复
            boolean flag = userService.checkUserNameDuplicate(user);
            if (!flag) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "账号重复！");
            }
            User after = userService.saveSystemUser(user);
            return ResultMsg.ok(after.getGuid());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "修改系统用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "name", value = "真实姓名", paramType = "query", dataType = "String")
    })
    @OsaSystemLog(module = "修改系统账号", template = "用户名：%ret，用户名称：%ret", ret = "data.username,data.name", type = LogType.SYSTEM_UPDATE)
    @PostMapping(value = "/editSystemUser")
    public ResultMsg editSystemUser(String guid, User user) {
        if (StringUtils.isEmpty(guid)) {
            return ResultMsg.error("用户guid不能为空");
        }
        user.setGuid(guid);
        if (StringUtils.isEmpty(user.getName())) {
            return ResultMsg.error("用户姓名不能为空");
        }
        try {
            //用户名不得能为空
//            if (StringUtils.isEmpty(user.getUsername())) {
//                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "用户名不能为空！");
//            }
            //检查用户名是否重复
//            boolean flag = userService.checkUserNameDuplicate(user);
//            if (!flag) {
//                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "用户名重复！");
//            }
            return ResultMsg.ok(userService.updateSystemUser(user));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @OsaSystemLog(module = "删除系统账号", template = "用户名：%ret，用户名称：%ret", ret = "data.username,data.name", type = LogType.SYSTEM_DELETE)
    @ApiOperation(value = "删除系统用户")
    @DeleteMapping(value = "/{id}")
    public ResultMsg deleteSystemUser(@PathVariable(value = "id") String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultMsg.error("用户id不能为空");
        }
        User self = SecurityUtils.getCurrentUser();
        if (Objects.isNull(self)) {
            return ResultMsg.error("获取当前登录用户失败");
        }
        if (id.equals(self.getGuid())) {
            return ResultMsg.error("不能删除自己");
        }
        try {
            User user = userService.deleteUserByGuid(id);
            // 操作员,移除设备权限
            userAssetAssetgroupService.remove(new QueryWrapper<UserAssetAssetgroup>().eq("user_guid", id));
            // 删除所有与该账号相关的审计操作关联;
            auditorOperatorService.remove(new QueryWrapper<AuditorOperator>().eq("auditor_guid", id).or().eq("operator_guid", id));
            return ResultMsg.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

}
