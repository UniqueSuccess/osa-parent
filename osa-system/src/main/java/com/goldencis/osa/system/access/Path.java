package com.goldencis.osa.system.access;

/**
 * 需要写入的文件路径
 */
public enum Path {

    /**
     * IP黑名单列表
     */
    bips("/proc/osa/bips"),
    /**
     * 端口黑名单列表
     */
    bports("/proc/osa/bports"),
    /**
     * 业务服务器IP
     */
    local("/proc/osa/local"),
    /**
     * 阻断MAC
     */
    mac("/proc/osa/mac"),
    /**
     * 阻断口
     */
    nac_eth("/proc/osa/nac_eth"),
    /**
     * 受控服务器IP列表
     */
    servers("/proc/osa/servers"),
    /**
     * 准入开启开关
     */
    Switch("/proc/osa/switch"),
    /**
     * IP白名单列表
     */
    wips("/proc/osa/wips"),
    /**
     * 端口白名单列表
     */
    wports("/proc/osa/wports"),
    /**
     * 受控端口列表
     */
    ports("/proc/osa/ports");

    /**
     * 文件路径
     */
    private String path;

    Path(String path) {
        this.path = path;
    }

    public String path() {
        return this.path;
    }
}
