package com.goldencis.osa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.Assetgroup;
import com.goldencis.osa.asset.mapper.AssetMapper;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.IAssetgroupService;
import com.goldencis.osa.asset.util.AssetConstans;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultTree;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.core.entity.Navigation;
import com.goldencis.osa.core.entity.Resource;
import com.goldencis.osa.core.entity.Role;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.IDictionaryService;
import com.goldencis.osa.core.service.IPermissionService;
import com.goldencis.osa.core.service.IRoleService;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.core.utils.ResourceType;
import com.goldencis.osa.system.domain.SystemAccountInfo;
import com.goldencis.osa.system.entity.AuditorOperator;
import com.goldencis.osa.system.entity.UserAssetAssetgroup;
import com.goldencis.osa.system.mapper.UserAssetAssetgroupMapper;
import com.goldencis.osa.system.service.IAuditorOperatorService;
import com.goldencis.osa.system.service.IUserAssetAssetgroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 用户、设备/设备组 关联表（操作员管理设备/设备组）  服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-12-07
 */
@Service
public class UserAssetAssetgroupServiceImpl extends ServiceImpl<UserAssetAssetgroupMapper, UserAssetAssetgroup> implements IUserAssetAssetgroupService {

    /**
     * 授权类型:设备
     */
    private static final int PERMISSION_ASSET = 1;
    /**
     * 授权类型:设备组
     */
    private static final int PERMISSION_ASSET_GROUP = 2;

    @Autowired
    IUserService userService;

    @Autowired
    UserAssetAssetgroupMapper userAssetAssetgroupMapper;

    @Autowired
    IAssetgroupService assetgroupService;

    @Autowired
    IAssetService assetService;
    @Autowired
    AssetMapper assetMapper;
    @Autowired
    private IPermissionService permissionService;
    @Autowired
    private IAuditorOperatorService auditorOperatorService;


    @Autowired
    IDictionaryService dictionaryService;

    @Autowired
    IRoleService roleService;

