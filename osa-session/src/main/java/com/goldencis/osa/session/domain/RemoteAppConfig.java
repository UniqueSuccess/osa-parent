package com.goldencis.osa.session.domain;

import lombok.Data;

/**
 * remoteApp单点登录配置信息
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-23 10:36
 **/
@Data
public class RemoteAppConfig {
    /**
     * 应用程序发布器id
     */
    private int publishId;
    /**
     * 应用程序发布器名称
     */
    private String publishName;
    /**
     * 协议
     */
    private String protocol = "rdp";
    /**
     * 端口
     */
    private int port = 3389;
    /**
     * sso(单点登录)id
     */
    private int ssoRuleId;
    /**
     * sso(单点登录)名称;链接工具名称
     */
    private String ssoRuleName;
    /**
     * 客户端地址,不同类型的客户端需要调用不同的接口
     */
    private String url;

    /**
     * 判断配置是否无效
     * @return 无效,返回true;否则,false
     */
    public boolean invalid() {
        return publishId == -1
                || ssoRuleId == -1;
    }
}
