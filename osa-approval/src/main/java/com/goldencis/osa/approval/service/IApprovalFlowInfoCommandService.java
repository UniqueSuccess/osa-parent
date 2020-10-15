package com.goldencis.osa.approval.service;

import com.goldencis.osa.approval.entity.ApprovalFlowInfoCommand;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 具体审批流程命令关联表--定义审批命令信息  服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
public interface IApprovalFlowInfoCommandService extends IService<ApprovalFlowInfoCommand> {

    /**
     * 根据命令 flowId 获取 命令详情
     * @param flowId
     * @return
     */
    ApprovalFlowInfoCommand getApprovalFlowInfoCommandByFlowId(String flowId);
}
