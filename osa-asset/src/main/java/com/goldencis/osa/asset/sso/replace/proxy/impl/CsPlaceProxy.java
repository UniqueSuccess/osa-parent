package com.goldencis.osa.asset.sso.replace.proxy.impl;

import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.resource.domain.Cs;
import com.goldencis.osa.asset.resource.domain.IRelation;
import com.goldencis.osa.asset.sso.replace.IRulePlaceholder;
import com.goldencis.osa.asset.sso.replace.impl.AccountPlaceholder;
import com.goldencis.osa.asset.sso.replace.impl.IpPlaceholder;
import com.goldencis.osa.asset.sso.replace.impl.PwdPlaceholder;
import com.goldencis.osa.asset.sso.replace.proxy.IPlaceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-07 17:44
 **/
@Component
public class CsPlaceProxy implements IPlaceProxy {

    @Autowired
    private Cs cs;

    /**
     * 设备类型
     *
     * @return
     */
    @Override
    public IRelation type() {
        return cs;
    }

    /**
     * 转换
     *
     * @param asset   资产
     * @param account
     * @return
     */
    @Override
    public @NotNull List<IRulePlaceholder> transform(@NotNull Asset asset, AssetAccount account) {
        List<IRulePlaceholder> list = new ArrayList<>();
        list.add(new IpPlaceholder(asset.getIp()));
        if (Objects.nonNull(account)) {
            list.add(new AccountPlaceholder(account.getUsername()));
            list.add(new PwdPlaceholder(account.getPassword()));
        }
        return list;
    }


}
