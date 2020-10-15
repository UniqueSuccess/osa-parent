package com.goldencis.osa.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.entity.Granted;
import com.goldencis.osa.asset.mapper.AssetAccountMapper;
import com.goldencis.osa.asset.params.AssetAccountParams;
import com.goldencis.osa.asset.service.IAssetAccountService;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.IGrantedService;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.core.utils.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 设备账号模块, 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-25
 */
@Service
public class AssetAccountServiceImpl extends ServiceImpl<AssetAccountMapper, AssetAccount> implements IAssetAccountService {

    private final IAssetService assetService;
    private final IGrantedService grantedService;

    @Autowired
    @Lazy
    public AssetAccountServiceImpl(IAssetService assetService, IGrantedService grantedService) {
        this.assetService = assetService;
        this.grantedService = grantedService;
    }

    @Override
    public void saveOrUpdateAccountInfo(AssetAccount account) {
        // 检查设备id是否存在
        if (assetService.getById(account.getAssetId()) == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        Integer trusteeship = account.getTrusteeship();
        if (trusteeship == null) {
            throw new IllegalArgumentException("账号托管类型错误");
        }
        if (ConstantsDto.ACCOUNT_TRUSTEESHIP_TRUE != trusteeship && ConstantsDto.ACCOUNT_TRUSTEESHIP_FALSE != trusteeship) {
            throw new IllegalArgumentException("账号托管类型错误");
        }
        this.saveOrUpdate(account);
    }

    @Override
    public void getAssetAccountsInPage(@NotNull IPage<AssetAccount> page, @NotNull AssetAccountParams params) {
        QueryUtils.addFuzzyQuerySymbols(params);
        int total = baseMapper.countAssetAccountsInPage(params);
        List<AssetAccount> list = baseMapper.getAssetAccountsInPage(params);
        page.setTotal(total);
        page.setRecords(list);
    }

    @Override
    public IPage<AssetAccount> getAccountListByAssetGroupId(Integer id) {
        IPage<AssetAccount> page = new Page<>();
        List<AssetAccount> list = baseMapper.getAccountListByAssetGroupId(id);
        page.setRecords(list);
        page.setTotal(list.size());
        return page;
    }

    @Override
    public void deleteAccountByAssetId(@NotNull Integer assetId) {
        List<AssetAccount> list = list(new QueryWrapper<AssetAccount>().eq("asset_id", assetId));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(item -> {
            baseMapper.delete(new QueryWrapper<AssetAccount>().eq("id", item.getId()));
        });
    }

    @Override
    public int deleteAccountById(@NotNull Integer accountId) {
        // 检查账号是否存在授权信息
        List<Granted> list = grantedService.list(new QueryWrapper<Granted>().eq("account_id", accountId));
        // 存在授权,需要提交审批
        if (!CollectionUtils.isEmpty(list)) {
//            approvalFlowService.
            return -1;
        }
        this.removeById(accountId);
        return 0;
    }

    @Override
    public int deleteAccountById(@NotNull List<Integer> accountIdList) {
        Objects.requireNonNull(accountIdList, "账号集合不能为空");
        int status = 0;
        for (Integer accountId : accountIdList) {
            try {
                int i = this.deleteAccountById(accountId);
                if (i == -1) {
                    status = -1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }
}
