package com.goldencis.osa.task.controller;


import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.task.entity.TaskAsset;
import com.goldencis.osa.task.service.ITaskAssetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 定时改密计划 前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2019-01-19
 */
@Api(tags = "任务管理")
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private ITaskAssetService taskAssetService;

    @ApiOperation(value = "保存自动改密计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle", value = "计划循环方式,none:不循环,只执行一次;week:按周循环;month:按月循环", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "day", value = "执行日期,按周循环:传1-7;按月循环:传1-28", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "time", value = "执行时间,格式为:(时:分);如:01:35", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "密码类型,固定:SAME,随机:RANDOM,同一:FIXED", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码类型为固定(SAME)时,用户输入的密码", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "rule", value = "随机密码的生成规则(int值1:数字,2:大写字母,4:小写字母,8:特殊符号;做加法,使用位运算匹配)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "随机密码的长度,正整数", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "ftp", value = "是否将改密信息发送到FTP服务器", paramType = "query", dataType = "Boolean"),
            @ApiImplicitParam(name = "ftpAddr", value = "FTP服务器地址", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "ftpDir", value = "FTP服务器存储路径", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "ftpAccount", value = "FTP服务器账号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "ftpPwd", value = "FTP服务器密码", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "email", value = "是否将改密信息发送到指定邮箱", paramType = "query", dataType = "Boolean"),
            @ApiImplicitParam(name = "emailAddress", value = "邮箱集合", paramType = "query", dataType = "List"),
            @ApiImplicitParam(name = "resourceId", value = "设备id集合", paramType = "query", dataType = "List"),
            @ApiImplicitParam(name = "number", value = "数字", paramType = "query", dataType = "Boolean"),
            @ApiImplicitParam(name = "lowercase", value = "小写字母", paramType = "query", dataType = "Boolean"),
            @ApiImplicitParam(name = "capital", value = "大写字母", paramType = "query", dataType = "Boolean"),
            @ApiImplicitParam(name = "special", value = "特殊符号", paramType = "query", dataType = "Boolean"),
    })
    @PostMapping(value = "/asset/save")
    public ResultMsg save(@RequestBody TaskAsset task) {
        if (Objects.isNull(task)) {
            return ResultMsg.error("参数不正确");
        }
        try {
            taskAssetService.refreshTaskAsset(task);
            return ResultMsg.ok();
        } catch (Exception e) {
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取自动改密计划详情")
    @GetMapping(value = "/asset/detail")
    public ResultMsg detail() {
        try {
            TaskAsset taskAsset = taskAssetService.detail();
            return ResultMsg.ok(taskAsset);
        } catch (Exception e) {
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取设备树")
    @GetMapping(value = "/asset/tree")
    public Object getAssetListTreeByUserGuid() {
        try {
            return taskAssetService.getAssetListTree();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }


}