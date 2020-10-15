package com.goldencis.osa.asset.resource.domain;

import com.goldencis.osa.asset.resource.AssetResourceType;
import org.springframework.stereotype.Component;

/**
 * 标记每个最高级设备类型对应的Mapper
 */
public interface IRelation {

    /**
     * 最上级的设备类型id
     * @return
     */
    Integer typeId();

    /**
     * 设备类型(对应哪一张设备从表)
     * @return
     */
    AssetResourceType resourceType();

}
