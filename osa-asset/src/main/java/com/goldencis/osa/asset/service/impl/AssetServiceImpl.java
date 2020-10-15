package com.goldencis.osa.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.asset.domain.AssetCount;
import com.goldencis.osa.asset.entity.*;
import com.goldencis.osa.asset.mapper.AssetMapper;
import com.goldencis.osa.asset.params.AssetParams;
import com.goldencis.osa.asset.resource.AssetResource;
import com.goldencis.osa.asset.resource.AssetResourceDispatcher;
import com.goldencis.osa.asset.resource.domain.Bs;
import com.goldencis.osa.asset.service.*;
import com.goldencis.osa.asset.util.AssetConstans;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultTree;
import com.goldencis.osa.common.utils.DateUtil;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.core.entity.LogSystem;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.ILogSystemService;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.core.service.IUsergroupService;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.core.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-25
 */
@Service
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements IAssetService {

    private final Logger logger = LoggerFactory.getLogger(AssetServiceImpl.class);
    private final IAssetAccountService assetAccountService;
    private final IAssetAssetgroupService assetAssetgroupService;
    private final AssetResourceDispatcher assetResourceDispatcher;
    private final ISsoRuleService ssoRuleService;
    private final IGrantedService grantedService;
    private final IAssetDbService assetDbService;
    private final IAssetCsService assetCsService;
    private final IAssetBsService assetBsService;
    private final IAssetgroupService assetgroupService;
    private final IUserService userService;
    private final IUsergroupService usergroupService;

    @Autowired
    IAssetTypeService assetTypeService;
    @Autowired
    private ILogSystemService systemService;

    @Autowired
    public AssetServiceImpl(IAssetAccountService assetAccountService,
                            IAssetAssetgroupService assetAssetgroupService,
                            AssetResourceDispatcher assetResourceDispatcher,
                            ISsoRuleService ssoRuleService,
                            IGrantedService grantedService,
                            IAssetDbService assetDbService,
                            IAssetCsService assetCsService,
                            IAssetBsService assetBsService,
                            IAssetgroupService assetgroupService,
                            IUserService userService,
                            IUsergroupService usergroupService) {
        this.assetAccountService = assetAccountService;
        this.assetAssetgroupService = assetAssetgroupService;
        this.assetResourceDispatcher = assetResourceDispatcher;
        this.ssoRuleService = ssoRuleService;
        this.grantedService = grantedService;
        this.assetDbService = assetDbService;
        this.assetCsService = assetCsService;
        this.assetBsService = assetBsService;
        this.assetgroupService = assetgroupService;
        this.userService = userService;
        this.usergroupService = usergroupService;
    }


    @Override
    public void getAssetsInPage(IPage<Asset> page, AssetParams params) {
        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        // 转换查询条件
        formatQueryParams(params);
        // 获取用户设备权限,以及用户设备组权限
        User user = SecurityUtils.getCurrentUser();
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("获取登录用户失败");
        }
        String userId = user.getGuid();
        // 超级管理员与系统自带的操作员,可以查看所有设备
        if (!ConstantsDto.USER_SYSTEM_ID.equals(userId)
                && !ConstantsDto.USER_OPERATOR_ID.equals(userId)) {
            params.setPermissionAssetIdList(baseMapper.getAssetIdListByUserPermission(userId));
            params.setPermissionGroupIdList(baseMapper.getAssetGroupIdListByUserPermission(userId));
        }
        int total = baseMapper.countAssetsInPage(params);
        List<Asset> list = baseMapper.getAssetsInPage(params);
        list.forEach(item -> item.setGranted(hasGranted(item.getId(), null)));
        page.setTotal(total);
        page.setRecords(list);
    }

    /**
     * 格式化查询参数
     *
     * @param params
     * @return
     */
    private void formatQueryParams(AssetParams params) {
        // 设备组id
        String groupId = params.getGroupId();
        if (!StringUtils.isEmpty(groupId)) {
            /*
             * 2018年10月31日19:06:34修改:设备组以及用户组取消级联,不需要将下级组的id关联出来
             */
            List<Integer> list = Arrays.stream(groupId.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            params.setGroupIdList(list);
        }
        // 设备类型
        String assetType = params.getAssetType();
        if (!StringUtils.isEmpty(assetType)) {
            List<Integer> list = Arrays.stream(assetType.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            params.setAssetTypeList(list);
        }
        String assetIds = params.getAssetIds();
        if (!StringUtils.isEmpty(assetIds)) {
            List<Integer> list = null;
            try {
                list = Arrays.stream(assetIds.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
            }
            params.setAssetIdList(list);
        }
    }

    @Override
    public void deleteAssetById(Integer id) {
        deleteAssetById(id, null);
    }

    @Override
    public void deleteAssetById(Integer id, Callback callback) {
        Asset asset = baseMapper.selectById(id);
        if (asset == null) {
            throw new IllegalArgumentException("设备不存在,id : " + id);
        }
        // 在用的应用程序发布器,不可删除
        if (checkPublish(asset.getId())) {
            throw new IllegalArgumentException("在用的应用程序发布器不可删除");
        }
        // 存在授权的设备不允许删除
        // 这个地方存在一个问题,如果授权被拒绝,设备也不可删除
        if (hasGranted(id, null)) {
            grantedService.deleteGrantedAssetByAssetId(id);
            if (Objects.nonNull(callback)) {
                callback.result("提交审批成功!");
            }
            return;
        }
        realDeleteAssetById(id);
        if (Objects.nonNull(callback)) {
            callback.result("删除成功!");
        }
    }

    /**
     * 判断设备是否存在授权信息，
     *
     * @param assetId 设备id
     * @return 如果存在，返回true；否则，返回false
     */
    private boolean hasGranted(Integer assetId, Integer accountId) {
        QueryWrapper<Granted> wrapper = new QueryWrapper<>();
        if (Objects.nonNull(assetId)) {
            wrapper.eq("asset_id", assetId);
        }
        if (Objects.nonNull(accountId)) {
            wrapper.eq("account_id", accountId);
        }
        // 只查询没有被拒绝的授权记录
        wrapper.ne("status", -1);
        return Objects.nonNull(grantedService.getOne(wrapper));
    }

    /**
     * 检查应用程序发布器是否在用
     *
     * @param assetId 设备id
     * @return 如果设备是应用程序发布器, 并且在用, 返回true;否则,返回false;
     */
    @Override
    public boolean checkPublish(Integer assetId) {
        Objects.requireNonNull(assetId, "设备id不能为null");
        Asset asset = this.getById(assetId);
        if (Objects.isNull(asset) || Objects.isNull(asset.getIsPublish()) || !asset.getIsPublish().equals(1)) {
            return false;
        }
        // 如果从表中存在任意一条用该设备做应用程序发布器的数据,则返回true;
        return Objects.nonNull(assetCsService.getOne(new QueryWrapper<AssetCs>().eq("publish", assetId)))
                || Objects.nonNull(assetBsService.getOne(new QueryWrapper<AssetBs>().eq("publish", assetId)))
                || Objects.nonNull(assetDbService.getOne(new QueryWrapper<AssetDb>().eq("publish", assetId)));
    }


    @Override
    public String deleteAssetsByIdList(List<Integer> list) {
        for (Integer id : list) {
            try {
                deleteAssetById(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void realDeleteAssetById(Integer id) {
        realDeleteAssetById(id, true);
    }

    @Override
    public void realDeleteAssetById(@NotNull Integer id, boolean log) {
        if (Objects.isNull(id)) {
            return;
        }
        Asset asset = baseMapper.selectById(id);
        if (Objects.isNull(asset)) {
            return;
        }
        // 删除从表
        assetResourceDispatcher.deleteResourceByTypeAndAssetId(asset.getType(), id);
        // 删除设备设备组中间表
        assetAssetgroupService.remove(new QueryWrapper<AssetAssetgroup>().eq("asset_id", id));
        // 删除设备账号表中的记录
        assetAccountService.deleteAccountByAssetId(id);
        // 删除SSO(单点登录)从表信息
        ssoRuleService.remove(new QueryWrapper<SsoRule>().eq("asset_id", id));
        // 删除设备
        this.removeById(id);
        if (log) {
            saveSystemLog("设备管理-删除设备", "在 【设备管理】中 执行【删除设备】操作成功：【设备名称：" + asset.getName() + "】", "AssetController.delete(..) invoke");
        }
    }

    @Override
    public Asset getAssetDetailById(@NotNull Integer id) {
        Asset asset = baseMapper.getAssetDetailById(id);
        Objects.requireNonNull(asset, "设备不存在,id : " + id);
        AssetResource resource = assetResourceDispatcher.getResourceByTypeAndAssetId(asset.getType(), asset.getId());
        asset.setExtra(resource);
        asset.setSsoRules(ssoRuleService.getSSORuleListByAssetId(asset.getId()));
        asset.setAccounts(assetAccountService.list(new QueryWrapper<AssetAccount>().eq("asset_id", id)));
        return asset;
    }

    @Override
    public void saveOrUpdateAsset(Asset entity) {
        saveOrUpdateAsset(entity, null);
    }

    @Override
    public void saveOrUpdateAsset(Asset entity, Callback callback) {
        verifyAssetData(entity);
        if (!checkNameDuplicate(entity)) {
            throw new IllegalArgumentException("设备名称不可用");
        }
        if (!new Bs().typeId().equals(entity.getType()) && !checkIpDuplicate(entity)) {
            throw new IllegalArgumentException("IP地址不可用");
        }
        Integer id = entity.getId();
        if (Objects.isNull(id)) {
            id = saveAsset(entity);
            // 保存用户设备关联表
            User user = SecurityUtils.getCurrentUser();
            if (Objects.nonNull(user)) {
                baseMapper.insertUserAssetPermission(user.getGuid(), id);
            }
        } else {
            updateAsset(entity, callback);
        }
        // 更新设备设备组中间表
        updateAssetAssetGroup(id, entity.getGroupId());
    }

    /**
     * 保存设备信息
     *
     * @param entity
     */
    private Integer saveAsset(@NotNull Asset entity) {
        User user = SecurityUtils.getCurrentUser();
        String guid = user != null ? user.getGuid() : null;
        entity.setCreateBy(guid);
        entity.setCreateTime(LocalDateTime.now());
        baseMapper.insertAndGetPrimaryKey(entity);
        // 刷新从表信息
        assetResourceDispatcher.refreshResource(entity);
        // 保存账号信息
        saveAccountList(entity);
        // 更新SSO(单点登录)从表
        saveOrUpdateSSOTable(entity);
        // cs,bs,数据库设备,将应用程序发布器的账号复制过来
//        saveAccountForApp(entity);

        // 返回sql中生成的id
        return entity.getId();
    }

    /**
     * 更新设备信息
     *
     * @param entity
     */
    private void updateAsset(@NotNull Asset entity, Callback callback) {
        // 应用程序发布器标示为null或者不为1,并且存在将这个设备作为应用程序发布器的设备
        Integer isPublish = entity.getIsPublish();
        if ((Objects.isNull(isPublish)
                || !isPublish.equals(1))
                && checkPublish(entity.getId())) {
            throw new IllegalArgumentException("在用的应用程序发布器,不能变更为非应用程序发布器");
        }

        User user = SecurityUtils.getCurrentUser();
        String guid = user != null ? user.getGuid() : null;
        entity.setUpdateBy(guid);
        entity.setUpdateTime(LocalDateTime.now());
        this.updateById(entity);

        // 更新从表信息
        assetResourceDispatcher.refreshResource(entity);
        // 更新账号信息
        updateAccountList(entity, callback);
        // 更新SSO(单点登录)从表
        saveOrUpdateSSOTable(entity);

    }

    /**
     * 更新设备设备组中间表
     *
     * @param assetId      设备id
     * @param assetGroupId 设备组id
     */
    private void updateAssetAssetGroup(Integer assetId, Integer assetGroupId) {
        AssetAssetgroup assetAssetgroup = assetAssetgroupService.getOne(new QueryWrapper<AssetAssetgroup>().eq("asset_id", assetId));
        if (Objects.isNull(assetAssetgroup)) {
            assetAssetgroup = new AssetAssetgroup();
            assetAssetgroup.setAssetgroupId(Objects.requireNonNull(assetGroupId, "设备组id不能为null"));
            assetAssetgroup.setAssetId(assetId);
            assetAssetgroupService.save(assetAssetgroup);
        } else {
            if (!assetAssetgroup.getAssetgroupId().equals(assetGroupId)) {
                assetAssetgroup.setAssetgroupId(assetGroupId);
                assetAssetgroupService.update(assetAssetgroup, new QueryWrapper<AssetAssetgroup>().eq("asset_id", assetId));
            }
        }
    }

    @Override
    public List<Asset> listWithAssetAccount(List<Integer> assetIds) {
        List<Asset> assetList = baseMapper.listWithAssetAccount(assetIds);
        return assetList == null ? new ArrayList<>() : assetList;
    }

    @Override
    public List<Asset> listAssetsNotPublis() {
        List<Asset> assetList = baseMapper.listAssetsNotPublis();
        return assetList;
    }

    /**
     * 保存数据验证
     */
    void verifyAssetData(Asset asset) {
        if (!com.goldencis.osa.common.utils.StringUtils.isInLength(asset.getName(), 30)) {
            throw new IllegalArgumentException("设备名称最大长度为30");
        }
        if (!com.goldencis.osa.common.utils.StringUtils.isInLength(asset.getIp(), 30)) {
            throw new IllegalArgumentException("设备ip最大长度为30");
        }
        if (!com.goldencis.osa.common.utils.StringUtils.isInLength(asset.getRemark(), 200)) {
            throw new IllegalArgumentException("设备备注最大长度为200");
        }
    }

    //------设备授权相关--start---------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public List<ResultTree> formatTreeWithAccountAndAssetAndGroup(List<Assetgroup> groupList, List<Asset> assetList) {
        //创建多类型树
        List<ResultTree> trees = new ArrayList<>();

        //构建树，首先确认设备组是否存在。
        if (!ListUtils.isEmpty(groupList) || !ListUtils.isEmpty(assetList)) {
            //设备组集合转化为Map，id为key，设备组为value
            Map<Integer, Assetgroup> groupMap = ListUtils.isEmpty(groupList) ? new HashMap<>() : groupList.stream().collect(Collectors.toMap(Assetgroup::getId, assetgroup -> assetgroup));

            List<ResultTree> assetTrees = new ArrayList<>();
            //其次，确认设备是否存在
            if (!ListUtils.isEmpty(assetList)) {
                //设备集合转化为Map，id为key，设备为value
                Map<Integer, Asset> assetMap = assetList.stream().collect(Collectors.toMap(Asset::getId, asset -> asset));
                List<AssetAssetgroup> relationList = assetAssetgroupService.list(new QueryWrapper<AssetAssetgroup>().in("asset_id", assetMap.keySet()));

                //最后，确认设备和设备组关联是否存在
                if (!ListUtils.isEmpty(relationList)) {
                    //根据实际存在的关联遍历
                    relationList.stream().forEach(relation -> {
                        Assetgroup assetgroup = groupMap.get(relation.getAssetgroupId());
                        Asset asset = assetMap.get(relation.getAssetId());

                        if (asset != null) {
                            //如果设备对应的设备组不在Map中，补齐
                            if (assetgroup == null) {
                                assetgroup = assetgroupService.getById(relation.getAssetgroupId());
                                groupList.add(assetgroup);
                                groupMap.put(assetgroup.getId(), assetgroup);
                            }
                            this.formatTree(assetgroup, asset, assetTrees);
                        }
                    });
                }
            }

            //由于当前设备组不足以构建树，需要查找构建树的最小集合
            List<Assetgroup> parentList = this.findMinGroupListToBuildTree(groupList, groupMap);
            groupList.addAll(parentList);

            //添加设备组的节点
            List<ResultTree> groupTrees = groupList.stream().map(assetgroup -> {
                //设备组 不能被勾选( disabled: true 禁掉 checkbox  和 不能select) ，不能被选中(checked: true 是否勾选(如果勾选，子节点也会全部勾选))
                //顶级 父节点默认展开(是否展开子节点) expand ：true, 其余 父节点默认展开(是否展开子节点) expand ：false
                return new ResultTree(AssetConstans.PREFIX_GROUP + assetgroup.getId(), assetgroup.getName(), assetgroup.getLevel(), assetgroup.getPid() != null ? AssetConstans.PREFIX_GROUP + assetgroup.getPid() : null, AssetConstans.NODE_GROUP, "icon-file", false, false, assetgroup.getPid() == null);
            }).collect(Collectors.toList());

            //先将设备组树添加进来
            trees.addAll(groupTrees);
            if (!ListUtils.isEmpty(assetTrees)) {
                //再将设备的树添加进来，保证组在上，设备在下
                trees.addAll(assetTrees);
            }

        } else {
            //如果该用户没有任何组的权限，默认展示顶级设备组，但无任何操作权限。
            Assetgroup assetgroup = assetgroupService.getById(ConstantsDto.SUPER_GROUP);
            ResultTree defaultNode = new ResultTree(AssetConstans.PREFIX_GROUP + assetgroup.getId(), assetgroup.getName(), assetgroup.getLevel(), assetgroup.getPid() != null ? AssetConstans.PREFIX_GROUP + assetgroup.getPid() : null, AssetConstans.NODE_GROUP, "icon-file", false, true, assetgroup.getPid() == null);
            trees.add(defaultNode);
        }
        return trees;
    }

    /**
     * 找构建树的最小集合(补集)
     *
     * @param groupList 当前用户权限下的设备组集合
     * @param groupMap  设备组集合对应的Map
     * @return 构建完整树所需要添加的补集
     */
    private List<Assetgroup> findMinGroupListToBuildTree(List<Assetgroup> groupList, Map<Integer, Assetgroup> groupMap) {
        List<Assetgroup> parentList = new ArrayList<>();
        for (Assetgroup assetgroup : groupList) {
            this.addParentNodeInListAndMap(assetgroup, parentList, groupMap);
        }
        return parentList;
    }

    /**
     * 递归调用，查询指定设备组的父节点，是否在设备组Map中，没有的话查询放入List和Map，继续递归调用，直至顶级设备组
     *
     * @param assetgroup 指定设备组
     * @param parentList 待添加的父节点补集
     * @param groupMap   全部设备的Map
     */
    private void addParentNodeInListAndMap(Assetgroup assetgroup, List<Assetgroup> parentList, Map<Integer, Assetgroup> groupMap) {
        if (assetgroup.getLevel() != null && assetgroup.getLevel() != 0) {//排除顶级设备组
            if (!groupMap.containsKey(assetgroup.getPid())) {//当该组的父级组不在Map中时，需要将其父类查出，放到Map跟集合中，并递归调用当前方法查其父类
                Assetgroup parent = assetgroupService.getById(assetgroup.getPid());
                parentList.add(parent);
                groupMap.put(parent.getId(), parent);
                this.addParentNodeInListAndMap(parent, parentList, groupMap);
            }
        }
    }

    /**
     * 初始化虚拟组
     *
     * @param collect
     * @return
     */
    @Override
    public List<ResultTree> initNihilityList(List<ResultTree> collect) {
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

    /**
     * 根据操作员guid 设置操作员已选设备
     *
     * @param guid        用户或者用户组的guid
     * @param trees       设备组、设备、账号树
     * @param isGroup
     * @param isUserGroup
     */
    @Override
    public List<ResultTree> setSelectedByGuid(String guid, List<ResultTree> trees, boolean isGroup, boolean isUserGroup) {
        if (StringUtils.isEmpty(guid)) {
            throw new IllegalArgumentException("用户id为空");
        }
        if (Objects.isNull(userService.findUserByGuid(guid)) && Objects.isNull(usergroupService.getById(guid))) {
            throw new IllegalArgumentException("用户不存在");
        }

        //根据回显设备还是设备组，将ResultTree中账号或者设备组打为勾选并不可用状态
        if (!ListUtils.isEmpty(trees)) {
            List<String> ids = isGroup ? grantedService.findAssetgroupIdsByUserGuid(guid, isUserGroup) : grantedService.findAccountIdsByUserGuid(guid, isUserGroup);
            String prefix = isGroup ? AssetConstans.PREFIX_GROUP : AssetConstans.PREFIX_ACCOUNT;
            if (!ListUtils.isEmpty(ids)) {
                trees.stream().filter(resultTree -> ids.contains(resultTree.getId().replace(prefix, ""))).forEach(resultTree -> resultTree.lockCheckState(true));
            }
        }
        return trees;
    }

    /**
     * 根据操作员guid 设置设备权限
     *
     * @param trees 设备组、设备、设备账号树
     */
    @Override
    public List<ResultTree> setOperatorAssetByUserGuid(List<ResultTree> trees) {
        if (!ListUtils.isEmpty(trees)) {
            List<Integer> assetIds = baseMapper.findAssetIdsByUserGuid(ConstantsDto.OPERATOR_TYPE_ASSET, SecurityUtils.getCurrentUser().getGuid());
            trees.stream().forEach(resultTree -> {
                //1、过滤顶级节点
                assetIds.stream().filter(integer -> resultTree.getPid() != null).forEach(assetId -> {
                    //1 id 是asset的； 2、设备账号的pid 是asset的；
                    if (resultTree.getId().equals(AssetConstans.PREFIX_ASSET + assetId) || resultTree.getPid().equals(AssetConstans.PREFIX_ASSET + assetId)) {
                        resultTree.setDisabled(false);
                    }
                });
            });
        }
        return trees;
    }

    /**
     * 操作员的设备组树
     *
     * @param userGuid 操作员guid
     */
    @Override
    public List<ResultTree> getGrantedAssetgroupTreeByGuid(String userGuid) {
        if (StringUtils.isEmpty(userGuid)) {
            throw new IllegalArgumentException("用户id为空");
        }
        if (Objects.isNull(userService.findUserByGuid(userGuid))) {
            throw new IllegalArgumentException("用户不存在");
        }
        //罗列设备组
        List<Assetgroup> list = assetgroupService.list(null);

        String loginUserId = SecurityUtils.getCurrentUser().getGuid();
        //操作员权限
        List<Integer> assetgroupIds = baseMapper.findAssetIdsByUserGuid(ConstantsDto.OPERATOR_TYPE_ASSETGROUP, loginUserId);
        List<ResultTree> trees = new ArrayList<>(list.size());
        if (!ListUtils.isEmpty(list)) {
            trees = list.stream().map(item -> {
                ResultTree resultTree = item.formatTree();
                if (Objects.isNull(resultTree.getPid())) {
                    resultTree.setExpand(true);
                }
                if (!ListUtils.isEmpty(assetgroupIds)) {
                    // 1、操作员有设备组权限，设置disable:false； 操作员没有权限 设置disable:true；
                    if (assetgroupIds.stream().anyMatch(assetgroupId -> assetgroupId == item.getId())) {
                        resultTree.setDisabled(false);
                    } else {
                        resultTree.setDisabled(true);
                    }
                } else {
                    if (ConstantsDto.USER_SYSTEM_ID.equals(loginUserId) || ConstantsDto.USER_ADMIN_ID.equals(loginUserId)) {
                        resultTree.setDisabled(false);
                    } else {
                        resultTree.setDisabled(true);
                    }
                }
                return resultTree;
            }).collect(Collectors.toList());

            if (!ListUtils.isEmpty(trees)) {
                List<String> assetgroupGrantedsIds = grantedService.findAssetgroupIdsByUserGuid(userGuid, false);
                trees.stream().forEach(resultTree -> {
                    assetgroupGrantedsIds.stream().forEach(assetgroupId -> {
                        if (resultTree.getId().equals(String.valueOf(assetgroupId))) {
                            resultTree.setChecked(true);
                            resultTree.setDisabled(true);
                        }
                    });
                });
            }
        }
        return trees;
    }

    /**
     * 将设备保存入库,然后获取返回的id
     *
     * @param asset
     * @return
     */
    @Override
    public Integer saveAssetAndGetPrimaryKey(@NotNull Asset asset) {
        Integer id = asset.getId();
        if (Objects.isNull(id)) {
            baseMapper.insertAndGetPrimaryKey(asset);
        } else {
            baseMapper.updateById(asset);
        }
        return asset.getId();
    }

    @Override
    public List<AssetCount> infoForHomePage() {
        List<AssetCount> list = baseMapper.infoForHomePage();
        if (list.size() <= 5) {
            return list;
        }
        int count = 0;
        for (AssetCount item : list.subList(5, list.size())) {
            if (Objects.isNull(item.getCount())) {
                item.setCount(0);
            }
            count += item.getCount();
        }
        AssetCount assetCount = new AssetCount();
        assetCount.setTypeName("其他");
        assetCount.setCount(count);
        List<AssetCount> tobeReturn = list.subList(0, 5);
        tobeReturn.add(assetCount);
        return tobeReturn;

    }

    @Override
    public void findAssetsInGroupToList(List<Assetgroup> groupList, List<Asset> assetList) {
        //现将原有的设备集合转化为map
        Map<Integer, Asset> assetMap = ListUtils.isEmpty(assetList) ? new HashMap<>() : assetList.stream().collect(Collectors.toMap(Asset::getId, asset -> asset));

        List<Integer> additionIds = new ArrayList<>();
        //查找设备组下的设备，补齐设备节点
        for (Assetgroup assetgroup : groupList) {
            List<AssetAssetgroup> groupRelations = assetAssetgroupService.list(new QueryWrapper<AssetAssetgroup>().eq("assetgroup_id", assetgroup.getId()));
            if (!ListUtils.isEmpty(groupRelations)) {
                List<Integer> assetIds = groupRelations.stream().filter(assetAssetgroup -> !assetMap.containsKey(assetAssetgroup.getAssetId())).map(AssetAssetgroup::getAssetId).collect(Collectors.toList());
                additionIds.addAll(assetIds);
            }
        }

        if (!ListUtils.isEmpty(additionIds)) {
            List<Asset> assets = this.listWithAssetAccount(additionIds);
            assetList.addAll(assets);
        }
    }

    /**
     * 首页 -- 本周运维次数
     *
     * @return
     */
    @Override
    public List<HomeAssetsWeek> getHomeAssetsWeek() {
        List<HomeAssetsWeek> homeAssetsWeeks = new ArrayList<>();
        List<String> strDates = DateUtil.getPastDates(7);

        homeAssetsWeeks = strDates.stream().map(strDate -> {
            HomeAssetsWeek homeAssetsWeek = new HomeAssetsWeek(strDate, 0, 0, 0, 0, 0, 0);
            List<HomeAssets> homeAssets = baseMapper.getHomeAssetsByDay(strDate);
            homeAssets.stream().filter(Objects::nonNull).filter(homeAssets12 -> homeAssets12.getAssetType() != null).forEach(homeAssets1 -> {
                AssetType assetType = assetTypeService.getMostSuperiorAssetTypeById(homeAssets1.getAssetType());
                /**
                 * 1 UNIX;  2 WINDOWS ;  3 Active Dircetory ; 4 网络设备 ; 5	数据库 ;
                 * 6 C/S ;  7 B/S ;  8 中间件;  9 主机 ;  10	HA资源
                 */
                if (!Objects.isNull(assetType)) {
                    switch (assetType.getId()) {
                        case 1:
                            //UNIX
                            homeAssetsWeek.setUnixNums(homeAssets1.getAssetNums() + homeAssetsWeek.getUnixNums());
                            break;
                        case 2:
                            //WINDOWS
                            homeAssetsWeek.setWindowsNums(homeAssets1.getAssetNums() + homeAssetsWeek.getWindowsNums());
                            break;
                        case 4:
                            //网络设备
                            homeAssetsWeek.setNetNums(homeAssets1.getAssetNums() + homeAssetsWeek.getNetNums());
                            break;
                        case 5:
                            //数据库
                            homeAssetsWeek.setDbNums(homeAssets1.getAssetNums() + homeAssetsWeek.getDbNums());
                            break;
                        case 6:
                            // C/S
                            homeAssetsWeek.setCsNums(homeAssets1.getAssetNums() + homeAssetsWeek.getCsNums());
                            break;
                        case 7:
                            // B/S
                            homeAssetsWeek.setBsNums(homeAssets1.getAssetNums() + homeAssetsWeek.getBsNums());
                            break;
                        default:
                            break;
                    }
                }
            });
            //设置主机数量、应用数量
            homeAssetsWeek.setHostNums(homeAssetsWeek.getUnixNums() + homeAssetsWeek.getWindowsNums());
            homeAssetsWeek.setApplicationNums(homeAssetsWeek.getCsNums() + homeAssetsWeek.getBsNums());
            return homeAssetsWeek;
        }).collect(Collectors.toList());

        return homeAssetsWeeks;
    }

    /**
     * 查询所有操作员设备设备组权限
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getUserAssetAssetgroup() {
        return baseMapper.getUserAssetAssetgroup();
    }

    @Override
    public void saveUserAssetAssetgroup(List<Map<String, Object>> list) {
        baseMapper.saveUserAssetAssetgroup(list);
    }

    @Override
    public void deleteUserAssetAssetgroup() {
        baseMapper.deleteUserAssetAssetgroup();
    }

    /**
     * 构建多类型树结构
     *
     * @param assetgroup 设备组
     * @param asset      设备
     * @param trees      待构建的结构树
     */
    private void formatTree(Assetgroup assetgroup, Asset asset, List<ResultTree> trees) {
        //添加设备组的节点
        //trees.add(new ResultTree(PREFIX_GROUP + assetgroup.getId(), assetgroup.getName(), assetgroup.getLevel(), assetgroup.getPid() != null ? PREFIX_GROUP + assetgroup.getPid() : null, NODE_GROUP, "",  false));

        //添加设备的节点
        trees.add(new ResultTree(AssetConstans.PREFIX_ASSET + asset.getId(), asset.getName(), assetgroup.getLevel() + 1, AssetConstans.PREFIX_GROUP + assetgroup.getId(), AssetConstans.NODE_ASSET, "icon-monitor", false, false, false));

        //添加账户的节点
        if (!ListUtils.isEmpty(asset.getAccounts())) {
            List<ResultTree> accountTrees = asset.getAccounts().stream().map(account -> new ResultTree(AssetConstans.PREFIX_ACCOUNT + account.getId(), account.getUsername(), assetgroup.getLevel() + 2, AssetConstans.PREFIX_ASSET + asset.getId(), AssetConstans.NODE_ACCOUNT, "", false, false, false)).collect(Collectors.toList());
            trees.addAll(accountTrees);
        }
    }

    //------设备授权相关--end---------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public List<Asset> getPublishList() {
        return baseMapper.getPublishList();
    }

    /**
     * 检查ip是否重复
     * ip地址允许重复,只要不为空,就能入库
     *
     * @param asset
     * @return 是否可用，true为该ip可用，false为该ip不可用。
     */
    private boolean checkIpDuplicate(Asset asset) {
        Objects.requireNonNull(asset, "asset不能为null");
        return !StringUtils.isEmpty(asset.getIp());
    }

    /**
     * 检查名称是否重复
     * 名称不允许重复
     *
     * @param asset
     * @return 是否可用，true为该名称可用，false为该名称不可用。
     */
    private boolean checkNameDuplicate(Asset asset) {
        Objects.requireNonNull(asset, "asset不能为null");
        if (StringUtils.isEmpty(asset.getName())) {
            return false;
        }
        Asset one = this.getOne(new QueryWrapper<Asset>().eq("name", asset.getName()));
        if (Objects.isNull(one)) {
            return true;
        }
        return one.getId().equals(asset.getId());
    }

    private void saveAccountList(Asset entity) {
        Objects.requireNonNull(entity, "asset不能为null");
        List<AssetAccount> accounts = entity.getAccounts();
        if (Objects.nonNull(accounts)) {
            // 避免前端没有设置设备id
            for (AssetAccount account : accounts) {
                account.setAssetId(entity.getId());
            }
            assetAccountService.saveBatch(accounts);
        }
    }

    /**
     * 保存账号列表信息
     *
     * @param entity
     */
    private void updateAccountList(Asset entity, Callback callback) {
        Objects.requireNonNull(entity, "asset不能为null");
        List<AssetAccount> accounts = entity.getAccounts();
        // 先拿到之前的数据
        List<AssetAccount> preList = assetAccountService.list(new QueryWrapper<AssetAccount>().eq("asset_id", entity.getId()));
        if (Objects.nonNull(preList)) {
            // 统计出需要删除的集合
            List<AssetAccount> toBeDelete = preList.stream()
                    .filter(preAccount -> {
                        // 如果新的账号列表为空,表示要删除之前所有的账号信息
                        if (CollectionUtils.isEmpty(accounts)) {
                            return true;
                        }
                        for (AssetAccount account : accounts) {
                            if (preAccount.getId().equals(account.getId())) {
                                return false;
                            }
                        }
                        return true;
                    }).collect(Collectors.toList());

            List<Integer> applyForDelete = toBeDelete.stream()
                    .filter(item -> {
                        Integer id = item.getId();
                        // 没有授权的直接删除
                        if (!hasGranted(null, id)) {
                            assetAccountService.remove(new QueryWrapper<AssetAccount>().eq("id", id));
                            saveSystemLog("设备管理-删除设备账号", "在 【设备管理】中 执行【删除设备账号】操作成功：【设备账号名称：" + item.getUsername() + "】", "AssetController.update(..) invoke");
                            return false;
                        }
                        return true;
                    })
                    // 需要提交审批的id集合
                    .map(AssetAccount::getId)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(applyForDelete)) {
                grantedService.applyForDeleteAssetAccount(applyForDelete);
                if (Objects.nonNull(callback)) {
                    callback.result("提交审批成功");
                }
            }
        }
        // 保存新的数据
        if (!CollectionUtils.isEmpty(accounts)) {
            // 避免前端没有设置设备id
            for (AssetAccount account : accounts) {
                account.setAssetId(entity.getId());
            }
            assetAccountService.saveOrUpdateBatch(accounts);
        }
    }

    /**
     * 保存SSO(单点登录)从表
     *
     * @param entity
     */
    private void saveOrUpdateSSOTable(Asset entity) {
        Objects.requireNonNull(entity, "asset不能为null");
        List<SsoRule> ssoRules = entity.getSsoRules();
        // 先过滤出需要删除的数据
        List<SsoRule> preList = ssoRuleService.list(new QueryWrapper<SsoRule>().eq("asset_id", entity.getId()));
        if (!CollectionUtils.isEmpty(preList)) {
            List<SsoRule> toBeDelete = preList.stream().filter(preSsoRule -> {
                if (assetResourceDispatcher.checkOperationToolUsed(preSsoRule.getId())) {
                    return false;
                }
                if (CollectionUtils.isEmpty(ssoRules)) {
                    return true;
                }
                for (SsoRule ssoRule : ssoRules) {
                    if (preSsoRule.getId().equals(ssoRule.getId())) {
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());
            toBeDelete.forEach(item -> ssoRuleService.removeById(item.getId()));
        }
        if (!CollectionUtils.isEmpty(ssoRules)) {
            ssoRules.forEach(item -> item.setAssetId(entity.getId()));
            ssoRuleService.saveOrUpdateBatch(ssoRules);
        }
    }

    /**
     * 添加系统日志
     *
     * @param logPage 页面功能
     * @param logOp   描述
     * @param logDes  参数
     */
    void saveSystemLog(String logPage, String logOp, String logDes) {
        //添加日志
        LogSystem log = new LogSystem(LogType.SYSTEM_DELETE.getValue(), logPage, SecurityUtils.getCurrentUser().getName() + logOp, logDes);
        systemService.saveLog(log);
    }

}
