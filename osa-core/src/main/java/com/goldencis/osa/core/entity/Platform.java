package com.goldencis.osa.core.entity;

import lombok.Data;

/**
 * 管控平台
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-27 17:38
 **/
@Data
public class Platform {
    /**
     * 安全(密码限制)
     */
    private Security security;
    /**
     * 审批配置
     */
    private Approval approval;
    /**
     * 访问(访问限制)
     */
    private Access access;
    /**
     * 自定义配置
     */
    private Custom custom;

}
