package com.goldencis.osa.approval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.approval.entity.ApprovalFlowInfoGranted;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 具体审批流程授权关联表--定义审批授权信息 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
public interface ApprovalFlowInfoGrantedMapper extends BaseMapper<ApprovalFlowInfoGranted> {

    /**
     *  根据 授权审批id 获取 目标设备数量
     */
    int countAssetInFlowGranted(String flowId);

    /**
     * 获取分页授权详细
     */
    int countApprovalFlowGrantedsDetailInPage(Map<String, Object> paramMap);

    /**
     * 获取分页授权详细
     */
    List<ApprovalFlowInfoGranted> getApprovalFlowGrantedsDetailInPage(Map<String, Object> paramMap);


}
