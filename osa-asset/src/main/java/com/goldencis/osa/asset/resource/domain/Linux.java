package com.goldencis.osa.asset.resource.domain;

import com.goldencis.osa.asset.resource.AssetResourceType;
import org.springframework.stereotype.Component;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-01 15:22
 **/
@Component
public class Linux implements IRelation {

    @Override
    public Integer typeId() {
        return 1;
    }

    @Override
    public AssetResourceType resourceType() {
        return null;
    }
}
