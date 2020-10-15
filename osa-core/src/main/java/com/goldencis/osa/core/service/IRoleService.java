package com.goldencis.osa.core.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.core.entity.Role;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
public interface IRoleService extends IService<Role> {

    /**
     * 根据role的guid查找角色
     * @param guid 角色guid
     * @return 角色对象
     */
    Role findRoleByGuid(String guid);

    /**
     * 新增角色接口
     * @param role 角色对象
     */
    void saveRole(Role role);

    /**
     * 根据用户guid查询用户对应的角色集合
     * @param guid 用户guid
     * @return 角色集合
     */
    List<Role> getRoleListByUserguid(String guid);

    /**
     * 根据查询参数获取角色列表
     * @param params 查询参数
     * @return 角色列表
     */
    List<Role> getRoleListByParams(Map<String, String> params);

    /**
     * 检查角色名是否重复
     * @param role 角色对象
     * @return 是否可用
     */
    boolean checkRoleNameDuplicate(Role role);

    /**
     * 根据角色名称查找角色
     * @param name 角色名称
     * @return 角色
     */
    Role findRoleByName(String name);

    /**
     * 编辑角色
     * @param role 角色
     */
    void updateRole(Role role);

    /**
     * 保存角色权限
     * @param guid 角色唯一标示
     * @param list 权限集合
     */
    void updateRolePermissions(String guid, JSONArray list);

    /**
     * 删除角色
     * @param role
     * @return 删除的角色名称
     */
    String deleteRole(Role role);

    /**
     * 获取除普通用户之外的所有角色列表
     * @param excludeAdmin 是否排除管理员角色
     * @return
     */
    List<Role> getRoleListExcludeNormal(boolean excludeAdmin);
}
