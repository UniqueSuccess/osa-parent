package com.goldencis.osa.approval.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.approval.entity.ApprovalFlow;
import com.goldencis.osa.approval.entity.ApprovalFlowInfoGranted;
import com.goldencis.osa.core.entity.Approval;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  具体审批流程主表--定义审批公共信息 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
public interface IApprovalFlowService extends IService<ApprovalFlow> {

    /**
     * 分页获取授权审批列表
     */
    IPage<ApprovalFlow> getApprovalFlowGrantedsInPage(Map<String, String> params);

    /**
     * 分页获取命令审批列表
     */
    IPage<ApprovalFlow> getApprovalFlowCommandsInPage(Map<String, String> params);

    /**
     * 新增授权审批
     */
    void saveApprovalFlowGranted(ApprovalFlow approvalFlow);

    /**
     * 新增命令审批
     */
    void saveApprovalFlowCommand(ApprovalFlow approvalFlow);

    /**
     * 获取分页授权详情
     */
    IPage<ApprovalFlowInfoGranted> getApprovalFlowGrantedsDetailInPage(Map<String, String> params);

    /**
     * 获取所有的授权数据
     */
    List<ApprovalFlow> getApprovalFlowGranteds();


    /**
     * 检查所有待审批的数据
     */
    void checkApprovalFlowPendingGranteds();

    /**
     * 根据授权id 删除 待审批记录
     * @param grantedId 授权id
     */
    void deleteApprovalFlowByGrantedId(Integer grantedId);

    /**
     * 根据设备id，查找对应设备是否含有在审批中的申请
     * @param assetId 设备id
     * @return 是否有正在进行的审批
     */
    boolean isInApprovalByAssetIdAndApprovalType(Integer assetId, Integer definitionId);

    /**
     * 根据设备id，查找对应设备是否含有在审批中的申请
     * @param assetAccountId 设备账号id
     * @return 是否有正在进行的审批
     */
    boolean isInApprovalByAssetAccountIdAndApprovalType(Integer assetAccountId, Integer definitionId);

    /**
     * 根据设备id，查找对应设备是否含有在审批中的申请
     * @param assetGroupId 设备账号id
     * @return 是否有正在进行的审批
     */
    boolean isInApprovalByAssetGroupIdAndApprovalType(Integer assetGroupId, Integer definitionId);

    /**
     * 通过授权id 获取 同时授权的 授权id
     * @param grantedId 授权id
     * @return 同时授权集合
     */
    Set<Integer> getGrantedIdsFromFlowByGrantedId(Integer grantedId);

    /**
     * 根据flow id获取flow 和 审批结果备注
     * @param flowId 审批id
     */
    ApprovalFlow getApprovalFlowDetailByFlowId(String flowId);

    /**
     * 获取审批content
     */
    Approval getApprovalExpireSettings();

}
