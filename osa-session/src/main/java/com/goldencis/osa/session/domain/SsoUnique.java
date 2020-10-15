package com.goldencis.osa.session.domain;

import lombok.Data;

/**
 * redis中存储设备唯一标示的类型
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-04 16:39
 **/
@Data
public class SsoUnique {
    /**
     * 运维设备的唯一标示
     */
    private String devUnique;
    /**
     * 跳板机的唯一标示
     */
    private String jsUnique;
    /**
     * 会话id
     */
    private String sessionId;
    /**
     * 登录代理类型，包括coco代理(CO),guacamole代理(GA),客户端代理(RA)
     */
    private String loginFrom;
    /**
     * 标记该次会话是否已经被消费掉
     */
    private boolean used;

    /**
     * 发起sso的运维人员ip
     */
    private String ip;

    public SsoUnique() {
        used = false;
    }
}
