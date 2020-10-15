package com.goldencis.osa.session.domain;

import lombok.Data;

/**
 * 关闭remoteApp时,推送的mq消息
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 15:45
 **/
@Data
public class StopRemoteApp {

    private String sessionid;

    public StopRemoteApp(String sessionid) {
        this.sessionid = sessionid;
    }
}
