package com.goldencis.osa.approval.handle.impl;

import com.goldencis.osa.approval.entity.ApprovalFlow;
import com.goldencis.osa.approval.handle.ApprovalType;
import com.goldencis.osa.approval.handle.IApprovalHandler;
import org.springframework.stereotype.Component;

/**
 * 命令审批
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-25 16:39
 **/
@Component
public class CommandAddHandler implements IApprovalHandler {

    /**
     * 审批类型
     *
     * @return
     */
    @Override
    public ApprovalType type() {
        return ApprovalType.COMMAND_ADD;
    }

    @Override
    public void handle(ApprovalFlow approvalFlow, Integer approvalResult) {

    }
}
