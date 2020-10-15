package com.goldencis.osa.core.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.common.entity.ResultLog;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.core.aop.OsaSystemLog;
import com.goldencis.osa.core.entity.Usergroup;
import com.goldencis.osa.core.params.UsergroupParams;
import com.goldencis.osa.core.service.IUsergroupService;
import com.goldencis.osa.core.utils.QueryUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户组表 前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-10-23
 */
@Api(tags = "用户组管理")
@RestController
@RequestMapping("/usergroup")
public class UsergroupController {

    @Autowired
    private IUsergroupService usergroupService;

    @ApiOperation("分页获取用户组列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer"),
            @ApiImplicitParam(name = "searchStr", value = "查询条件", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String")
    })
    @GetMapping(value = "/getUsergroupInPage")
    public ResultMsg getUsergroupInPage(UsergroupParams params) {
        try {
            IPage<Usergroup> page = QueryUtils.paresParams2Page(params);
            usergroupService.getUsergroupInPage(page, params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation("获取用户组列表")
    @ApiImplicitParam(name = "guid", value = "用户guid", dataType = "String", paramType = "query")
    @GetMapping("/list")
    public Object getUsergroupList(String guid) {
        try {
            List<Usergroup> list = usergroupService.getUsergroupListByGuid(guid);
//            ResultMsg ok = ResultMsg.ok();
//            ok.setRows(list);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation("新建用户组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户组名称", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pid", value = "所属用户组id", required = true, paramType = "query", dataType = "Integer")
    })
    @PostMapping("/usergroup")
    @OsaSystemLog(module = "新建用户组",  template = "用户组名称：%s", args = "0.name",type = LogType.SYSTEM_ADD)
    public ResultMsg save(Usergroup usergroup) {
        try {
            usergroupService.saveUsergroup(usergroup);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation("删除用户组")
    @ApiImplicitParam(name = "id", value = "用户组id", required = true, paramType = "path", dataType = "Integer")
    @DeleteMapping("/usergroup/{id}")
    @OsaSystemLog(module = "删除用户组", template = "用户组名称：%ret", ret = "data", type = LogType.SYSTEM_DELETE)
    public ResultMsg delete(@PathVariable Integer id) {
        if (id == null) {
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, "用户组id不能为空");
        }
        try {
           String usergroupName = usergroupService.deleteUsergroupById(id);
            return ResultMsg.ok(usergroupName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation("批量删除用户组")
    @ApiImplicitParam(name = "ids", value = "用户组id,多个用,隔开", required = true, paramType = "query", dataType = "String")
    @OsaSystemLog(module = "批量删除用户组", template = "批量删除的用户组名为：%ret", ret = "data", type = LogType.SYSTEM_DELETE)
    @DeleteMapping("/usergroup/delete")
    public ResultMsg deleteBatch(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return ResultMsg.error("用户组id不能为空");
        }
        try {
            // 获取用户组id集合
            List<Integer> list = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            // 批量删除设备组
            ResultLog resultLog = usergroupService.deleteBatch(list);
            if (resultLog.getSuccessCount() == 0) {
                return ResultMsg.False("删除失败");
            }
            String msg;
            if (resultLog.getSuccessCount() == list.size()) {
                msg = "删除成功";
            } else {
                msg = "部分删除成功";
            }
            return ResultMsg.build(ResultMsg.RESPONSE_TRUE, msg,resultLog.getResultData());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation("更新用户组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户组id", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "name", value = "用户组名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pid", value = "所属用户组id", paramType = "query", dataType = "Integer")
    })
    @PutMapping("/usergroup/{id}")
    @OsaSystemLog(module = "编辑用户组",  template = "用户组名称：%s", args = "1.name",type = LogType.SYSTEM_UPDATE)
    public ResultMsg update(@PathVariable Integer id, Usergroup usergroup) {
        if (id == null) {
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, "用户组id不能为空");
        }
        try {
            usergroupService.updateUsergroupById(id, usergroup);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }


    @ApiOperation("获取用户组列表（用户组）")
    @ApiImplicitParam(name = "guid", value = "用户组guid", dataType = "String", paramType = "query")
    @GetMapping("/getUsergroupListByUserGroupGuid")
    public Object getUsergroupListByUserGroupGuid(Integer guid) {
        try {
            List<Usergroup> list = usergroupService.getUsergroupListByUserGroupGuid(guid);
//            ResultMsg ok = ResultMsg.ok();
//            ok.setRows(list);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }
}
