package com.goldencis.osa.core.params;

import com.goldencis.osa.common.entity.Pagination;
import lombok.Data;

/**
 * 用户列表分页查询参数
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-04 09:36
 **/
@Data
public class UserParams extends Pagination {
    /**
     * 用户组id
     */
    private Integer usergroupPid;
    /**
     * 用户状态
     */
    private Integer status;
    /**
     * 认证方式
     */
    private Integer authenticationMethod;

}
