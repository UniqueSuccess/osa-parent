package com.goldencis.osa.asset.sso.replace.proxy;

import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.resource.domain.IRelation;
import com.goldencis.osa.asset.sso.replace.IRulePlaceholder;
import reactor.util.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface IPlaceProxy {

    /**
     * 设备类型
     * @return
     */
    @NotNull
    IRelation type();

    /**
     * 转换
     * @param asset 资产
     * @return
     */
    @NotNull
    List<IRulePlaceholder> transform(@NotNull Asset asset, @Nullable AssetAccount account);
}
