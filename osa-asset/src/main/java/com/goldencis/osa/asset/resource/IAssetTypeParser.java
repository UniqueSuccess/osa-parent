package com.goldencis.osa.asset.resource;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-01 14:24
 **/
public interface IAssetTypeParser {

    /**
     * 通过设备类型,查询出Mapper的类型
     * @param assetType 设备类型,t_asset表中type字段
     * @return
     * @see com.goldencis.osa.asset.resource.AssetResourceType
     */
    AssetResourceType parse(@NotNull Integer assetType);

}
