package com.goldencis.osa.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.core.entity.UserRole;

/**
 * <p>
 * 用户角色关联表 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {
    /**
     * 根据用户guid删除用户角色
     */
    void deleteUserRoleByUserGuid(String userGuid);

    /**
     * 添加用户、角色关联表
     */
    boolean saveUserRole(UserRole userRole);

    /**
     * 根据用户名查找管理员角色的关联
     * @param username 用户名
     * @return 角色的关联
     */
    UserRole findAdminRoleRelationByUsername(String username);

    /**
     * 根据用户名查找普通角色的关联
     * @param username 用户名
     * @return 角色的关联
     */
    UserRole findNormalRoleRelationByUsername(String username);
}
