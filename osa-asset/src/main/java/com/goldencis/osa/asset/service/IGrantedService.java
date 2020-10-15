package com.goldencis.osa.asset.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.asset.domain.GrantDetail;
import com.goldencis.osa.asset.domain.GrantDetailParam;
import com.goldencis.osa.asset.entity.Granted;
import com.goldencis.osa.asset.entity.GrantedSignUser;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备授权表 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-05
 */
public interface IGrantedService extends IService<Granted> {

    String grantedAsset4User(String userJson, String grantedJson);

    /**
     * 根据用户guid获取设备账号ids
     * @param userGuid 用户id
     * @param isUserGroup
     */
    List<String> findAccountIdsByUserGuid(String userGuid, boolean isUserGroup);

    /**
     * 根据用户guid获取设备组ids
     * @param guid 用户id
     * @param isUserGroup
     */
    List<String> findAssetgroupIdsByUserGuid(String guid, boolean isUserGroup);


    /**
     * 获取授权设备账号列表
     */
    IPage<Granted> getAssestAccountsInPage(Map<String, String> params);

    /**
     * 获取授权设备组列表
     */
    IPage<Granted> getAssestgroupsInPage(Map<String, String> params);

    /**
     * 根据授权id 删除授权表中的设备账号
     */
    String deleteGrantedById(Integer grantedId);

    /**
     * 查找当前用户的授权集合，包含设备组的授权需要转化具体的设备账户授权，用户运维人员单点登录
     * @return
     */
    IPage<GrantedSignUser> getGrantedsByCurrentUser4SSOInPage(Map<String, String> params);

    /**
     * 查询当前用户是否有操作设备、设备账号权限
     */
    boolean checkCurrentUser4AssetAccout(Integer assetId, Integer assetAccountId);

    /**
     * 通过设备id删除设备，批量删除授权信息
     */
    String deleteGrantedAssetByAssetId(Integer id);

    /**
     * 申请删除设备账号
     * @param accountIdList 设备账号id集合
     */
    void applyForDeleteAssetAccount(@NotNull List<Integer> accountIdList);

    /**
     * 申请删除设备组
     * @param assetGroupIdList 设备组id集合
     */
    void applyForDeleteAssetGroup(@NotNull List<Integer> assetGroupIdList);

    /**
     * 根据用户id和授权状态获取授权集合
     * @param userId 用户id
     * @param status 授权状态
     * @return 授权集合
     */
    List<Granted> findGrantedListByUserIdAndStatus(String userId, int status);

    /**
     * 撤销授权
     * @param id 授权id
     */
    String revokeGrantedById(Integer id);

    /**
     * 分页获取授权详情
     * @param param
     * @return
     */
    IPage<GrantDetail> getGrantDetailInPage(GrantDetailParam param);

    /**
     * 获取授权详情数量
     * @param param
     * @return
     */
    Integer getGrantDetailCount(GrantDetailParam param);

    /**
     * 查询操作员对应的管理的所有设备的id集合
     * @param guid 操作员guid
     * @return 设备组的id集合
     */
    List<Integer> getAssetIdsByOperator(String guid);

    /**
     * 查询操作员对应的管理的所有设备组的id集合
     * @param guid 操作员guid
     * @return
     */
    List<Integer> getGroupIdsByOperator(String guid);
}
