package com.goldencis.osa.approval.service;

import com.goldencis.osa.approval.entity.ApprovalDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 具体审批流程审批结果关联表--定义审批结果信息 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
public interface IApprovalDetailService extends IService<ApprovalDetail> {

    /**
     * 审批结果
     * @param flowId 审批id
     * @param approvalResult 审批结果
     * @param approvalRemark 审批备注
     */
    String approvalResult(String flowId, Integer approvalResult, String approvalRemark, Integer typeFrom);
}
