package com.goldencis.osa.approval.handle;

import com.goldencis.osa.approval.entity.ApprovalFlow;

/**
 * 处理审批结果的接口
 */
public interface IApprovalHandler {

    /**
     * 审批类型
     * @return
     */
    ApprovalType type();

    void handle(ApprovalFlow approvalFlow, Integer approvalResult);

}
