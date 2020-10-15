package com.goldencis.osa.asset.excel.domain;

/**
 * 设备sheet页
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-13 11:42
 **/
public enum AssetSheet {
    USERREPORT("授权用户报表"),
    COMMANDREPORT("违规命令报表"),
    ASSETREPORT("授权设备报表"),
    ASSET("设备"),
    ASSETGROUP("设备组"),
    RULE("连接工具"),
    ACCOUNT("设备账号");

    private String tag;

    AssetSheet(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }
}
