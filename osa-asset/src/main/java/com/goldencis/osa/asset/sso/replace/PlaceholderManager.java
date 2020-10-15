package com.goldencis.osa.asset.sso.replace;

import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.entity.AssetType;
import com.goldencis.osa.asset.service.IAssetTypeService;
import com.goldencis.osa.asset.sso.replace.proxy.IPlaceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.util.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 统一管理占位符
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-07 19:51
 **/
@Component
public class PlaceholderManager {

    private final Logger logger = LoggerFactory.getLogger(PlaceholderManager.class);

    @Autowired
    private IAssetTypeService assetTypeService;
    @Autowired
    private List<? extends IPlaceProxy> proxies;

    public List<IRulePlaceholder> place(@NotNull Asset asset, @Nullable AssetAccount account) {
        AssetType assetType = assetTypeService.getMostSuperiorAssetTypeById(asset.getType());
        if (Objects.isNull(assetType)) {
            logger.warn("设备类型错误 {}", asset.toString());
            return new ArrayList<>(0);
        }
        for (IPlaceProxy proxy : proxies) {
            if (proxy.type().typeId().equals(assetType.getId())) {
                return proxy.transform(asset, account);
            }
        }
        return new ArrayList<>(0);
    }

}
