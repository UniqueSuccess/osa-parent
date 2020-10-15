package com.goldencis.osa.asset.resource.domain;

import com.goldencis.osa.asset.resource.AssetResourceType;
import org.springframework.stereotype.Component;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-01 15:24
 **/
@Component
public class Network implements IRelation {
    @Override
    public Integer typeId() {
        return 4;
    }

    @Override
    public AssetResourceType resourceType() {
        return AssetResourceType.ASSETNET;
    }
}
