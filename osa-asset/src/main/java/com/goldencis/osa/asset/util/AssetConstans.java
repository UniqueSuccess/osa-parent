package com.goldencis.osa.asset.util;

/**
 * 设备相关常量
 */
public class AssetConstans {

    //用于树结构中，区分不同类型相同id的节点
    public static final String PREFIX_GROUP = "group-";
    public static final String PREFIX_ASSET = "asset-";
    public static final String PREFIX_ACCOUNT = "account-";

    //树节点类型:1代表设备组，2代表设备，3代表账户
    public static final int NODE_GROUP = 1;
    public static final int NODE_ASSET = 2;
    public static final int NODE_ACCOUNT = 3;

}
