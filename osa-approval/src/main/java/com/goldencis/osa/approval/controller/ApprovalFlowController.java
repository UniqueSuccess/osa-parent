package com.goldencis.osa.approval.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.approval.entity.ApprovalDetail;
import com.goldencis.osa.approval.entity.ApprovalFlow;
import com.goldencis.osa.approval.entity.ApprovalFlowInfoCommand;
import com.goldencis.osa.approval.entity.ApprovalFlowInfoGranted;
import com.goldencis.osa.approval.service.IApprovalDetailService;
import com.goldencis.osa.approval.service.IApprovalFlowInfoCommandService;
import com.goldencis.osa.approval.service.IApprovalFlowService;
import com.goldencis.osa.approval.utils.ApprovalConstants;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.core.aop.OsaSystemLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 具体审批流程主表--定义审批公共信息 前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
@Api(tags = "审批管理")
@RestController
@RequestMapping("/approval")
public class ApprovalFlowController {
    @Autowired
    IApprovalFlowService approvalFlowService;

    @Autowired
    IApprovalDetailService approvalDetailService;

    @Autowired
    IApprovalFlowInfoCommandService approvalFlowInfoCommandService;

    @ApiOperation(value = "分页获取授权审批列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer"),
            @ApiImplicitParam(name = "searchStr", value = "名称查询", dataType = "String"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String"),
            @ApiImplicitParam(name = "approvalType", value = "审批类型（0待审批，1已审批）", dataType = "Integer"),
            @ApiImplicitParam(name = "definitionType", value = "类型的筛选条件（1添加； 2删除）", dataType = "String"),
            @ApiImplicitParam(name = "grantedMethod", value = "授权方式的筛选条件", dataType = "String")
    })
    @GetMapping(value = "/getApprovalFlowGrantedsInPage")
    public ResultMsg getApprovalFlowGrantedsInPage(@RequestParam Map<String, String> params) {
        try{
            //获取分页数据
            IPage<ApprovalFlow> page =  approvalFlowService.getApprovalFlowGrantedsInPage( params);
            return ResultMsg.page(page);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "分页获取命令审批列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer"),
            @ApiImplicitParam(name = "searchStr", value = "名称查询", dataType = "String"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String"),
            @ApiImplicitParam(name = "approvalType", value = "审批类型（0待审批，1已审批）", dataType = "Integer")
    })
    @GetMapping(value = "/getApprovalFlowCommandsInPage")
    public ResultMsg getApprovalFlowCommandsInPage(@RequestParam Map<String, String> params) {
        try{
            //获取分页数据
            IPage<ApprovalFlow> page =  approvalFlowService.getApprovalFlowCommandsInPage( params);
            return ResultMsg.page(page);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "分页获取授权审批列表详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer"),
            @ApiImplicitParam(name = "searchStr", value = "名称查询", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String"),
            @ApiImplicitParam(name = "flowId", value = "审批id", dataType = "String")
    })
    @GetMapping(value = "/getApprovalFlowGrantedsDetailInPage")
    public ResultMsg getApprovalFlowGrantedsDetailInPage(@RequestParam Map<String, String> params) {
        try{
            //获取分页数据
            IPage<ApprovalFlowInfoGranted> page =  approvalFlowService.getApprovalFlowGrantedsDetailInPage( params);
            String flowId = params.get("flowId");
            if (!StringUtils.isEmpty(flowId)){
                //后期审批结果 可能存在分步情况，暂不修改
//                ApprovalFlow approvalFlow = approvalFlowService.getApprovalFlowDetailByFlowId(flowId);
                ApprovalFlow approvalFlow = approvalFlowService.getOne(new QueryWrapper<ApprovalFlow>().eq("id",flowId));
                if (!Objects.isNull(approvalFlow)){
                    ApprovalDetail approvalDetail = approvalDetailService.getOne(new QueryWrapper<ApprovalDetail>().eq("flow_id",flowId));
                    if(!Objects.isNull(approvalDetail)){
                        approvalFlow.setApprovalRemark(approvalDetail.getRemark());
                    }
                    return ResultMsg.page(page,approvalFlow);
                }
            }
            return ResultMsg.page(page);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "分页获取授权命令列表详情")
    @ApiImplicitParam(name = "flowId", value = "审批id", paramType = "query", dataType = "String")
    @GetMapping(value = "/getApprovalFlowCommandsDetail")
    public ResultMsg getApprovalFlowCommandsDetail(String flowId) {
        try{
            if (StringUtils.isEmpty(flowId)){
                return ResultMsg.error("命令审批id为空");
            }
            ApprovalFlow approvalFlow = approvalFlowService.getOne(new QueryWrapper<ApprovalFlow>().eq("id",flowId));
            if (Objects.isNull(approvalFlow)){
                return ResultMsg.error("命令审批id不存在");
            }
            ApprovalFlowInfoCommand flowInfoCommand = approvalFlowInfoCommandService.getApprovalFlowInfoCommandByFlowId(flowId);
            if (!Objects.isNull(flowInfoCommand)){
                ApprovalDetail approvalDetail = approvalDetailService.getOne(new QueryWrapper<ApprovalDetail>().eq("flow_id",flowId));
                if(!Objects.isNull(approvalDetail)){
                    flowInfoCommand.setApprovalRemark(approvalDetail.getRemark());
                }
            }
            return ResultMsg.ok(flowInfoCommand);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "审批结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowId", value = "审批id", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "approvalResult", value = "审批结果（-1 拒绝， 1通过）", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "approvalRemark", value = "审批备注", paramType = "query", dataType = "String"),
    })
    @GetMapping(value = "/approvalResut")
    @OsaSystemLog(module = "审批", template = "审批详细为：%ret", ret = "data", type = LogType.APPROVAL_AUTHORIZED)
    public ResultMsg approvalResult(String flowId, Integer approvalResult, String approvalRemark) {
        try{
            if (StringUtils.isEmpty(flowId)){
                return ResultMsg.False("审批id为空");
            }
            if (Objects.isNull(approvalResult)){
                return ResultMsg.False("审批结果为空");
            }
            if (! com.goldencis.osa.common.utils.StringUtils.isInLength(approvalRemark,200)){
                return ResultMsg.False("审批备注最大长度为200");
            }

            String flowContent =  approvalDetailService.approvalResult(flowId, approvalResult, approvalRemark, ApprovalConstants.APPROVAL_APPROVALTYPE_OPERATION);
            return ResultMsg.ok(flowContent);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }
}
