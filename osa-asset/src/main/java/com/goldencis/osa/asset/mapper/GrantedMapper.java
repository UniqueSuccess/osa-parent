package com.goldencis.osa.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.approval.entity.ApprovalFlowInfoGranted;
import com.goldencis.osa.asset.domain.GrantDetail;
import com.goldencis.osa.asset.domain.GrantDetailParam;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.Granted;
import com.goldencis.osa.asset.entity.GrantedSignUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备授权表 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-11-05
 */
public interface GrantedMapper extends BaseMapper<Granted> {

    /**
     * 根据用户guid获取授权设备 id集合
     */
    List<String> findAccountIdsByUserGuid(Map<String, Object> params);

    /**
     * 根据用户guid获取授权设备组 id集合
     */
    List<String> findAssetgroupIdsByUserGuid(Map<String, Object> params);


    /**
     * 分页获取授权 设备账号
     */
    int countAssestAccountsInPage(Map<String, Object> paramMap);

    List<Granted> getAssestAccountsInPage(Map<String, Object> paramMap);

    /**
     * 分页获取授权 设备组
     */
    int countAssestgroupsInPage(Map<String, Object> paramMap);

    List<Granted> getAssestgroupsInPage(Map<String, Object> paramMap);

    /**
     * 根据用户guid获取授权设备账号资源
     */
    List<Granted> getUserGrantedAssestAccounts(Map<String, String> params);

    /**
     * 根据用户guid 获取授权设备组
     */
    List<Asset> getUserGrantedAssestgroups(Map<String, String> params);

    /**
     * 根据用户guid 获取用户组  授权设备账号
     */
    List<Granted> getUsergroupGrantedAssestAccounts(Map<String, String> params);

    /**
     * 根据用户guid 获取用户组  授权设备组的设备
     */
    List<Asset> getUsergroupGrantedAssestgroups(Map<String, String> params);

    /**
     * 判断保存设备账号是否正确性
     */
    int checkAccountCorrect(Map<String, Integer> map);

    /**
     * 判断设备授权是否已存在
     */
    Granted checkAccountExist(Map<String, Object> map);

    /**
     * 根据设备id获取设备账号id
     */
    List<Integer> getAccountIdsByAssetId(Integer assetId);

    /**
     * 根据设备账号id集合 获取账号分页
     */
    List<GrantedSignUser> getGrantedSignUserInPage( Map<String,Object> map );

    /**
     * 根据设备账号id集合 获取账号总数
     */
    int countGrantedSignUserInPage(Map<String, Object> paramMap);

    /**
     * 查询用户是否有操作设备、设备账号权限
     */
    int checkUser4AssetAccout(Map<String, Object> map);

    /**
     * 用户：通过授权账号 授权id 生成授权审批数据
     */
    ApprovalFlowInfoGranted getApprovalFlowInfoGrantedByAssetAccountUserId(Integer id);

    /**
     * 用户：通过设备组 授权id 生成授权审批数据
     */
    ApprovalFlowInfoGranted getApprovalFlowInfoGrantedByAssetgroupUserId(Integer id);

    /**
     * 用户组：通过授权账号 授权id 生成授权审批数据
     */
    ApprovalFlowInfoGranted getApprovalFlowInfoGrantedByAssetAccountUsergroupId(Integer id);

    /**
     * 用户组：通过设备组 授权id 生成授权审批数据
     */
   ApprovalFlowInfoGranted getApprovalFlowInfoGrantedByAssetgroupUsergroupId(Integer id);

    List<Granted> findGrantedListByUserIdAndStatus(@Param(value = "userId") String userId, @Param(value = "status") int status);

    /**
     * 根据用户id分页获取授权详情
     * @return
     */
    List<GrantDetail> getGrantDetailInPage(GrantDetailParam param);

    Integer getGrantDetailCount(GrantDetailParam param);

    /**
     * 查询操作员对应的管理的所有设备的id集合
     * @param guid 操作员guid
     * @return 设备组的id集合
     */
    List<Integer> getAssetIdsByOperator(String guid);

    /**
     * 查询操作员对应的管理的所有设备组的id集合
     * @param guid
     * @return
     */
    List<Integer> getGroupIdsByOperator(String guid);
}
