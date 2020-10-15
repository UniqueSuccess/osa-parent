package com.goldencis.osa.asset.sso.replace.proxy.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.entity.AssetDb;
import com.goldencis.osa.asset.resource.domain.Database;
import com.goldencis.osa.asset.resource.domain.IRelation;
import com.goldencis.osa.asset.service.IAssetDbService;
import com.goldencis.osa.asset.sso.replace.IRulePlaceholder;
import com.goldencis.osa.asset.sso.replace.impl.*;
import com.goldencis.osa.asset.sso.replace.proxy.IPlaceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 数据库类型的设备,替换占位符
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-07 17:52
 **/
@Component
public class DbPlaceProxy implements IPlaceProxy {

    @Autowired
    private Database database;
    @Autowired
    private IAssetDbService assetDbService;

    /**
     * 设备类型
     *
     * @return
     */
    @Override
    public @NotNull IRelation type() {
        return database;
    }

    /**
     * 转换
     *
     * @param asset 资产
     * @return
     */
    @Override
    public @NotNull List<IRulePlaceholder> transform(Asset asset, AssetAccount account) {
        List<IRulePlaceholder> list = new ArrayList<>();
        list.add(new IpPlaceholder(asset.getIp()));
        AssetDb db = assetDbService.getOne(new QueryWrapper<AssetDb>().eq("asset_id", asset.getId()));
        if (Objects.nonNull(db)) {
            list.add(new DbNamePlaceholder(db.getDbName()));
            list.add(new PortPlaceholder(db.getPort()));
        }
        if (Objects.nonNull(account)) {
            list.add(new AccountPlaceholder(account.getUsername()));
            list.add(new PwdPlaceholder(account.getPassword()));
        }
        return list;
    }
}
