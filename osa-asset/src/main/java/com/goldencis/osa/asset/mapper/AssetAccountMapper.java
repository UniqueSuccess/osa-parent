package com.goldencis.osa.asset.mapper;

import com.goldencis.osa.asset.entity.AssetAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.asset.params.AssetAccountParams;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-10-25
 */
public interface AssetAccountMapper extends BaseMapper<AssetAccount> {

    int countAssetAccountsInPage(AssetAccountParams params);

    List<AssetAccount> getAssetAccountsInPage(AssetAccountParams params);

    List<AssetAccount> getAccountListByAssetGroupId(Integer id);
}
