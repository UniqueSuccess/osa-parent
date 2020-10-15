package com.goldencis.osa.approval.service.impl;

import com.goldencis.osa.approval.entity.ApprovalFlowInfoCommand;
import com.goldencis.osa.approval.mapper.ApprovalFlowInfoCommandMapper;
import com.goldencis.osa.approval.service.IApprovalFlowInfoCommandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 具体审批流程命令关联表--定义审批命令信息  服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
@Service
public class ApprovalFlowInfoCommandServiceImpl extends ServiceImpl<ApprovalFlowInfoCommandMapper, ApprovalFlowInfoCommand> implements IApprovalFlowInfoCommandService {

    @Autowired
    ApprovalFlowInfoCommandMapper approvalFlowInfoCommandMapper;

    @Override
    public ApprovalFlowInfoCommand getApprovalFlowInfoCommandByFlowId(String flowId) {

        return approvalFlowInfoCommandMapper.getApprovalFlowInfoCommandByFlowId(flowId);
    }
}
