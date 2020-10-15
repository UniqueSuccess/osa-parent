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
 * @create: 2018-12-25 16:51
 **/
@Component
public class DeleteAccountHandler implements IApprovalHandler {

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
        return ApprovalType.DELETE_ACCOUNT;
    }

    @Override
    public void handle(ApprovalFlow approvalFlow, Integer approvalResult) {
        List<ApprovalFlowInfoGranted> approvalFlowInfoGranteds = approvalFlowInfoGrantedService.list(new QueryWrapper<ApprovalFlowInfoGranted>().eq("flow_id", approvalFlow.getId()));
        //删除账户【审批通过，删除账户； 审批拒绝，更改状态】
        approvalFlowInfoGranteds.forEach(approvalFlowInfoGranted -> {
            if (ConstantsDto.APPROVAL_AUTHORIZED == approvalResult){
                approvalDetailMapper.deleteGrantedById(approvalFlowInfoGranted.getGrantedId());
                //删除设备账号表中的记录
                approvalDetailMapper.deleteAccountByAccountId(approvalFlowInfoGranted.getAssetAccountId());
            }else if (ConstantsDto.APPROVAL_REJECTED == approvalResult){//删除授权的审批被拒绝时，恢复原有授权的状态(status和isdelete)，使其正常使用。
                approvalDetailMapper.updateGrantedStatusByGrantedId(approvalFlowInfoGranted.getGrantedId(), ConstantsDto.CONST_FALSE, ConstantsDto.APPROVAL_AUTHORIZED);
            }
        });
    }
}
