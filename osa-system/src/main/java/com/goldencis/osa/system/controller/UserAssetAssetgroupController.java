package com.goldencis.osa.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.entity.ResultTree;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.core.entity.Role;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.IRoleService;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.core.utils.SecurityUtils;
import com.goldencis.osa.system.domain.AuditDetailInfo;
import com.goldencis.osa.system.domain.SystemAccountInfo;
import com.goldencis.osa.system.entity.UserAssetAssetgroup;
import com.goldencis.osa.system.service.IUserAssetAssetgroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户、设备/设备组 关联表（操作员管理设备/设备组） 前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-12-07
 */
@Api(tags = "操作员设置设备/设备组权限")
@RestController
@RequestMapping("/userAssetAssetgroup")
public class UserAssetAssetgroupController {
    @Autowired
    IUserAssetAssetgroupService userAssetAssetgroupService;

    @Autowired
    IUserService userService;

    @Autowired
    IRoleService roleService;

    @ApiOperation(value = "根据用户id获取右侧设备权限和审计权限列表")
    @ApiImplicitParam(name = "id", value = "用户id", paramType = "path", dataTypeClass = String.class)
    @GetMapping(value = "/detail/{id}")
    public ResultMsg detail(@PathVariable(value = "id") String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultMsg.error("用户id不能为空");
        }
        try {
            SystemAccountInfo info = userAssetAssetgroupService.getDetailByUserGuid(id);
            return ResultMsg.ok(info);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "根据用户id获取设备权限")
    @ApiImplicitParam(name = "id", value = "用户id", paramType = "path", dataTypeClass = String.class)
    @GetMapping(value = "/detail/asset/{id}")
    public ResultMsg getAssetPermissionByUserId(@PathVariable(value = "id") String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultMsg.error("用户id不能为空");
        }
        try {
            List<SystemAccountInfo.AssetPermission.AssetItem> list = userAssetAssetgroupService.getAssetPermissionByUserId(id);
            Page<SystemAccountInfo.AssetPermission.AssetItem> page = new Page<>();
            page.setTotal(list.size());
            page.setRecords(list);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "根据用户id获取设备组权限")
    @ApiImplicitParam(name = "id", value = "用户id", paramType = "path", dataTypeClass = String.class)
    @GetMapping(value = "detail/assetGroup/{id}")
    public ResultMsg getAssetGroupPermissionByUserId(@PathVariable(value = "id") String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultMsg.error("用户id不能为空");
        }
        try {
            List<SystemAccountInfo.AssetPermission.AssetGroupItem> list = userAssetAssetgroupService.getAssetGroupPermissionByUserId(id);
            Page<SystemAccountInfo.AssetPermission.AssetGroupItem> page = new Page<>();
            page.setTotal(list.size());
            page.setRecords(list);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "审计员:通过审计员id获取有权限的操作员列表")
    @ApiImplicitParam(name = "id", value = "用户id", paramType = "path", dataTypeClass = String.class)
    @GetMapping(value = "detail/audit/{id}")
    public ResultMsg getAuditPermissionByUserId(@PathVariable(value = "id") String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultMsg.error("用户id不能为空");
        }
        try {
            List<SystemAccountInfo.AuditPermission.UserItem> list = userAssetAssetgroupService.getAuditPermissionByUserId(id, true);
            Page<SystemAccountInfo.AuditPermission.UserItem> page = new Page<>();
            page.setTotal(list.size());
            page.setRecords(list);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "为操作员保存设备权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGuid", value = "操作员guid", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "assetIds", value = "多个设备,分割", paramType = "query", dataTypeClass = String.class),
    })
    @PostMapping(value = "/save/asset")
    public ResultMsg saveAssetPermission(String userGuid, String assetIds) {
        if (StringUtils.isEmpty(userGuid)){
            return ResultMsg.error("操作员为空");
        }
        try {
            List<Integer> list = new ArrayList<>();
            if (!StringUtils.isEmpty(assetIds)) {
                list.addAll(Arrays.stream(assetIds.split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            userAssetAssetgroupService.saveAssetPermission(userGuid, list);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "为操作员保存设备组权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGuid", value = "操作员guid", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "assetGroupIds", value = "多个设备组,分割", paramType = "query", dataTypeClass = String.class),
    })
    @PostMapping(value = "/save/assetGroup")
    public ResultMsg saveAssetGroupPermission(String userGuid, String assetGroupIds) {
        if (StringUtils.isEmpty(userGuid)){
            return ResultMsg.error("操作员为空");
        }
        try {
            List<Integer> list = new ArrayList<>();
            if (!StringUtils.isEmpty(assetGroupIds)) {
                list.addAll(Arrays.stream(assetGroupIds.split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            userAssetAssetgroupService.saveAssetGroupPermission(userGuid, list);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "为审计员保存审计权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGuid", value = "操作员guid", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "operatorIds", value = "多个设备组,分割", paramType = "query", dataTypeClass = String.class),
    })
    @PostMapping(value = "/save/audit")
    public ResultMsg saveAuditPermission(String userGuid, String operatorIds) {
        if (StringUtils.isEmpty(userGuid)){
            return ResultMsg.error("操作员为空");
        }
        try {
            List<String> list = new ArrayList<>();
            if (!StringUtils.isEmpty(operatorIds)) {
                list.addAll(Arrays.stream(operatorIds.split(",")).filter(Objects::nonNull).collect(Collectors.toList()));
            }
            userAssetAssetgroupService.saveAuditPermission(userGuid, list);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "操作员： 通过操作员guid获取设备组树")
    @ApiImplicitParam(name = "userGuid", value = "操作员guid", paramType = "query",dataTypeClass = String.class)
    @GetMapping(value = "/getAssetgroupListTreeByUserGuid")
    public Object getAssetgroupListTreeByUserGuid( String userGuid) {
        try {
            List<ResultTree> collect = userAssetAssetgroupService.getAssetgroupListTreeByUserGuid(userGuid);
            return collect;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "操作员：通过操作员guid获取设备树")
    @ApiImplicitParam(name = "userGuid", value = "操作员guid", paramType = "query",dataTypeClass = String.class)
    @GetMapping(value = "/getAssetListTreeByUserGuid")
    public Object getAssetListTreeByUserGuid( String userGuid) {
        try {
            List<ResultTree> collect = userAssetAssetgroupService.getAssetListTreeByUserGuid(userGuid);
            return collect;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "审计员:获取所有的操作员列表,其中,有权限的checked为true")
    @ApiImplicitParam(name = "id", value = "操作员guid", paramType = "path",dataTypeClass = String.class)
    @GetMapping(value = "/auditList/{id}")
    public ResultMsg getAuditPermissionListByUserId(@PathVariable(value = "id") String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultMsg.error("用户id不能为空");
        }
        try {
            List<SystemAccountInfo.AuditPermission.UserItem> list = userAssetAssetgroupService.getAuditPermissionByUserId(id, false);
            List<AuditDetailInfo> collect = list.stream().map(AuditDetailInfo::new).collect(Collectors.toList());
            return ResultMsg.ok(collect);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "操作员：通过操作员guid获取设备组列表")
    @GetMapping(value = "/getAssetgroupListByUserGuid")
    public ResultMsg getAssetgroupListByUserGuid( String userGuid) {
        try {
            List<UserAssetAssetgroup> userAssetAssetgroups = userAssetAssetgroupService.getAssetgroupListByUserGuid(userGuid);
            return ResultMsg.ok(userAssetAssetgroups);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "操作员：获取设备组分页列表",notes = "分页获取已授权的设备组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataTypeClass =  Integer.class ),
            @ApiImplicitParam(name = "length", value = "每页条数", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "searchStr", value = "查询", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userGuid", value = "操作员guid",dataTypeClass = String.class)
    })
    @GetMapping(value = "/getAssetgroupByUserGuidInPage")
    public ResultMsg getAssetgroupByUserGuidInPage(@RequestParam Map<String, String> params) {
        try {
            //分页查询
            IPage<UserAssetAssetgroup> page = userAssetAssetgroupService.getAssetgroupByUserGuidInPage(params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }


    @ApiOperation(value = "操作员：获取设备分页列表",notes = "分页获取已授权的设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataTypeClass =  Integer.class ),
            @ApiImplicitParam(name = "length", value = "每页条数", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "searchStr", value = "查询", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userGuid", value = "操作员guid",dataTypeClass = String.class)
    })
    @GetMapping(value = "/getAssetByUserGuidInPage")
    public ResultMsg getAssetByUserGuidInPage(@RequestParam Map<String, String> params) {
        try {
            //分页查询
            IPage<UserAssetAssetgroup> page = userAssetAssetgroupService.getAssetByUserGuidInPage(params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "系统管理账号列表",notes = "系统--账号配置：获取系统管理账号列表")
    @GetMapping(value = "/getSystemUsers")
    public ResultMsg getSystemUsers(String searchStr) {
        try {
            //分页查询
            List<User> userList = userAssetAssetgroupService.getSystemUsers(searchStr);
            userList.removeIf(user -> ConstantsDto.USER_SYSTEM_ID.equals(user.getGuid()));
            //设置用户roletype
            userList.forEach(user -> {
                //查询关联角色集合
                List<Role> roleList = roleService.getRoleListByUserguid(user.getGuid());
                if (! ListUtils.isEmpty(roleList)){
                    user.setRoleType(roleList.get(0).getGuid());
                }
            });
            return ResultMsg.ok(userList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * 新建系统用户时,选择角色;
     * 超级管理员(system)用户,可以创建管理员角色的用户
     * @return
     */
    @ApiOperation(value = "角色类型",notes = "系统--账号配置：新建系统用户时选择角色")
    @GetMapping(value = "/getSystemUserRoleTypes")
    public ResultMsg getSystemUserRoleTypes() {
        try {
            User user = SecurityUtils.getCurrentUser();
            if (Objects.isNull(user)) {
                return ResultMsg.False("获取当前登录用户失败");
            }
            // 是否排除管理员角色
            boolean excludeAdmin = true;
            if (ConstantsDto.USER_SYSTEM_ID.equals(user.getGuid())) {
                excludeAdmin = false;
            }
            List<Role> roles = roleService.getRoleListExcludeNormal(excludeAdmin);
            return ResultMsg.ok(roles);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "通过系统管理员guid获取用户信息")
    @ApiImplicitParam(name = "guid", value = "用户guid", required = true, paramType = "path", dataType = "String")
    @GetMapping(value = "/SystemUser/{guid}")
    public ResultMsg findSystemUserByGuid(@PathVariable("guid") String guid) {
        try {
            //查询用户信息
            User user = userService.getById(guid);
            //查询关联角色集合
            List<Role> roleList = roleService.getRoleListByUserguid(guid);
            if (! ListUtils.isEmpty(roleList)){
                user.setRoleType(roleList.get(0).getGuid());
            }
            return ResultMsg.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "删除设备权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGuid", value = "操作员guid", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "assetIds", value = "多个设备id,分割", paramType = "query", dataTypeClass = String.class),
    })
    @DeleteMapping(value = "delete/asset")
    public ResultMsg deleteAssetPermission(String userGuid, String assetIds) {
        if (StringUtils.isEmpty(userGuid)) {
            return ResultMsg.error("用户id不能为空");
        }
        if (StringUtils.isEmpty(assetIds)) {
            return ResultMsg.error("设备id不能为空");
        }
        try {
            List<Integer> list = Arrays.stream(assetIds.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            userAssetAssetgroupService.deleteAssetPermission(userGuid, list);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "删除设备组权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGuid", value = "操作员guid", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "assetGroupIds", value = "多个设备id,分割", paramType = "query", dataTypeClass = String.class),
    })
    @DeleteMapping(value = "delete/assetGroup")
    public ResultMsg deleteAssetGroupPermission(String userGuid, String assetGroupIds) {
        if (StringUtils.isEmpty(userGuid)) {
            return ResultMsg.error("用户id不能为空");
        }
        if (StringUtils.isEmpty(assetGroupIds)) {
            return ResultMsg.error("设备组id不能为空");
        }
        try {
            List<Integer> list = Arrays.stream(assetGroupIds.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            userAssetAssetgroupService.deleteAssetGroupPermission(userGuid, list);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "删除审计权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGuid", value = "操作员guid", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "operatorIds", value = "多个用户id,分割", paramType = "query", dataTypeClass = String.class),
    })
    @DeleteMapping(value = "delete/audit")
    public ResultMsg deleteAuditPermission(String userGuid, String operatorIds) {
        if (StringUtils.isEmpty(userGuid)) {
            return ResultMsg.error("用户id不能为空");
        }
        if (StringUtils.isEmpty(operatorIds)) {
            return ResultMsg.error("用户id不能为空");
        }
        try {
            List<String> list = Arrays.stream(operatorIds.split(",")).collect(Collectors.toList());
            userAssetAssetgroupService.deleteAuditPermission(userGuid, list);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }
}