    @Override
    public void saveAssetPermission(String userGuid, List<Integer> list) {
        User user = userService.getById(userGuid);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("用户不存在");
        }
        list.stream()
                // 先判断设备是否存在
                .filter(id -> Objects.nonNull(assetService.getById(id)))
                // 判断是否存在相同的授权信息
                .filter(id -> Objects.isNull(getOne(new QueryWrapper<UserAssetAssetgroup>()
                        .eq("type", PERMISSION_ASSET)
                        .eq("asset_id", id)
                        .eq("user_guid", userGuid))))
                .map(id -> new UserAssetAssetgroup(userGuid, PERMISSION_ASSET, id, null))
                .forEach(this::save);
    }

    @Override
    public void saveAssetGroupPermission(String userGuid, List<Integer> list) {
        User user = userService.getById(userGuid);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("用户不存在");
        }
        list.stream()
                .filter(id -> Objects.nonNull(assetgroupService.getById(id)))
                .filter(id -> Objects.isNull(getOne(new QueryWrapper<UserAssetAssetgroup>()
                        .eq("type", PERMISSION_ASSET_GROUP)
                        .eq("assetgroup_id", id)
                        .eq("user_guid", userGuid))))
                .map(id -> new UserAssetAssetgroup(userGuid, PERMISSION_ASSET_GROUP, null, id))
                .forEach(this::save);
    }

    @Override
    public void saveAuditPermission(String userGuid, List<String> list) {
        User user = userService.getById(userGuid);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("用户不存在");
        }
        list.stream()
                .filter(id -> Objects.nonNull(userService.getById(id)))
                .filter(id -> Objects.isNull(auditorOperatorService.getOne(new QueryWrapper<AuditorOperator>()
                        .eq("auditor_guid", userGuid)
                        .eq("operator_guid", id))))
                .map(id -> new AuditorOperator(userGuid, id))
                .forEach(auditorOperatorService::save);
    }

    @Override
    public void deleteAssetPermission(String userGuid, List<Integer> list) {
        User user = userService.getById(userGuid);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("用户不存在");
        }
        list.forEach(item -> {
            this.remove(new QueryWrapper<UserAssetAssetgroup>().eq("user_guid", userGuid).eq("asset_id", item));
        });
    }

    @Override
    public void deleteAssetGroupPermission(String userGuid, List<Integer> list) {
        User user = userService.getById(userGuid);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("用户不存在");
        }
        list.forEach(item -> {
            this.remove(new QueryWrapper<UserAssetAssetgroup>().eq("user_guid", userGuid).eq("assetgroup_id", item));
        });
    }

    @Override
    public void deleteAuditPermission(String userGuid, List<String> list) {
        User user = userService.getById(userGuid);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("用户不存在");
        }
        list.forEach(item -> auditorOperatorService.remove(new QueryWrapper<AuditorOperator>().eq("auditor_guid", userGuid).eq("operator_guid", item)));
    }

    /**
     * 根据用户guid获取选中的设备组
     *
     * @param userGuid
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ResultTree> getAssetgroupListTreeByUserGuid(String userGuid) {

        //罗列选中内容
        List<UserAssetAssetgroup> userAssetgroups = new ArrayList<>();

        //用户guid不为空，判断 用户存不存在
        if (!StringUtils.isEmpty(userGuid)) {
            if (Objects.isNull(userService.findUserByGuid(userGuid))) {
                throw new IllegalArgumentException("操作员不存在");
            }
            //罗列选中内容
            userAssetgroups = userAssetAssetgroupMapper.selectList(new QueryWrapper<UserAssetAssetgroup>().eq("user_guid", userGuid).eq("type", ConstantsDto.OPERATOR_TYPE_ASSETGROUP));
        }

        //罗列设备组
        List<Assetgroup> list = assetgroupService.list(null);
        List<ResultTree> collect = new ArrayList<>(list.size());
        if (!ListUtils.isEmpty(list)) {
            List<UserAssetAssetgroup> finalUserAssetgroups = userAssetgroups;
            collect = list.stream().map(item -> {
                ResultTree resultTree = item.formatTree();
                if (Objects.isNull(resultTree.getPid())) {
                    resultTree.setExpand(true);
                }
                if (!ListUtils.isEmpty(finalUserAssetgroups)) {
                    if (finalUserAssetgroups.stream().anyMatch(userAssetgroup -> userAssetgroup.getAssetgroupId() == item.getId())) {
                        resultTree.setChecked(true);
                        resultTree.setDisabled(true);
                        resultTree.setIgnore(true);
                    }
                }
                return resultTree;
            }).collect(Collectors.toList());
        }

        List<ResultTree> nihilityList = initNihilityList(collect);
        collect.addAll(nihilityList);
        return collect;
    }

    /**
     * 初始化虚拟组
     * @param collect
     * @return
     */
    private List<ResultTree> initNihilityList(List<ResultTree> collect) {
        List<ResultTree> nihilityList = new ArrayList<>();
        for (ResultTree tree : collect) {
            if (tree.getId().endsWith(ConstantsDto.NIHILITY)) {
                continue;
            }
            boolean hasChild = false;
            for (int i = collect.size() - 1; i >= 0; i--) {
                ResultTree item = collect.get(i);
                if (tree.getId().equals(item.getPid())) {
                    hasChild = true;
                    break;
                }
            }
            if (!hasChild) {
                continue;
            }
            String id = tree.getId() + ConstantsDto.NIHILITY;
            String name = ConstantsDto.NAME_NIHILITY;
            int level = tree.getLevel() + 1;
            ResultTree nihility = new ResultTree(id, name, level, tree.getId(), tree.isChecked(), true, false);
            nihilityList.add(nihility);
        }
        return nihilityList;
    }

    @Override
    public List<ResultTree> getAssetListTreeByUserGuid(String userGuid) {
        //罗列选中内容
        List<UserAssetAssetgroup> userAssetAssetgroups = new ArrayList<>();

        //用户guid不为空，判断 用户存不存在
        if (!StringUtils.isEmpty(userGuid)) {
            if (Objects.isNull(userService.findUserByGuid(userGuid))) {
                throw new IllegalArgumentException("操作员不存在");
            }
            //罗列选中内容
            userAssetAssetgroups = userAssetAssetgroupMapper.selectList(new QueryWrapper<UserAssetAssetgroup>().eq("user_guid", userGuid).eq("type", ConstantsDto.OPERATOR_TYPE_ASSET));
        }

        //获取设备集合（过滤除 应用程序发布器之外的设备）
        List<Asset> assetList = assetService.listAssetsNotPublis();
        List<Assetgroup> groupList = assetgroupService.list(null);

        //将设备组、设备拼接为结构树
        List<ResultTree> trees = assetService.formatTreeWithAccountAndAssetAndGroup(groupList, assetList);

        if (!ListUtils.isEmpty(trees)) {
            if (!ListUtils.isEmpty(userAssetAssetgroups)) {
                List<UserAssetAssetgroup> finalUserAssets = userAssetAssetgroups;
                trees.stream().forEach(resultTree -> {
                    if (finalUserAssets.stream().anyMatch(userAssetAssetgroup -> resultTree.getId().equals(AssetConstans.PREFIX_ASSET + userAssetAssetgroup.getAssetId()))) {
                        resultTree.setChecked(true);
                        resultTree.setDisabled(true);
                        resultTree.setIgnore(true);
                    }
                });
            }
        }
        return trees;
    }

    @Override
    public List<UserAssetAssetgroup> getAssetgroupListByUserGuid(String userGuid) {
        List<UserAssetAssetgroup> assetAssetgroupList = userAssetAssetgroupMapper.selectList(new QueryWrapper<UserAssetAssetgroup>().eq("user_guid", userGuid).eq("type", ConstantsDto.OPERATOR_TYPE_ASSETGROUP));
        if (!ListUtils.isEmpty(assetAssetgroupList)) {
            //为设备组添加添加所属组名称
            assetAssetgroupList.stream().forEach(userAssetAssetgroup -> {
                //设置 设备组名字
                userAssetAssetgroup.setAssetgroupName(assetgroupService.getById(userAssetAssetgroup.getAssetgroupId()).getName());
                //设置 设备数量
                userAssetAssetgroup.setAssetgroupAssetCount(assetgroupService.countAssetInGroups(userAssetAssetgroup.getAssetgroupId()));
                //设置 父级设备组名字
                userAssetAssetgroup.setAssetgroupPName(assetgroupService.getPnameByAssetgroupId(userAssetAssetgroup.getAssetgroupId()));
            });
        }
        return assetAssetgroupList;
    }


    /**
     * 根据操作员guid 获取设备组列表
     *
     * @return 设备组列表
     */
    @Override
    @Transactional(readOnly = true)
    public IPage<UserAssetAssetgroup> getAssetgroupByUserGuidInPage(Map<String, String> params) {
        Page page = new Page();
        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);
        if (Objects.isNull(paramMap.get("userGuid"))) {
            throw new IllegalArgumentException("操作员为空");
        }
        String userGuid = String.valueOf(paramMap.get("userGuid"));
        if (Objects.isNull(userService.findUserByGuid(userGuid))) {
            throw new IllegalArgumentException("操作员不存在");
        }

        paramMap.put("type", ConstantsDto.OPERATOR_TYPE_ASSETGROUP);

        //统计日志总数量
        int total = userAssetAssetgroupMapper.countAssetgroupByUserGuidInPage(paramMap);
        //带参数的分页查询
        List<UserAssetAssetgroup> userAssetAssetgroups = userAssetAssetgroupMapper.getAssetgroupByUserGuidInPage(paramMap);

        if (!ListUtils.isEmpty(userAssetAssetgroups)) {
            //为设备组添加添加所属组名称
            userAssetAssetgroups.stream().forEach(userAssetAssetgroup -> {
                if (!Objects.isNull(userAssetAssetgroup.getAssetgroupId())) {
                    //设置 设备数量
                    userAssetAssetgroup.setAssetgroupAssetCount(assetgroupService.countAssetInGroups(userAssetAssetgroup.getAssetgroupId()));
                    //设置 父级设备组名字
                    userAssetAssetgroup.setAssetgroupPName(assetgroupService.getPnameByAssetgroupId(userAssetAssetgroup.getAssetgroupId()));
                }
            });
        }
        page.setTotal(total);
        page.setRecords(userAssetAssetgroups);
        return page;
    }

    /**
     * 获取设备分页列表
     *
     * @param params
     * @return
     */
    @Override
    public IPage<UserAssetAssetgroup> getAssetByUserGuidInPage(Map<String, String> params) {
        Page page = new Page();
        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);
        if (Objects.isNull(paramMap.get("userGuid"))) {
            throw new IllegalArgumentException("操作员为空");
        }
        String userGuid = String.valueOf(paramMap.get("userGuid"));
        if (Objects.isNull(userService.findUserByGuid(userGuid))) {
            throw new IllegalArgumentException("操作员不存在");
        }

        paramMap.put("type", ConstantsDto.OPERATOR_TYPE_ASSET);

        //总数量
        int total = userAssetAssetgroupMapper.countAssetByUserGuidInPage(paramMap);
        //带参数的分页查询
        List<UserAssetAssetgroup> userAssetAssetgroups = userAssetAssetgroupMapper.getAssetByUserGuidInPage(paramMap);
        page.setTotal(total);
        page.setRecords(userAssetAssetgroups);
        return page;
    }

    /**
     * 获取所有系统管理员
     */
    @Override
    public List<User> getSystemUsers(String searchStr) {
        Map<String, Object> paramMap = new HashMap<>();
        //增加角色限定
        Integer adminType = dictionaryService.getRoleType(ConstantsDto.TYPE_ADMIN);
        Integer customType = dictionaryService.getRoleType(ConstantsDto.TYPE_CUSTOM);
        List<Role> roleList = roleService.list(new QueryWrapper<Role>().in("type", adminType, customType));

        if (!ListUtils.isEmpty(roleList)) {
            paramMap.put("roleList", roleList.stream().map(Role::getGuid).collect(Collectors.toList()));
        }
        paramMap.put("searchStr", searchStr);

        //带参数的分页查询
        List<User> userList = userAssetAssetgroupMapper.getSystemUsers(paramMap);
        return userList;
    }

    @Override
    public SystemAccountInfo getDetailByUserGuid(@NotNull String id) {
        // 查询用户
        User user = userService.getById(id);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("用户不存在: " + id);
        }
        SystemAccountInfo info = new SystemAccountInfo();
        // 查询用户角色类型
        List<Resource> list = permissionService.findUserPermissionsByResourceType(user, ResourceType.NAVIGATION.getValue());
        if (!ListUtils.isEmpty(list)) {
            list.stream()
                    .map(resource -> {
                        if (resource instanceof Navigation) {
                            return (Navigation) resource;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .forEach(item -> {
                        if (item.getId() == 3) {
                            info.setAssetPermission(new SystemAccountInfo.AssetPermission());
                        }
                        if (item.getId() == 5) {
                            info.setAuditPermission(new SystemAccountInfo.AuditPermission());
                        }
                    });
        }
        return info;
    }

    /**
     * 审计权限
     *
     * @param id
     * @return
     */
    @Override
    public List<SystemAccountInfo.AuditPermission.UserItem> getAuditPermissionByUserId(String id, boolean checkedOnly) {
        // 查询出所有拥有设备页签权限的用户
        List<User> userList = userService.getUserByPermission(ResourceType.NAVIGATION.getValue(), 3);
        List<AuditorOperator> list = auditorOperatorService.list(new QueryWrapper<AuditorOperator>().eq("auditor_guid", id));
        Stream<SystemAccountInfo.AuditPermission.UserItem> stream = userList.stream().map(user -> {
            boolean checked = false;
            for (AuditorOperator operator : list) {
                if (operator.getOperatorGuid().equals(user.getGuid())) {
                    checked = true;
                    break;
                }
            }
            return new SystemAccountInfo.AuditPermission.UserItem(user, checked);
        });
        if (checkedOnly) {
            return stream.filter(SystemAccountInfo.AuditPermission.UserItem::getChecked).collect(Collectors.toList());
        } else {
            return stream.collect(Collectors.toList());
        }
    }

    @Override
    public List<SystemAccountInfo.AssetPermission.AssetGroupItem> getAssetGroupPermissionByUserId(String id) {
        List<UserAssetAssetgroup> assetgroupList = this.getAssetgroupListByUserGuid(id);
        return assetgroupList.stream().map(group -> {
            SystemAccountInfo.AssetPermission.AssetGroupItem item = new SystemAccountInfo.AssetPermission.AssetGroupItem();
            item.setId(group.getAssetgroupId());
            item.setName(group.getAssetgroupName());
            item.setPName(group.getAssetgroupPName());
            item.setAssetCount(group.getAssetgroupAssetCount());
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SystemAccountInfo.AssetPermission.AssetItem> getAssetPermissionByUserId(String id) {
        List<UserAssetAssetgroup> list = this.list(new QueryWrapper<UserAssetAssetgroup>().eq("user_guid", id).eq("type", ConstantsDto.OPERATOR_TYPE_ASSET));
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        return list.stream()
                .filter(userAssetAssetgroup -> Objects.nonNull(userAssetAssetgroup.getAssetId()))
                .map(userAssetAssetgroup -> {
                    Integer assetId = userAssetAssetgroup.getAssetId();
                    Asset asset = assetMapper.getAssetDetailById(assetId);
                    return new SystemAccountInfo.AssetPermission.AssetItem(asset);
                }).collect(Collectors.toList());
    }

}
