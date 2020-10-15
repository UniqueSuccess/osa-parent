package com.goldencis.osa.session.domain;

import lombok.Data;

/**
 * 请求开启remoteApp的post接口中参数
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 10:54
 **/
@Data
public class RemoteAppOpenBody {
    /**
     * 会话id
     */
    private String sessionid;
    /**
     * 跳板机设备的唯一标识
     */
    private String unique;

}
