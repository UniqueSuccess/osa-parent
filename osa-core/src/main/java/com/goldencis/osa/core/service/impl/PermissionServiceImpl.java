package com.goldencis.osa.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.core.entity.*;
import com.goldencis.osa.core.mapper.PermissionMapper;
import com.goldencis.osa.core.mapper.RolePermissionMapper;
import com.goldencis.osa.core.service.IPermissionService;
import com.goldencis.osa.core.utils.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 访问资源权限表 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    private Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);
    private static final String CACHE_PERMISSION = "permissionCache";
    @Autowired
    private PermissionMapper permissionMapper;

    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private Map<Integer, BaseMapper<? extends Resource>> resourceMapperMap;

    private static final String PERMISSION_SEPARATOR = "::";
    private static final String PERMISSION_SEARCH_SEPARATOR = "|";

    @Override
    public Resource findResourceByResourceTypeAndId(Integer resourceType, Integer resourceId) {
        Resource resource = this.getResourceMapper(resourceType).selectById(resourceId);
        return resource;
    }

    @Override
    public List<? extends Resource> findResourceListByResourceType(Integer resourceType) {
        List<? extends Resource> resourceList = this.getResourceMapper(resourceType).selectList(new QueryWrapper<>());
        return resourceList;
    }

    @Override
    public List<? extends Resource> findVisibleResourceListByResourceType(Integer resourceType) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("visible", true);
        List<? extends Resource> resourceList = this.getResourceMapper(resourceType).selectByMap(params);
        return resourceList;
    }

    @Override
    public List<? extends Resource> findGrantVisibleResourceListByResourceType(Integer resourceType) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("grant_visible", true);
        List<? extends Resource> resourceList = this.getResourceMapper(resourceType).selectByMap(params);
        return resourceList;
    }

    @Override
    public Resource findResourceByPermission(Permission permission) {
        return this.findResourceByResourceTypeAndId(permission.getResourceType(), permission.getResourceId());
    }

    @Override
    public Permission findPermissionById(Integer id) {
        Permission permission = permissionMapper.selectById(id);
        Resource resource = this.findResourceByPermission(permission);
        permission.setResource(resource);
        return permission;
    }

    @Override
