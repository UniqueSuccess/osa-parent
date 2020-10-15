package com.goldencis.osa.approval.mapper;

import com.goldencis.osa.approval.entity.ApprovalFlowInfoCommand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 具体审批流程命令关联表--定义审批命令信息  Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
public interface ApprovalFlowInfoCommandMapper extends BaseMapper<ApprovalFlowInfoCommand> {

    /**
     * 通过授权命令id 获取授权输入内容
     */
    String getCommandContentInFlowCommand(String flowId);

    /**
     * 通过命令审批id  获取 审批用户、设备信息
     * @param flowId 命令审批id
     */
    ApprovalFlowInfoCommand getApprovalFlowInfoCommandByFlowId(String flowId);
}
