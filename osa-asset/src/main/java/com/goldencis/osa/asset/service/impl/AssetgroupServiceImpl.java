package com.goldencis.osa.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.asset.entity.AssetAssetgroup;
import com.goldencis.osa.asset.entity.Assetgroup;
import com.goldencis.osa.asset.entity.Granted;
import com.goldencis.osa.asset.mapper.AssetAssetgroupMapper;
import com.goldencis.osa.asset.mapper.AssetgroupMapper;
import com.goldencis.osa.asset.service.IAssetgroupService;
import com.goldencis.osa.asset.service.IGrantedService;
import com.goldencis.osa.asset.util.AssetConstans;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultTree;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.core.entity.LogSystem;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.ILogSystemService;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.core.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-24
 */
@Service
public class AssetgroupServiceImpl extends ServiceImpl<AssetgroupMapper, Assetgroup> implements IAssetgroupService {

    private final Logger logger = LoggerFactory.getLogger(AssetgroupServiceImpl.class);

    @Autowired
    private AssetgroupMapper assetgroupMapper;

    @Autowired
    private AssetAssetgroupMapper assetAssetgroupMapper;
    @Autowired
    private IGrantedService grantedService;

    @Autowired
    private ILogSystemService logSystemService;

    @Override
    public List<Assetgroup> getAssetgroupListByPid(Integer id) {
        List<Assetgroup> list = assetgroupMapper.getAssetgroupListByPid(id);
        return list;
    }

    @Override
    public QueryWrapper<Assetgroup> parseParams2QueryWapper(Map<String, String> params) {
        QueryWrapper<Assetgroup> wrapper = new QueryWrapper<>();

        QueryUtils.setQeryConditionByParamsMap(wrapper, params, "name");

        //增加排序条件，默认按创建时间的倒序排列
        QueryUtils.setQeryOrderByParamsMap(wrapper, params, ConstantsDto.ORDER_TYPE_DESC, "create_time");
        // 设置用户权限筛选
        User user = SecurityUtils.getCurrentUser();
        String guid = user.getGuid();
        // 超级管理员和系统自带操作员,不需要设备组权限
        if (!ConstantsDto.USER_SYSTEM_ID.equals(guid)
                && !ConstantsDto.USER_OPERATOR_ID.equals(guid)) {
            List<Integer> list = baseMapper.getGroupIdByUserPermission(guid);
            if (CollectionUtils.isEmpty(list)) {
                // 集合为空,没有权限,不查任何
                wrapper.eq("id", -1);
            } else {
                wrapper.in("id", list);
            }
        }
        return wrapper;
    }

    @Override
    public void getAssetgroupInPage(IPage<Assetgroup> page, QueryWrapper<Assetgroup> wrapper) {
        //基本的分页查询
        this.page(page, wrapper);

        if (!ListUtils.isEmpty(page.getRecords())) {
            Set<Integer> pidSet = page.getRecords().stream().map(assetgroup -> {
                assetgroup.setAssetCount(this.countAssetInGroups(assetgroup.getId()));
                return assetgroup;
            }).map(Assetgroup::getPid).collect(Collectors.toSet());

            //查询父类集合
            List<Assetgroup> pgrouplist = assetgroupMapper.selectList(new QueryWrapper<Assetgroup>().in("id", pidSet));
            if (!ListUtils.isEmpty(pgrouplist)) {
                //将父类集合转化为Map，key为id，value为组
                Map<Integer, Assetgroup> pgroupMap = pgrouplist.stream().collect(Collectors.toMap(Assetgroup::getId, assetgroup -> assetgroup));
                //为设备组添加添加所属组名称
                page.getRecords().stream().filter(assetgroup -> pgroupMap.containsKey(assetgroup.getPid())).forEach(assetgroup -> assetgroup.setPname(pgroupMap.get(assetgroup.getPid()).getName()));
            }
        }
    }

    @Override
    @Cacheable(value = "assetgroup_assetcount", key = "#id")
    public int countAssetInGroups(Integer id) {
        if (id != null) {
            return assetAssetgroupMapper.selectCount(new QueryWrapper<AssetAssetgroup>().eq("assetgroup_id", id));
        }
        return 0;
    }

    @Override
    public boolean checkAssetgroupNameDuplicate(Assetgroup assetgroup) {
        Assetgroup preAssetgroup = this.findAssetgroupByName(assetgroup.getName());

        //判断数据库是否有该记录，不存在即可用，返回true，如果有继续判断
        if (preAssetgroup != null) {
            //比较两个对象的id，若一致，是同一个对象没有改变名称的情况，返回可用true。
            if (preAssetgroup.getId().equals(assetgroup.getId())) {
                return true;
            }
            //若果不同，说明为两个对象，名称重复，不可用，返回false
            return false;
        }
        return true;
    }

