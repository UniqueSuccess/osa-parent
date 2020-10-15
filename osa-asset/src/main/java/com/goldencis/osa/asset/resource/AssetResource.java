package com.goldencis.osa.asset.resource;

import javax.validation.constraints.NotNull;

public interface AssetResource {

    /**
     * 获取设备id
     * @return
     */
    Integer getAssetId();

    /**
     * 设置设备id
     * @param assetId
     */
    void setAssetId(@NotNull Integer assetId);
}
