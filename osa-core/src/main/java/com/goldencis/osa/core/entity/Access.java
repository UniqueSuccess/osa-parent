package com.goldencis.osa.core.entity;

import lombok.Data;

/**
 * 访问限制
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-27 17:47
 **/
@Data
public class Access {
    /**
     * 登录控制
     */
    private Login login;
    /**
     * 资源控制
     */
    private Assets resource;
}
