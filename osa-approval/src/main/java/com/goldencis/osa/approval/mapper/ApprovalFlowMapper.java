package com.goldencis.osa.approval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.approval.entity.ApprovalFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  具体审批流程主表--定义审批公共信息 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
public interface ApprovalFlowMapper extends BaseMapper<ApprovalFlow> {

    /**
     * 获取分页授权审批列表 数量
     */
    int countApprovalFlowGrantedsInPage(Map<String, Object> paramMap);

    /**
     * 获取分页授权审批列表
     */
    List<ApprovalFlow> getApprovalFlowGrantedsInPage(Map<String, Object> paramMap);

    /**
     * 获取分页命令审批列表 数量
     */
    int countApprovalFlowComandsInPage(Map<String, Object> paramMap);

    /**
     * 获取分页命令审批列表
     */
    List<ApprovalFlow> getApprovalFlowComandsInPage(Map<String, Object> paramMap);

    /**
     * 获取所有待审批的命令
     */
    List<ApprovalFlow> getApprovalFlowGranteds(Map<String, Object> paramMap);

    /**
     * 根据设备id和审批流程类型，查询正在进行中的审批集合
     * @param id 设备id
     * @param definitionId 审批流程类型
     * @return 正在进行中的审批集合
     */
    List<ApprovalFlow> findUnfinishedApprovalListByAssetIdAndDefinitionId(@Param(value = "assetId") Integer id, @Param(value = "definitionId")  Integer definitionId);

    /**
     * 根据flow id获取flow 和 审批结果备注
     * @param flowId 审批id
     */
    ApprovalFlow getApprovalFlowDetailByFlowId(String flowId);

    /**
     * 根据设备id和审批流程类型，查询正在进行中的审批集合
     * @param id 设备id
     * @param definitionId 审批流程类型
     * @return 正在进行中的审批集合
     */
    List<ApprovalFlow> findUnfinishedApprovalListByAssetAccountIdAndDefinitionId(@Param(value = "assetAccountId") Integer id, @Param(value = "definitionId")  Integer definitionId);

    /**
     * 根据设备id和审批流程类型，查询正在进行中的审批集合
     * @param id 设备id
     * @param definitionId 审批流程类型
     * @return 正在进行中的审批集合
     */
    List<ApprovalFlow> findUnfinishedApprovalListByAssetGroupIdAndDefinitionId(@Param(value = "assetGroupId") Integer id, @Param(value = "definitionId")  Integer definitionId);

    /**
     * 获取审批配置
     */
    String getApprovalExpireSettings();
}
