package com.goldencis.osa.approval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.approval.entity.ApprovalDetail;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 * 具体审批流程审批结果关联表--定义审批结果信息 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
public interface ApprovalDetailMapper extends BaseMapper<ApprovalDetail> {

    /**
     * 通过授权id 删除授权
     */
    void deleteGrantedById(Integer grantedId);

    /**
     * 通过授权id 修改授权状态
     */
    void updateGrantedStatusByGrantedId(@Param("grantedId") Integer grantedId, @Param("isdelete") Integer isdelete, @Param("approvalResult") Integer approvalResult);

    /**
     * 通过设备id 删除设备相关
     */
    void deleteAssetRelatedByAssetId(Integer assetId);

    void deleteAssetGroupRelatedByAssetGroupId(@Param(value = "assetGroupId") Integer assetGroupId);

    /**
     * 通过账户id，删除账户记录
     * @param accountId 账户id
     */
    void deleteAccountByAccountId(Integer accountId);

    /**
     * 获取设备组下的设备数量
     * @param assetGroupId 设备组id
     * @return
     */
    Integer getAssetCountByAssetGroupId(@Param(value = "assetGroupId") Integer assetGroupId);

    /**
     * 获取设备组下的子设备组数量
     * @param assetGroupId 设备组id
     * @return
     */
    Integer getChildCountByAssetGroupId(@Param(value = "assetGroupId") Integer assetGroupId);

}
