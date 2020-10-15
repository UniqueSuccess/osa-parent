package com.goldencis.osa.session.domain;

import lombok.Data;

/**
 * remoteApp链接时的配置信息
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-22 17:31
 **/
@Data
public class RemoteAppInfo {
    /**
     * 应用程序发布器的ip地址
     */
    private String ip;
    /**
     * 运维协议
     */
    private String protocol = "rdp";
    /**
     * rdp协议端口
     */
    private int port;
    /**
     * 应用程序发布器的账号
     */
    private String account;
    /**
     * 应用程序发布器的密码
     */
    private String password;
    /**
     * 发布规则
     */
    private String rule;
    /**
     * 运维人员登录设备的ip地址
     */
    private String userIp;

}
