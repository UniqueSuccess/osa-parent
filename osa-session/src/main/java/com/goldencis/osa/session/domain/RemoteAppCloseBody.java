package com.goldencis.osa.session.domain;

import lombok.Data;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 10:59
 **/
@Data
public class RemoteAppCloseBody {
    /**
     * 用户手动关闭
     */
    public static final int STATE_CLOSE = 0;
    /**
     * 管理员阻断
     */
    public static final int STATE_BLOCK = 1;

    /**
     * 会话id
     */
    private String sessionid;
    /**
     * 关闭状态
     */
    private int state;
}
