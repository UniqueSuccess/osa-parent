package com.goldencis.osa.asset.resource;

/**
 * 设备从表类型
 */
public enum AssetResourceType {
    /**
     * t_asset_bs
     */
    ASSETBS(1),
    /**
     * t_asset_cs
     */
    ASSETCS(2),
    /**
     * t_asset_db
     */
    ASSETDB(3),
    /**
     * t_asset_net
     */
    @Deprecated
    ASSETNET(4);

    private Integer value;

    AssetResourceType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
