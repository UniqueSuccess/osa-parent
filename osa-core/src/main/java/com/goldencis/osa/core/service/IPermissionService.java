package com.goldencis.osa.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.core.entity.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 访问资源权限表 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
public interface IPermissionService extends IService<Permission> {

    /**
     * 根据资源类型和资源id查找对应的资源
     * @param resourceType 资源类型
     * @param resourceId 资源id
     * @return 资源对象
     */
    Resource findResourceByResourceTypeAndId(Integer resourceType, Integer resourceId);

    /**
     * 根据资源类型查找对应的全部资源集合
     * @param resourceType 资源类型
     * @return 全部资源集合
     */
    List<? extends Resource> findResourceListByResourceType(Integer resourceType);

    /**
     * 根据资源类型查找对应的全部可见资源集合
     * @param resourceType 资源类型
     * @return 全部资源集合
     */
    List<? extends Resource> findVisibleResourceListByResourceType(Integer resourceType);

    /**
     * 根据资源类型查找对应的全部授权可见资源集合
     * @param resourceType 资源类型
     * @return 全部资源集合
     */
    List<? extends Resource> findGrantVisibleResourceListByResourceType(Integer resourceType);

    /**
     * 根据权限查找对应的资源
     * @param permission 权限
     * @return 资源
     */
    Resource findResourceByPermission(Permission permission);

    /**
     * 根据权限id查找权限及对应的资源
     * @param id 权限id
     * @return 权限对象
     */
    Permission findPermissionById(Integer id);

    /**
     * 根据用户和指定资源类型查询对应的权限资源集合
     * @param user 查询的用户
     * @param resourceType 资源类型
     * @return 权限资源集合
     */
    List<Resource> findUserPermissionsByResourceType(User user, Integer resourceType);

    /**
     * 根据用户查询对应的全部权限资源集合
     * @param user 查询的用户
     * @return 权限资源Map，key为资源类型，value为权限资源集合
     */
    Map<String, List<Resource>> findUserPermissions(User user);

    /**
     * 获取完整的权限树,将树中对应的权限勾选
     * @param navigationList 菜单集合
     * @param operationList 功能集合
     * @param rolePermissions 角色的权限集合
     * @return 角色的权限树
     */
    List<Navigation> generatorPermissionTree(List navigationList, List operationList, List<Permission> rolePermissions);

    /**
     * 根据角色查询对应的权限集合
     * @param guid 角色guid
     * @return 权限集合
     */
    List<Permission> findPermissionListByRoleGuid(String guid);

    /**
     * 根据功能代码查询对应分类的功能集合
     * @param operations 功能代码
     * @return
     */
    List<Permission> findPermissionListByOperationCode(List<Operation> operations);

    /**
     * 如果为默认角色，不能更改，要将disabled设为true
     * @param permissions 权限集合
     */
    void disabledPermissionTree(List<Navigation> permissions);
}
