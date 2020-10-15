package com.goldencis.osa.common.entity;

import lombok.Data;

/**
 * 邮箱配置
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-23 15:46
 **/
@Data
public class MailConfig {

    /**
     * 邮箱服务器
     */
    private String serverAddress;
    /**
     * smtp端口
     */
    private Integer port;
    /**
     * 发送邮箱
     */
    private String from;
    /**
     * 发送邮箱的密码
     */
    private String password;

}
