package com.goldencis.osa.system.controller;


import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.system.service.IAuditorOperatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 审计员、操作员 关联表 前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-12-04
 */
@Api(tags = "审批员设置操作员权限")
@RestController
@RequestMapping("/auditorOperator")
public class AuditorOperatorController {
    @Autowired
    IAuditorOperatorService auditorOperatorService;

    @ApiOperation(value = "保存、编辑设备组权限",notes = "保存、编辑设备组操作权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGuid", value = "审计员guid", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "operatorIds", value = "多个操作员id ,分割", paramType = "query", dataTypeClass = String.class)
    })
    @PostMapping(value = "/saveOrUpdate")
    public ResultMsg saveOrUpdateAuditorOperators(String userGuid, String operatorIds) {
        try {
            if (StringUtils.isEmpty(userGuid)){
                return ResultMsg.error("审计员为空");
            }
            if (StringUtils.isEmpty(operatorIds)){
                return ResultMsg.error("操作员为空");
            }
            auditorOperatorService.saveOrUpdateAuditorOperators(userGuid, operatorIds);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "通过审计员guid获取操作员列表")
    @ApiImplicitParam(name = "userGuid", value = "审计员guid", paramType = "query",dataTypeClass = String.class)
    @GetMapping(value = "/getOperatorListByAuditorGuid")
    public ResultMsg getOperatorListByAuditorGuid( String userGuid) {
        try {
            List<User> operators = auditorOperatorService.getOperatorListByAuditorGuid(userGuid);
            return ResultMsg.ok(operators);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }
}