    @Override
    public Assetgroup findAssetgroupByName(String name) {
        List<Assetgroup> list = assetgroupMapper.selectList(new QueryWrapper<Assetgroup>().eq("name", name));
        if (Objects.isNull(list) || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public void saveAssetgroup(Assetgroup assetgroup) {
        //验证数据长度
        verifyAssetgroupData(assetgroup);
        //设置父级设备组相关信息
        this.setParentAssetGroupInfo(assetgroup);

        assetgroup.setStatus(ConstantsDto.CONST_TRUE);
        assetgroup.setCreateTime(LocalDateTime.now());
        User user = SecurityUtils.getCurrentUser();
        assetgroup.setCreateBy(user.getGuid());

        assetgroupMapper.insertAndGetPrimaryKey(assetgroup);

        Integer id = assetgroup.getId();
        baseMapper.insertUserAssetGroupPermission(user.getGuid(), id);
    }

    @Override
    public void editAssetgroup(Assetgroup assetgroup) {
        //验证数据长度
        verifyAssetgroupData(assetgroup);

        // 避免修改默认的顶级部门
        if (!ConstantsDto.SUPER_GROUP.equals(assetgroup.getId())) {
            String oldTreePath = assetgroup.getTreePath();
            String oldSelfPath = oldTreePath + assetgroup.getId() + ConstantsDto.SEPARATOR_COMMA + "%";

            //设置父级设备组相关信息
            this.setParentAssetGroupInfo(assetgroup);

            String newTreePath = assetgroup.getTreePath();

            //更新子级设备组的TreePath
            assetgroupMapper.updateTreePath(oldTreePath, newTreePath, oldSelfPath);
        }

        assetgroupMapper.updateById(assetgroup);
    }

    @Override
    public int deleteAssetgroup(Integer id) {
        List<Assetgroup> list = assetgroupMapper.getAssetgroupListByPid(id);
        if (ListUtils.isEmpty(list)) {
            throw new RuntimeException("该设备组不存在！");
        } else if (list.size() > 1) {
            throw new RuntimeException("该设备组存在子级设备组，不能删除！");
        }

        List<AssetAssetgroup> relationList = assetAssetgroupMapper.selectList(new QueryWrapper<AssetAssetgroup>().eq("assetgroup_id", id));
        if (!ListUtils.isEmpty(relationList)) {
            throw new RuntimeException("该设备组下存在设备，不能删除！");
        }
        if (Objects.nonNull(grantedService.getOne(new QueryWrapper<Granted>().eq("assetgroup_id", id)))) {
            grantedService.applyForDeleteAssetGroup(Collections.singletonList(id));
            return -1;
        }
        realDeleteAssetgroup(id);
        return 0;
    }

    @Override
    public int deleteBatch(List<Integer> list) {
        AtomicInteger successCount = new AtomicInteger();
        List<Integer> collect = list.stream()
                .filter(Objects::nonNull)
                .map(this::getById)
                .filter(Objects::nonNull)
                // 排序,从level层级最低的开始删除,
                .sorted((o1, o2) -> o2.getLevel() - o1.getLevel())
                // 过滤掉不允许删除的设备组
                .filter(item -> {
                    List<Assetgroup> innerList = assetgroupMapper.getAssetgroupListByPid(item.getId());
                    if (ListUtils.isEmpty(innerList)) {
                        logger.warn("该设备组不存在！");
                        return false;
                    } else if (innerList.size() > 1) {
                        logger.warn("该设备组存在子级设备组，不能删除！");
                        return false;
                    }
                    List<AssetAssetgroup> relationList = assetAssetgroupMapper.selectList(new QueryWrapper<AssetAssetgroup>().eq("assetgroup_id", item.getId()));
                    if (!ListUtils.isEmpty(relationList)) {
                        logger.warn("该设备组下存在设备，不能删除！");
                        return false;
                    }
                    return true;
                })
                .filter(item -> {
                    if (checkGrantById(item.getId())) {
                        return true;
                    }
                    // 没有授权的直接删除
                    try {
                        realDeleteAssetgroup(item.getId());
                        successCount.getAndIncrement();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .map(Assetgroup::getId)
                .collect(Collectors.toList());
        grantedService.applyForDeleteAssetGroup(collect);
        return successCount.get();
    }

    /**
     * 真正的删除设备组,不做任何校验的那种,
     * 在调用这个函数之前,请务必先<b>手动确认</b>待删除的设备组<b>没有任何授权,没有任何子节点,没有包含任何设备</b>
     *
     * @param id 设备组id
     */
    private void realDeleteAssetgroup(Integer id) {
        if (Objects.isNull(id)) {
            return;
        }
        Assetgroup assetgroup = assetgroupMapper.selectById(id);
        // 直接删除该设备组所有的权限记录
        assetgroupMapper.deleteAssetGroupPermissionById(id);
        assetgroupMapper.deleteById(id);
        saveSystemLog("设备组管理--删除设备组", "在【设备组管理】中 执行【删除设备组】操作成功：【设备组名称：" + assetgroup.getName() + "】", "AssetgroupController.delete(..) invoke");
    }

    /**
     * 检查设备组是否存在授权
     *
     * @param assetGroupId 设备组id
     * @return 如果存在, 返回true;否则,返回false;
     */
    private boolean checkGrantById(Integer assetGroupId) {
        return (Objects.nonNull(grantedService.getOne(new QueryWrapper<Granted>().eq("assetgroup_id", assetGroupId))));
    }

    @Override
    public List<Assetgroup> getAssetgroupListByAssetId(Integer id) {
        List<Assetgroup> list = assetgroupMapper.selectList(null);
        if (!ListUtils.isEmpty(list)) {
            List<AssetAssetgroup> relationList = assetAssetgroupMapper.selectList(new QueryWrapper<AssetAssetgroup>().eq("asset_id", id));
            if (!ListUtils.isEmpty(relationList)) {
                List<Integer> checkedIds = relationList.stream().map(AssetAssetgroup::getAssetgroupId).collect(Collectors.toList());
                list.forEach(assetgroup -> assetgroup.setChecked(checkedIds.contains(assetgroup.getId())));
                return list;
            }
        }
        return null;
    }

    @Override
    public String getPnameByAssetgroupId(Integer assetgroupId) {
        return assetgroupMapper.getPnameByAssetgroupId(assetgroupId);
    }

    /**
     * 查询操作员对应的管理的所有设备组的id集合
     *
     * @param guid 操作员guid
     * @return
     */
    @Override
    public List<Integer> getGroupIdsByOperator(String guid) {
        return baseMapper.getGroupIdsByOperator(guid);
    }

    @Override
    public List<ResultTree> formatTreePermissionGroupList(List<Assetgroup> groupList) {
        List<ResultTree> trees = new ArrayList<>();
        if (CollectionUtils.isEmpty(groupList)) {
            //如果该用户没有任何组的权限，默认展示顶级设备组，但无任何操作权限。
            Assetgroup assetgroup = this.getById(ConstantsDto.SUPER_GROUP);
            ResultTree defaultNode = new ResultTree(String.valueOf(assetgroup.getId()), assetgroup.getName(), assetgroup.getLevel(), assetgroup.getPid() != null ? String.valueOf(assetgroup.getPid()) : null, AssetConstans.NODE_GROUP, "icon-file", false, true, assetgroup.getPid() == null);
            trees.add(defaultNode);
            return trees;
        }
        Map<Integer, Assetgroup> map = groupList.stream().collect(Collectors.toMap(Assetgroup::getId, assetgroup -> assetgroup));
        Map<Integer, Assetgroup> basic = new HashMap<>(map);
        //由于当前设备组不足以构建树，需要查找构建树的最小集合
        List<Assetgroup> parentList = this.findMinGroupListToBuildTree(groupList, map);
        groupList.addAll(parentList);

        //添加设备组的节点
        List<ResultTree> groupTrees = groupList.stream().map(assetgroup -> {
            //设备组 不能被勾选( disabled: true 禁掉 checkbox  和 不能select) ，不能被选中(checked: true 是否勾选(如果勾选，子节点也会全部勾选))
            //顶级 父节点默认展开(是否展开子节点) expand ：true, 其余 父节点默认展开(是否展开子节点) expand ：false
            boolean disabled = !basic.containsKey(assetgroup.getId());
            return new ResultTree(String.valueOf(assetgroup.getId()), assetgroup.getName(), assetgroup.getLevel(), assetgroup.getPid() != null ? String.valueOf(assetgroup.getPid()) : null, AssetConstans.NODE_GROUP, "icon-file", false, disabled, assetgroup.getPid() == null);
        }).collect(Collectors.toList());
        trees.addAll(groupTrees);
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
                Assetgroup parent = this.getById(assetgroup.getPid());
                parentList.add(parent);
                groupMap.put(parent.getId(), parent);
                this.addParentNodeInListAndMap(parent, parentList, groupMap);
            }
        }
    }

    /**
     * 设置父级设备组相关信息
     *
     * @param assetgroup 设备组
     */
    private void setParentAssetGroupInfo(Assetgroup assetgroup) {
        if (assetgroup.getPid() == null) {
            assetgroup.setPid(ConstantsDto.SUPER_GROUP);
            assetgroup.setTreePath(ConstantsDto.SEPARATOR_COMMA + ConstantsDto.SUPER_GROUP + ConstantsDto.SEPARATOR_COMMA);
            assetgroup.setLevel(ConstantsDto.LEVEL_SUPER_GROUP + 1);
        } else {
            Assetgroup pAssetgroup = assetgroupMapper.selectOne(new QueryWrapper<Assetgroup>().eq("id", assetgroup.getPid()));
            assetgroup.setTreePath(pAssetgroup.getTreePath() + pAssetgroup.getId() + ConstantsDto.SEPARATOR_COMMA);
            assetgroup.setLevel(pAssetgroup.getLevel() + 1);
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
        logSystemService.saveLog(log);
    }

    /**
     * 保存数据验证
     */
    void verifyAssetgroupData(Assetgroup assetgroup) {
        if (! com.goldencis.osa.common.utils.StringUtils.isInLength(assetgroup.getName(),30)){
            throw new IllegalArgumentException("设备组名称最大长度为30");
        }
        if (! com.goldencis.osa.common.utils.StringUtils.isInLength(assetgroup.getRemark(),200)){
            throw new IllegalArgumentException("设备组备注最大长度为200");
        }
    }
}