//    @Cacheable(value = CACHE_PERMISSION, key = "T(String).valueOf(#user.guid).concat('-').concat(#resourceType)")
    public List<Resource> findUserPermissionsByResourceType(User user, Integer resourceType) {
        //根据用户和资源类型查询权限集合
        List<Permission> permissionList = permissionMapper.findUserPermissionsByResourceType(user, resourceType);
        List<Resource> resourceList = null;
        if (!ListUtils.isEmpty(permissionList)) {
            List<Integer> resourceIdList = permissionList.stream().map(Permission::getResourceId).collect(Collectors.toList());
            //根据资源的类型获取对应Mapper
            BaseMapper resourceMapper = this.getResourceMapper(resourceType);
            //查询对应资源类型里，对应资源id集合的资源
            resourceList = resourceMapper.selectList(new QueryWrapper<Resource>().in("id", resourceIdList));
        }

        return resourceList;
    }

    @Override
    public Map<String, List<Resource>> findUserPermissions(User user) {
        Map<String, List<Resource>> resourceMap = new HashMap<>();
        List<ResourceType> resourceTypeList = ResourceType.getResourceTypeList();
        for (ResourceType resourceType : resourceTypeList) {
            resourceMap.put(resourceType.getName(), this.findUserPermissionsByResourceType(user, resourceType.getValue()));
        }
        return resourceMap;
    }

    @Override
    public List<Permission> findPermissionListByRoleGuid(String guid) {
        List<Permission> list = permissionMapper.findPermissionListByRoleGuid(guid);
        return list;
    }

    @Override
    public List<Permission> findPermissionListByOperationCode(List<Operation> operations) {
        if (!ListUtils.isEmpty(operations)) {
            StringBuffer search = new StringBuffer();
            search.append("^");
            for (Operation operation : operations) {
                search.append(operation.getCode() + PERMISSION_SEPARATOR + PERMISSION_SEARCH_SEPARATOR);
            }
            search.deleteCharAt(search.length() - 1);
            System.out.println(search.toString());

            return permissionMapper.findPermissionListByOperationCode(search.toString());
        }
        return null;
    }

    @Override
    public void disabledPermissionTree(List<Navigation> permissions) {
        if (!ListUtils.isEmpty(permissions)) {
            for (Navigation permission : permissions) {
                permission.setDisabled(true);
            }
        }
    }

    @Override
    public List<Navigation> generatorPermissionTree(List navigationList, List operationList, List<Permission> rolePermissions) {
        //存放页签的集合和Map
        List<Navigation> navigations = new ArrayList<>();
        Map<Integer, Navigation> navigationMap = new HashMap<>();
        //存放功能的集合和Map
        List<Operation> operations = new ArrayList<>();
        Map<Integer, Operation> operationMap = new HashMap<>();

        for (Object obj : navigationList) {
            //区分实际的权限为页签还是功能，将其归纳进对应的集合和Map中
            this.sistinguishPermissionToListAndMap(obj, navigations, navigationMap, operations, operationMap);
        }

        for (Object obj : operationList) {
            //区分实际的权限为页签还是功能，将其归纳进对应的集合和Map中
            this.sistinguishPermissionToListAndMap(obj, navigations, navigationMap, operations, operationMap);
        }

        //构建完整的权限树
        //首选构建菜单树
        List<Navigation> permissionTree = this.generatorNavigationTree(navigations, navigationMap);

        //为菜单树增加功能节点
        this.addOperation4PermissionTree(permissionTree, operations, navigationMap);

        //为回显的权限勾选
        this.checkRolePermission4PermissionTree(permissionTree, rolePermissions, navigationMap, operationMap);
        return permissionTree;
    }

    private List<Navigation> generatorNavigationTree(List<Navigation> navigations, Map<Integer, Navigation> navigationMap) {
        List<Navigation> permissionTree = new ArrayList<>();

        for (Navigation navigation : navigations) {
            if (ConstantsDto.LEVEL_ONE.equals(navigation.getLevel())) {
                permissionTree.add(navigation);
            } else {
                if (!navigationMap.containsKey(navigation.getParentId())) continue;

                Navigation parent = navigationMap.get(navigation.getParentId());
                if (parent.getSub() == null) {
                    parent.setSub(new ArrayList<>());
                }
                parent.getSub().add(navigation);
            }
        }

        return permissionTree;
    }

    private void addOperation4PermissionTree(List<Navigation> permissionTree, List<Operation> operations, Map<Integer, Navigation> navigationMap) {
        for (Operation operation : operations) {
            Integer navigationId = operation.getNavigationId();
            if (navigationId != null && navigationId != 0) {
                if (!navigationMap.containsKey(navigationId)) continue;

                Navigation navigation = navigationMap.get(navigationId);
                if (navigation.getSub() == null) {
                    navigation.setSub(new ArrayList<>());
                }
                navigation.getSub().add(operation);
            }
        }
    }

    private void checkRolePermission4PermissionTree(List<Navigation> permissionTree, List<Permission> rolePermissions, Map<Integer, Navigation> navigationMap, Map<Integer, Operation> operationMap) {
        if (!ListUtils.isEmpty(rolePermissions)) {
            for (Permission rolePermission : rolePermissions) {
                if (ResourceType.NAVIGATION.getValue().equals(rolePermission.getResourceType())) {
                    if (!navigationMap.containsKey(rolePermission.getResourceId())) continue;
                    navigationMap.get(rolePermission.getResourceId()).setChecked(true);
                } else if (ResourceType.OPERATION.getValue().equals(rolePermission.getResourceType())) {
                    if (!operationMap.containsKey(rolePermission.getResourceId())) continue;
                    operationMap.get(rolePermission.getResourceId()).setChecked(true);
                }
            }
        }
    }

    /**
     * 区分实际的权限为页签还是功能，将其归纳进对应的集合和Map中
     *
     * @param obj           带区分的权限
     * @param navigations   页签的集合
     * @param navigationMap 页签的Map
     * @param operationList 功能的集合
     * @param operationMap  功能的Map
     */
    private void sistinguishPermissionToListAndMap(Object obj, List<Navigation> navigations, Map<Integer, Navigation> navigationMap, List<Operation> operationList, Map<Integer, Operation> operationMap) {
        if (obj instanceof Navigation) {
            Navigation navigation = (Navigation) obj;
            navigations.add(navigation);
            navigationMap.put(navigation.getId(), navigation);
        } else if (obj instanceof Operation) {
            Operation operation = (Operation) obj;
            operationList.add(operation);
            operationMap.put(operation.getId(), operation);
        }
    }

    /**
     * 根据资源类型，获取对应的mapper
     *
     * @param resourceType 资源类型
     * @return 对应的mapper
     */
    protected BaseMapper<? extends Resource> getResourceMapper(Integer resourceType) {
        return resourceMapperMap.get(resourceType);
    }
}
