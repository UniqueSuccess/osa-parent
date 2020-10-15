package com.goldencis.osa.approval.handle.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.approval.entity.ApprovalFlow;
import com.goldencis.osa.approval.entity.ApprovalFlowInfoGranted;
import com.goldencis.osa.approval.handle.ApprovalType;
import com.goldencis.osa.approval.handle.IApprovalHandler;
import com.goldencis.osa.approval.mapper.ApprovalDetailMapper;
import com.goldencis.osa.approval.service.IApprovalFlowInfoGrantedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-25 16:48
 **/
@Component
public class GrantedAddHandler implements IApprovalHandler {

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
        return ApprovalType.GRANTED_ADD;
    }

    @Override
    public void handle(ApprovalFlow approvalFlow, Integer approvalResult) {
        List<ApprovalFlowInfoGranted> approvalFlowInfoGranteds = approvalFlowInfoGrantedService.list(new QueryWrapper<ApprovalFlowInfoGranted>().eq("flow_id",approvalFlow.getId()));
        if (CollectionUtils.isEmpty(approvalFlowInfoGranteds)) {
            return;
        }
        System.out.println("审批结果："+approvalResult);
        //授权 添加【更改授权状态】
        approvalFlowInfoGranteds.forEach(approvalFlowInfoGranted -> approvalDetailMapper.updateGrantedStatusByGrantedId(approvalFlowInfoGranted.getGrantedId(), 0, approvalResult));
    }
}
