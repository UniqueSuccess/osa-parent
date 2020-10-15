package com.goldencis.osa.approval.handle.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.approval.entity.ApprovalFlow;
import com.goldencis.osa.approval.entity.ApprovalFlowInfoGranted;
import com.goldencis.osa.approval.handle.ApprovalType;
import com.goldencis.osa.approval.handle.IApprovalHandler;
import com.goldencis.osa.approval.mapper.ApprovalDetailMapper;
import com.goldencis.osa.approval.service.IApprovalFlowInfoGrantedService;
import com.goldencis.osa.common.constants.ConstantsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-25 16:49
 **/
@Component
public class DeleteAssetHandler implements IApprovalHandler {

    @Autowired
    IApprovalFlowInfoGrantedService approvalFlowInfoGrantedService;
    @Autowired
    ApprovalDetailMapper approvalDetailMapper;

    /**
     * 审批类型
     *
     * @return
     */
    @Override
    public ApprovalType type() {
        return ApprovalType.DELETE_ASSET;
    }

    @Override
    public void handle(ApprovalFlow approvalFlow, Integer approvalResult) {
        List<ApprovalFlowInfoGranted> approvalFlowInfoGranteds = approvalFlowInfoGrantedService.list(new QueryWrapper<ApprovalFlowInfoGranted>().eq("flow_id", approvalFlow.getId()));
        //授权删除【同意授权，删除设备； 拒绝授权，更改状态】
        approvalFlowInfoGranteds.stream().forEach(approvalFlowInfoGranted -> {
            if (ConstantsDto.APPROVAL_AUTHORIZED == approvalResult){
                approvalDetailMapper.deleteGrantedById(approvalFlowInfoGranted.getGrantedId());

                // 删除设备账号表中的记录、删除设备设备组中间表、删除SSO(单点登录)从表信息、删除从表
                approvalDetailMapper.deleteAssetRelatedByAssetId(approvalFlowInfoGranted.getAssetId());
            }else if (ConstantsDto.APPROVAL_REJECTED == approvalResult){
                approvalDetailMapper.updateGrantedStatusByGrantedId(approvalFlowInfoGranted.getGrantedId(), ConstantsDto.CONST_FALSE, ConstantsDto.APPROVAL_AUTHORIZED);
            }
        });
    }
}
