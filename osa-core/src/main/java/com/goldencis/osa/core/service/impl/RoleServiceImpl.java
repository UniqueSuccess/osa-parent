package com.goldencis.osa.core.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.core.entity.Dictionary;
import com.goldencis.osa.core.entity.*;
import com.goldencis.osa.core.mapper.RoleMapper;
import com.goldencis.osa.core.mapper.RolePermissionMapper;
import com.goldencis.osa.core.service.IDictionaryService;
import com.goldencis.osa.core.service.IOperationService;
import com.goldencis.osa.core.service.IPermissionService;
import com.goldencis.osa.core.service.IRoleService;
import com.goldencis.osa.core.utils.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private IOperationService operationService;

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private IDictionaryService dictionaryService;

    private static final String PERMISSION_SEPARATOR = "::";
    private static final String PERMISSION_CODE_TYPE = "select";
    private static final String DEFAULT_ROLE_PERMISSION = "DEFAULT_ROLE_PERMISSION";

    @Override
    @Transactional(readOnly = true)
    public Role findRoleByGuid(String guid) {
        Role role = roleMapper.selectById(guid);
        return role;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void saveRole(Role role) {
        verifyRoleData(role);
        if (org.springframework.util.StringUtils.isEmpty(role.getName())) {
            throw new IllegalArgumentException("角色名称不能为空");
        }
        role.setName(role.getName().trim());
        List<Role> list = this.list(new QueryWrapper<Role>().eq("name", role.getName()));
        if (!CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("角色名称重复");
        }
        String guid = UUID.randomUUID().toString();
        role.setGuid(guid);
        role.setCreateTime(LocalDateTime.now());
        role.setType(ConstantsDto.ROLE_TYPE_CUSTOM);

        //授予角色默认权限，如登录权限(logonsuccess)，获取菜单和权限的权限
        List<Integer> permissionIds = dictionaryService.getDictionaryListByType(DEFAULT_ROLE_PERMISSION).stream().map(Dictionary::getValue).collect(Collectors.toList());
        if (!ListUtils.isEmpty(permissionIds)) {
            for (Integer permissionId : permissionIds) {
                rolePermissionMapper.insert(new RolePermission(guid, permissionId));
            }
        }
        roleMapper.insert(role);
    }



    @Override
    @Transactional(readOnly = true)
    public List<Role> getRoleListByUserguid(String guid) {
        List<Role> roleList = roleMapper.getRoleListByUserguid(guid);
        return roleList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getRoleListByParams(Map<String, String> params) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        QueryUtils.setQeryConditionByParamsMap(wrapper, params, "name");

        List<Role> roleList = roleMapper.selectList(wrapper);
        return roleList;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkRoleNameDuplicate(Role role) {
        Role preRole = this.findRoleByName(role.getName());

        //判断数据库是否有该记录，不存在即可用，返回true，如果有继续判断
        if (preRole != null) {
            //比较两个对象的id，若一致，是同一个对象没有改变名称的情况，返回可用true。
            if (preRole.getGuid().equals(role.getGuid())) {
                return true;
            }
            //若果不同，说明为两个角色，名称重复，不可用，返回false
            return false;
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Role findRoleByName(String name) {
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("name", name));
        return role;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void updateRole(Role role) {
        verifyRoleData(role);
        this.updateById(role);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void updateRolePermissions(String guid, JSONArray list) {
        Set<RolePermission> rolePermissions = new HashSet<>();
        List<Integer> navigationIds = new ArrayList<>();
        List<Integer> operationIds = new ArrayList<>();

        //解析权限的json数组
        this.parsePermissionJsonArray(list, navigationIds, operationIds);

        if (!ListUtils.isEmpty(navigationIds)) {
            //获取页签权限集合
            List<Permission> navigationPers = permissionService.list(new QueryWrapper<Permission>().eq("resource_type", ConstantsDto.PERMISSION_TYPE_NAVIGATION).in("resource_id", navigationIds));
            //将权限添加到关联集合中
            this.add4RolePermissions(guid, navigationPers, rolePermissions);

            //获取页签权限对应的查询功能的集合
            List<Permission> selectPers = this.getSelectPermission4Navigation(navigationIds);
            //将权限添加到关联集合中
            this.add4RolePermissions(guid, selectPers, rolePermissions);
        }

        if (!ListUtils.isEmpty(operationIds)) {
            //获取授权时可见的功能权限集合
            List<Permission> grantedOperPers = permissionService.list(new QueryWrapper<Permission>().eq("resource_type", ConstantsDto.PERMISSION_TYPE_OPERATION).in("resource_id", operationIds));
            this.add4RolePermissions(guid, grantedOperPers, rolePermissions);

            //根据授权时可见的功能权限集合，找出对应的真实功能权限的集合
            List<Operation> operations = operationService.list(new QueryWrapper<Operation>().in("id", operationIds));
            //根据功能代码查询对应分类的功能集合
            List<Permission> realOperPers = permissionService.findPermissionListByOperationCode(operations);
            this.add4RolePermissions(guid, realOperPers, rolePermissions);
        }

        rolePermissionMapper.delete(new QueryWrapper<RolePermission>().eq("role_guid", guid));

        //授予角色默认权限，如登录权限(logonsuccess)，获取菜单和权限的权限
        List<Integer> permissionIds = dictionaryService.getDictionaryListByType(DEFAULT_ROLE_PERMISSION).stream().map(Dictionary::getValue).collect(Collectors.toList());
        if (!ListUtils.isEmpty(permissionIds)) {
            for (Integer permissionId : permissionIds) {
                rolePermissions.add(new RolePermission(guid, permissionId));
            }
        }
        //插入对应的权限关联
        if (rolePermissions != null && rolePermissions.size() > 0) {
            for (RolePermission rolePermission : rolePermissions) {
                rolePermissionMapper.insert(rolePermission);
            }
        }
    }

    @Override
    public String deleteRole(Role role) {
        rolePermissionMapper.delete(new QueryWrapper<RolePermission>().eq("role_guid", role.getGuid()));
        roleMapper.deleteById(role.getGuid());
        return role.getName();
    }

    @Override
    public List<Role> getRoleListExcludeNormal(boolean excludeAdmin) {
        return baseMapper.getRoleListExcludeNormal(excludeAdmin);
    }

    private void add4RolePermissions(String guid, List<Permission> permissionList, Set<RolePermission> rolePermissions) {
        if (!ListUtils.isEmpty(permissionList)) {
            for (Permission permission : permissionList) {
                rolePermissions.add(new RolePermission(guid, permission.getId()));
            }
        }
    }

    private List<Permission> getSelectPermission4Navigation(List<Integer> navigationIds) {
        List<Operation> operations = operationService.list(new QueryWrapper<Operation>().in("navigation_id", navigationIds).like("code" , "%" + PERMISSION_SEPARATOR + PERMISSION_CODE_TYPE));

        if (!ListUtils.isEmpty(operations)) {
            List<Permission> selectPermissions = permissionService.list(new QueryWrapper<Permission>().eq("resource_type", ConstantsDto.PERMISSION_TYPE_OPERATION).in("resource_id", operations.stream().map(Operation::getId).collect(Collectors.toList())));
            //根据功能代码查询对应分类的功能集合
            List<Permission> selectPermissionsChild = permissionService.findPermissionListByOperationCode(operations);
            if (!ListUtils.isEmpty(selectPermissions)) {
                selectPermissions.addAll(selectPermissionsChild);
            } else {
                selectPermissions = selectPermissionsChild;
            }
            return selectPermissions;
        }
        return null;
    }

    private void parsePermissionJsonArray(JSONArray list, List<Integer> navigationIds, List<Integer> operationIds) {
        //遍历授权的集合，将页签权限和功能权限区分
        for (int i = 0; i < list.size(); i++) {
            JSONObject item = list.getJSONObject(i);
            Integer type = item.getInteger("type");
            if (type == ConstantsDto.PERMISSION_TYPE_NAVIGATION) {
                navigationIds.add(item.getInteger("id"));
            } else if (type == ConstantsDto.PERMISSION_TYPE_OPERATION) {
                operationIds.add(item.getInteger("id"));
            } else {
                continue;
            }
        }
    }


    /**
     * 保存数据验证
     */
    void verifyRoleData(Role role) {
        if (! com.goldencis.osa.common.utils.StringUtils.isInLength(role.getName(),30)){
            throw new IllegalArgumentException("角色名称最大长度为30");
        }
    }
}
