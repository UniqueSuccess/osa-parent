package com.goldencis.osa.asset.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.params.AssetAccountParams;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-25
 */
public interface IAssetAccountService extends IService<AssetAccount> {

    /**
     * 保存账号信息
     * @param account
     */
    void saveOrUpdateAccountInfo(@NotNull AssetAccount account);

    /**
     * 设备账号分页查询
     * @param page 分页参数(存储查询结果)
     * @param params 查询参数
     */
    void getAssetAccountsInPage(@NotNull IPage<AssetAccount> page, @NotNull AssetAccountParams params);

    /**
     * 根据设备组id获取组内所有设备的账号信息
     * @param id 设备组id
     * @return
     */
    IPage<AssetAccount> getAccountListByAssetGroupId(Integer id);

    /**
     * 根据设备id删除账号信息
     * @param assetId
     */
    void deleteAccountByAssetId(@NotNull Integer assetId);

    /**
     * 根据账号id删除账号信息
     * @param accountId
     * @return 执行结果,如果为-1,表示该账号存在授权信息,已经提交审批;
     */
    int deleteAccountById(@NotNull Integer accountId);

    /**
     * 根据账号id删除一批账号信息
     * @param accountIdList 账号id列表
     * @return 执行结果,如果为-1,表示至少有一条账号存在授权信息,已经提交审批;
     */
    int deleteAccountById(@NotNull List<Integer> accountIdList);

}
