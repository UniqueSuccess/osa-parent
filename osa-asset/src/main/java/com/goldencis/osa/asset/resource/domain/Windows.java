package com.goldencis.osa.asset.resource.domain;

import com.goldencis.osa.asset.resource.AssetResourceType;
import org.springframework.stereotype.Component;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-01 15:24
 **/
@Component
public class Windows implements IRelation {
    @Override
    public Integer typeId() {
        return 2;
    }

    @Override
    public AssetResourceType resourceType() {
        return null;
    }
}
