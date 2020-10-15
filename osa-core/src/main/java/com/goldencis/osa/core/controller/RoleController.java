package com.goldencis.osa.core.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.core.aop.OsaSystemLog;
import com.goldencis.osa.core.entity.Role;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.entity.UserRole;
import com.goldencis.osa.core.service.IRoleService;
import com.goldencis.osa.core.service.IUserRoleService;
import com.goldencis.osa.core.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/role")
public class RoleController {

    private final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private IRoleService roleService;
    @Autowired
    private IUserRoleService userRoleService;

    @ApiOperation(value = "根据角色guid获取用户信息")
    @ApiImplicitParam(name = "guid", value = "角色guid", required = true, type = "path", dataType = "String")
    @GetMapping(value = "/role/{guid}")
    public ResultMsg findRoleByGuid(@PathVariable(value = "guid") String guid) {
        try {

            Role role = roleService.findRoleByGuid(guid);

            return ResultMsg.ok(role);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(500, e.getMessage());
        }
    }

    @ApiOperation(value = "获取角色列表")
//    @ApiImplicitParam(name = "searchStr", value = "角色名称查询条件", type = "query", dataType = "String")
    @GetMapping(value = "/getRoleList")
    public ResultMsg getRoleList() {
        try {
            User user = SecurityUtils.getCurrentUser();
            if (user == null) {
                return ResultMsg.False("用户未登录");
            }
            //根据查询参数获取角色列表
            List<Role> roleList = roleService.getRoleListExcludeNormal(false);
            return ResultMsg.ok(roleList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(500, e.getMessage());
        }
    }

    @ApiOperation(value = "新增角色")
    @ApiImplicitParam(name = "name", value = "角色名称", type = "query", dataType = "String")
    @OsaSystemLog(module = "新增角色", template = "角色名称为：%s", args = "0.name", type = LogType.SYSTEM_ADD)
    @PostMapping(value = "/role")
    public ResultMsg save(Role role) {
        try {
            if (rejectOperate()) {
                return ResultMsg.False("抱歉,您没有操作角色的权限");
            }
            //保存角色
            roleService.saveRole(role);

            return ResultMsg.ok(role.getGuid());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(500, e.getMessage());
        }
    }

    @ApiOperation(value = "编辑角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "guid", value = "角色唯一标示", type = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "角色名称", type = "query", dataType = "String")
    })
    @OsaSystemLog(module = "编辑角色", template = "编辑角色，新角色名称为：%s", args = "0.name", type = LogType.SYSTEM_UPDATE)
    @PutMapping(value = "/role")
    public ResultMsg edit(Role role) {
        try {
            if (rejectOperate()) {
                return ResultMsg.False("抱歉,您没有操作角色的权限");
            }
            //角色名不得能为空
            if (StringUtils.isEmpty(role.getName()) || StringUtils.isEmpty(role.getGuid())) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "角色名称不能为空！");
            }
            //预置角色不能修改
            if (ConstantsDto.ROLE_SYSTEM_PID.equals(role.getGuid())|| ConstantsDto.ROLE_OPERATOR_PID.equals(role.getGuid()) || ConstantsDto.ROLE_AUDITOR_PID.equals(role.getGuid()) || ConstantsDto.ROLE_USER_PID.equals(role.getGuid())) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "预置角色不能修改！");
            }

            //检查角色名是否重复
            boolean flag = roleService.checkRoleNameDuplicate(role);

            if (!flag) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "角色名重复！");
            }
            //编辑角色
            roleService.updateRole(role);

            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(500, e.getMessage());
        }
    }

    @ApiOperation(value = "保存角色权限")
    @OsaSystemLog(module = "保存角色权限", template = "保存角色权限。", args = "data", type = LogType.SYSTEM_ADD)
    @PostMapping(value = "/rolePermissions")
    public ResultMsg updateRolePermissions(@RequestBody JSONObject rolePermission) {
        try {
            if (rejectOperate()) {
                return ResultMsg.False("抱歉,您没有操作角色的权限");
            }
            if (!rolePermission.containsKey("guid") || !rolePermission.containsKey("list")) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "角色唯一标示或权限参数不能为空！");
            }
            String guid = rolePermission.getString("guid");
            //预置角色不能修改
            if (ConstantsDto.ROLE_SYSTEM_PID.equals(guid)|| ConstantsDto.ROLE_OPERATOR_PID.equals(guid) || ConstantsDto.ROLE_AUDITOR_PID.equals(guid) || ConstantsDto.ROLE_USER_PID.equals(guid)) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "预置角色不能修改！");
            }
            JSONArray list = rolePermission.getJSONArray("list");

            Role role = roleService.getById(guid);
            if (role == null) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "角色不存在！");
            }

            //保存角色权限
            roleService.updateRolePermissions(guid, list);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(500, e.getMessage());
        }
    }

    @ApiOperation(value = "删除角色")
    @ApiImplicitParam(name = "guid", value = "角色唯一标示", type = "query", dataType = "String")
    @OsaSystemLog(module = "删除角色", template = "删除的角色名称为：%ret", args = "data", type = LogType.SYSTEM_DELETE)
    @DeleteMapping(value = "/role/{guid}")
    public ResultMsg deleteRole(@PathVariable(value = "guid") String guid) {
        try {
            if (rejectOperate()) {
                return ResultMsg.False("抱歉,您没有操作角色的权限");
            }
            if (StringUtils.isEmpty(guid)) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "角色唯一标示参数不能为空！");
            }
            //预置角色不能修改
            if (ConstantsDto.ROLE_SYSTEM_PID.equals(guid)|| ConstantsDto.ROLE_OPERATOR_PID.equals(guid) || ConstantsDto.ROLE_AUDITOR_PID.equals(guid) || ConstantsDto.ROLE_USER_PID.equals(guid)) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "预置角色不能修改！");
            }
            Role role = roleService.getById(guid);
            if (role == null) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "角色不存在！");
            }
            List<UserRole> list = userRoleService.list(new QueryWrapper<UserRole>().eq("role_guid", role.getGuid()));
            if (!CollectionUtils.isEmpty(list)) {
                return ResultMsg.False("不能删除当前角色");
            }
            //删除角色
            String roleName = roleService.deleteRole(role);
            return ResultMsg.ok(roleName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(500, e.getMessage());
        }
    }


    @ModelAttribute
    public void getModel(@RequestParam(value = "guid", required = false) String guid, Map<String, Object> map) {
        if (guid != null) {
            Role role = roleService.getById(guid);
            map.put("role", role);
        }
    }

    /**
     * 拒绝操作
     * 当编辑删除角色的时候,需要调用该函数,判断当前登录用户是否拥有操作角色的权限
     * 系统自带的超级管理员(system)可以操作角色;
     * 角色类型为系统自带管理员的,可以操作角色;
     * @return 如果没有,需要拒绝该次请求,返回true;否则,返回false
     */
    private boolean rejectOperate() {
        User user = SecurityUtils.getCurrentUser();
        if (Objects.isNull(user)) {
            logger.warn("获取当前登录用户失败");
            return true;
        }
        String id = user.getGuid();
        // 超级管理员system拥有编辑删除角色的权限
        if (ConstantsDto.USER_SYSTEM_ID.equals(id)) {
            return false;
        }
        // 系统自带的管理员角色,拥有编辑删除角色的权限
        List<UserRole> list = userRoleService.list(new QueryWrapper<UserRole>().eq("user_guid", id));
        for (UserRole item : list) {
            if (ConstantsDto.ROLE_SYSTEM_PID.equals(item.getRoleGuid())) {
                return false;
            }
        }
        return true;
    }
}
