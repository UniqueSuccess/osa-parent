package com.goldencis.osa.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.common.entity.ResultTree;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.system.domain.SystemAccountInfo;
import com.goldencis.osa.system.entity.UserAssetAssetgroup;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户、设备/设备组 关联表（操作员管理设备/设备组）  服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-12-07
 */
public interface IUserAssetAssetgroupService extends IService<UserAssetAssetgroup> {

    /**
     * 通过用户guid获取设备组树
     */
    List<ResultTree> getAssetgroupListTreeByUserGuid(String userGuid);

    /**
     * 通过用户guid获取设备树
     */
    List<ResultTree> getAssetListTreeByUserGuid(String userGuid);

    /**
     * 设备列表
     * @param userGuid 操作员guid
     * @return 数据列表
     */
    List<UserAssetAssetgroup> getAssetgroupListByUserGuid(String userGuid);

    /**
     * 获取操作员--设备组分页列表
     */
    IPage<UserAssetAssetgroup> getAssetgroupByUserGuidInPage(Map<String, String> params);

    /**
     * 获取操作员--设备分页列表
     */
    IPage<UserAssetAssetgroup> getAssetByUserGuidInPage(Map<String, String> params);

    /**
     * 获取系统管理员列表
     */
    List<User> getSystemUsers(String searchStr);

    /**
     * 根据用户id获取右侧详细信息(设备权限列表和审计权限列表)
     * @param id 用户id
     */
    SystemAccountInfo getDetailByUserGuid(@NotNull String id);

    /**
     * 根据用户id获取设备权限
     * @param id 用户id
     * @return
     */
    List<SystemAccountInfo.AssetPermission.AssetItem> getAssetPermissionByUserId(@NotNull String id);

    /**
     * 根据用户id获取设备组权限
     * @param id 用户id
     * @return
     */
    List<SystemAccountInfo.AssetPermission.AssetGroupItem> getAssetGroupPermissionByUserId(@NotNull String id);

    /**
     * 根据用户id获取审计权限
     * @param id
     * @param checkedOnly 是否只要勾选的
     * @return
     */
    List<SystemAccountInfo.AuditPermission.UserItem> getAuditPermissionByUserId(@NotNull String id, boolean checkedOnly);

    /**
     * 保存设备权限
     * @param userGuid 用户id
     * @param list 设备id集合
     */
    void saveAssetPermission(String userGuid, List<Integer> list);

    /**
     * 保存设备组权限
     * @param userGuid 用户id
     * @param list 设备组id集合
     */
    void saveAssetGroupPermission(String userGuid, List<Integer> list);

    void saveAuditPermission(String userGuid, List<String> list);

    void deleteAssetPermission(String userGuid, List<Integer> list);

    void deleteAssetGroupPermission(String userGuid, List<Integer> list);

    void deleteAuditPermission(String userGuid, List<String> list);
}
