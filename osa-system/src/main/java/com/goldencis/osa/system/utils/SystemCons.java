package com.goldencis.osa.system.utils;

/**
 * 系统模块--常量
 */
public class SystemCons {

    /**
     * 1用户黑名单，2用户白名单，3 用户ip黑名单，4 用户ip 白名单，5 设备黑名单，6设备白名单
     */
    public static final int SYSTEM_USER_BLACK= 1;
    public static final int SYSTEM_USER_WHITE = 2;
    public static final int SYSTEM_USER_IP_BLACK= 3;
    public static final int SYSTEM_USER_IP_WHITE = 4;
    public static final int SYSTEM_ASSET_IP_BLACK = 5;
    public static final int SYSTEM_ASSET_IP_WHITE = 6;

    //上传的客户端和跳板机的安装包和升级包的类型
    public static final int CLIENT_PACKAGE = 1;
    public static final int CLIENT_PACKAGE_XP = 2;
    public static final int CLIENT_UPDATE = 3;
    public static final int BRIDGE_PACKAGE = 4;
    public static final int BRIDGE_UPDATE = 5;
}
