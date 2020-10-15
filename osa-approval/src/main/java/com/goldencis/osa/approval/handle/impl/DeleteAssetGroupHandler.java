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
import java.util.Objects;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-26 15:30
 **/
@Component
public class DeleteAssetGroupHandler implements IApprovalHandler {

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
        return ApprovalType.DELETE_ASSET_GROUP;
    }

    @Override
    public void handle(ApprovalFlow approvalFlow, Integer approvalResult) {
        List<ApprovalFlowInfoGranted> list = approvalFlowInfoGrantedService.list(new QueryWrapper<ApprovalFlowInfoGranted>().eq("flow_id", approvalFlow.getId()));
        //授权删除【同意授权，删除设备； 拒绝授权，更改状态】
        for (ApprovalFlowInfoGranted detail : list) {
            if (ConstantsDto.APPROVAL_AUTHORIZED == approvalResult) {
                pass(detail);
            } else if (ConstantsDto.APPROVAL_REJECTED == approvalResult) {
                reject(detail);
            }
        }

    }

    /**
     * 审批通过
     * @param detail
     */
    private void pass(ApprovalFlowInfoGranted detail) {
        Integer assetgroupId = detail.getAssetgroupId();
        if (Objects.isNull(assetgroupId)) {
            return;
        }
        // 首先判断设备组是否存在子设备组,或者包含设备,如果存在,不允许删除这个设备组
        // 因为在提交审批之后,审批通过之前,用户有可能会修改这个设备组的信息
        boolean beDelete = canAssetGroupBeDelete(assetgroupId);
        if (beDelete) {
            approvalDetailMapper.deleteGrantedById(detail.getGrantedId());
            // 删除设备组所有关联表,以及设备组信息
            approvalDetailMapper.deleteAssetGroupRelatedByAssetGroupId(assetgroupId);
        }
    }

    /**
     * 审批拒绝
     * @param detail
     */
    private void reject(ApprovalFlowInfoGranted detail) {
        approvalDetailMapper.updateGrantedStatusByGrantedId(detail.getGrantedId(), ConstantsDto.CONST_FALSE, ConstantsDto.APPROVAL_AUTHORIZED);
    }

    /**
     * 判断这个设备组是否可以被删除
     * @param assetgroupId 设备组id
     * @return 如果可以,返回true;否则,返回false
     */
    private boolean canAssetGroupBeDelete(Integer assetgroupId) {
        Integer assetCount = approvalDetailMapper.getAssetCountByAssetGroupId(assetgroupId);
        if (Objects.nonNull(assetCount) && assetCount > 0) {
            return false;
        }
        Integer childCount = approvalDetailMapper.getChildCountByAssetGroupId(assetgroupId);
        if (Objects.nonNull(childCount) && childCount > 0) {
            return false;
        }
        return true;
    }
}
