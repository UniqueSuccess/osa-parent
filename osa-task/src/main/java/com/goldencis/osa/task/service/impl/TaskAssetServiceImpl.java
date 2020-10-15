package com.goldencis.osa.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAssetgroup;
import com.goldencis.osa.asset.entity.AssetType;
import com.goldencis.osa.asset.entity.Assetgroup;
import com.goldencis.osa.asset.service.IAssetAssetgroupService;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.IAssetTypeService;
import com.goldencis.osa.asset.service.IAssetgroupService;
import com.goldencis.osa.asset.util.AssetConstans;
import com.goldencis.osa.common.entity.ResultTree;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.core.entity.QuartzJob;
import com.goldencis.osa.core.service.impl.TaskService;
import com.goldencis.osa.task.domain.*;
import com.goldencis.osa.task.entity.TaskAsset;
import com.goldencis.osa.task.entity.TaskAssetAsset;
import com.goldencis.osa.task.entity.TaskAssetEmail;
import com.goldencis.osa.task.mapper.TaskAssetMapper;
import com.goldencis.osa.task.service.ITaskAssetAssetService;
import com.goldencis.osa.task.service.ITaskAssetEmailService;
import com.goldencis.osa.task.service.ITaskAssetService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 定时改密计划 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2019-01-19
 */
@Service
public class TaskAssetServiceImpl extends ServiceImpl<TaskAssetMapper, TaskAsset> implements ITaskAssetService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private IAssetService assetService;
    @Autowired
    private IAssetgroupService assetgroupService;
    @Autowired
    private IAssetAssetgroupService assetAssetgroupService;
    @Autowired
    private ITaskAssetAssetService taskAssetAssetService;
    @Autowired
    private ITaskAssetEmailService taskAssetEmailService;
    @Autowired
    private IAssetTypeService assetTypeService;

    @Override
    public void refreshTaskAsset(TaskAsset task) {
        Type type;
        try {
            type = Type.valueOf(task.getType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("密码类型错误: " + task.getType());
        }
        switch (type) {
            case FIXED:
                checkPasswordValid(task.getPassword());
                break;
            case SAME:
            case RANDOM:
                task.setRule(calcRule(task));
                checkRuleValid(task.getRule());
                checkLengthValid(task.getLength());
                break;
            default:
                break;
        }
        if (Objects.nonNull(task.getFtp()) && task.getFtp()) {
            checkFtpAddrValid(task.getFtpAddr());
        }

        task.setId(TASK_ID);
        task.setName(TASK_NAME);
        task.setOnce(Cycle.none.equals(Cycle.valueOf(task.getCycle())));
        String cron = parseCron(task.getCycle(), task.getDay(), task.getTime());
        task.setCron(cron);

        this.saveOrUpdate(task);

        // 保存设备信息集合
        taskAssetAssetService.remove(null);
        List<String> resourceId = task.getResourceId();
        if (!CollectionUtils.isEmpty(resourceId)) {
            taskAssetAssetService.saveBatch(resourceId.stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(s -> Integer.parseInt(s.replace(AssetConstans.PREFIX_ASSET, "")))
                    .map(i -> new TaskAssetAsset(TASK_ID, i))
                    .collect(Collectors.toList()));
        }

        // 保存邮箱信息集合
        taskAssetEmailService.remove(null);
        List<String> emailAddress = task.getEmailAddress();
        if (!CollectionUtils.isEmpty(emailAddress)) {
            taskAssetEmailService.saveBatch(emailAddress.stream()
                    .map(s -> new TaskAssetEmail(TASK_ID, s))
                    .collect(Collectors.toList()));
        }

        // 更新cron表达式
        QuartzJob bean = taskService.getJob(JOB_NAME, GROUP_NAME);
        bean.setCronEx(cron);
        try {
            taskService.updateCronExpression(bean, 1);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private int calcRule(TaskAsset task) {
        int rule = 0;
        if (Objects.nonNull(task.getNumber()) && task.getNumber()) {
            rule += TaskAsset.FLAG_NUMBER;
        }
        if (Objects.nonNull(task.getLowercase()) && task.getLowercase()) {
            rule += TaskAsset.FLAG_LOWERCASE;
        }
        if (Objects.nonNull(task.getCapital()) && task.getCapital()) {
            rule += TaskAsset.FLAG_CAPITAL;
        }
        if (Objects.nonNull(task.getSpecial()) && task.getSpecial()) {
            rule += TaskAsset.FLAG_SPECIAL;
        }
        return rule;
    }

    private void checkFtpAddrValid(String ftpAddr) {
        Objects.requireNonNull(ftpAddr);
        if (!ftpAddr.contains(":")) {
            throw new IllegalArgumentException("FTP服务器地址不正确");
        }
        String[] split = ftpAddr.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("FTP服务器地址格式不正确");
        }
        try {
            Integer.parseInt(split[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("FTP端口不正确", e);
        }
    }

    private void checkPasswordValid(String password) {
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("使用固定密码时,密码不能为空!");
        }
    }

    private Integer checkLengthValid(Integer length) {
        Objects.requireNonNull(length, "密码长度不能为空");
        if (length < 6 || length > 127) {
            throw new IllegalArgumentException("密码长度不正确 " + length);
        }
        return length;
    }

    /**
     * 检查密码规则是否有效
     * 标准为:数字,小写字母,大写字母,特殊符号至少勾选一个
     *
     * @param rule
     * @return
     */
    private Integer checkRuleValid(Integer rule) {
        Objects.requireNonNull(rule, "密码规则不能为空");
        if (!TaskAsset.numberFlag(rule)
                && !TaskAsset.lowercaseFlag(rule)
                && !TaskAsset.capitalFlag(rule)
                && !TaskAsset.specialFlag(rule)) {
            throw new IllegalArgumentException("密码规则不正确,数字,小写字母,大写字母,特殊符号至少勾选一个!");
        }
        return rule;
    }

    /**
     * 转换为cron表达式
     *
     * @param cycleStr
     * @param day
     * @param time
     * @return
     */
    private String parseCron(String cycleStr, String day, String time) {
        String[] timeSplit = time.split(":");
        Cycle cycle = Cycle.valueOf(cycleStr);
        switch (cycle) {
            case none:
                // 只执行一次的时候,cron表达式为每天指定时间点执行,在执行完成之后,将cron表达式改为永不执行
                return String.format("0 %s %s * * ? *", timeSplit[1], timeSplit[0]);
            case week:
                Week week = Week.getByNum(Integer.parseInt(day));
                if (Objects.isNull(week)) {
                    throw new IllegalArgumentException("day不正确: " + day);
                }
                return String.format("0 %s %s ? * %s", timeSplit[1], timeSplit[0], week.name());
            case month:
                return String.format("0 %s %s %s * ?", timeSplit[1], timeSplit[0], day);
        }
        throw new IllegalArgumentException("cycle参数错误: " + cycleStr);
    }

    @Override
    public TaskAsset detail() {
        List<TaskAsset> list = baseMapper.selectList(null);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        TaskAsset task = list.get(0);
        List<TaskAssetAsset> assetList = taskAssetAssetService.list(new QueryWrapper<TaskAssetAsset>().eq("task_id", task.getId()));
        task.setAssetList(assetList.stream()
                .filter(Objects::nonNull)
                .map(item -> assetService.getAssetDetailById(item.getAssetId()))
                .map(AssetItem::new)
                .collect(Collectors.toList()));
        task.setEmailAddress(taskAssetEmailService.list(new QueryWrapper<TaskAssetEmail>().eq("task_id", task.getId())).stream()
                .map(TaskAssetEmail::getEmail)
                .collect(Collectors.toList()));
        Integer rule = task.getRule();
        if (Objects.nonNull(rule)) {
            task.setNumber(TaskAsset.numberFlag(rule));
            task.setLowercase(TaskAsset.lowercaseFlag(rule));
            task.setCapital(TaskAsset.capitalFlag(rule));
            task.setSpecial(TaskAsset.specialFlag(rule));
        }
        return task;
    }

    @Override
    public List<ResultTree> getAssetListTree() {
        List<TaskAssetAsset> list = taskAssetAssetService.list(null);
        //获取设备集合（过滤除 应用程序发布器之外的设备）
        List<Asset> assetList = assetService.listAssetsNotPublis();
        List<Assetgroup> groupList = assetgroupService.list(null);

        //将设备组、设备拼接为结构树
        List<ResultTree> trees = formatAssetTree(groupList, assetList);

        if (!ListUtils.isEmpty(trees)) {
            if (!ListUtils.isEmpty(list)) {
                trees.forEach(resultTree -> {
                    if (list.stream().anyMatch(item -> resultTree.getId().equals(AssetConstans.PREFIX_ASSET + item.getAssetId()))) {
                        resultTree.setChecked(true);
                        resultTree.setDisabled(true);
                        resultTree.setIgnore(true);
                    }
                });
            }
        }
        return trees;
    }

    private List<ResultTree> formatAssetTree(List<Assetgroup> groupList, List<Asset> assetList) {
        //创建多类型树
        List<ResultTree> trees = new ArrayList<>();

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
                Map<Integer, String> iconMap = assetTypeService.list(null).stream().filter(assetType -> Objects.nonNull(assetType.getIcon())).collect(Collectors.toMap(AssetType::getId, AssetType::getIcon));
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
                        this.formatTree(assetgroup, asset, assetTrees, iconMap);
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

        return trees;
    }

    /**
     * 构建多类型树结构
     *
     * @param assetgroup 设备组
     * @param asset      设备
     * @param trees      待构建的结构树
     */
    private void formatTree(Assetgroup assetgroup, Asset asset, List<ResultTree> trees, Map<Integer, String> iconMap) {
        //添加设备组的节点
        //trees.add(new ResultTree(PREFIX_GROUP + assetgroup.getId(), assetgroup.getName(), assetgroup.getLevel(), assetgroup.getPid() != null ? PREFIX_GROUP + assetgroup.getPid() : null, NODE_GROUP, "",  false));

        //添加设备的节点
        trees.add(new ResultTreeWithIp(AssetConstans.PREFIX_ASSET + asset.getId(), asset.getName(), asset.getIp(), assetgroup.getLevel() + 1, AssetConstans.PREFIX_GROUP + assetgroup.getId(), AssetConstans.NODE_ASSET, "icon-monitor", iconMap.get(asset.getType()), false, false, false));

        //添加账户的节点
        if (!ListUtils.isEmpty(asset.getAccounts())) {
            List<ResultTree> accountTrees = asset.getAccounts().stream().map(account -> new ResultTree(AssetConstans.PREFIX_ACCOUNT + account.getId(), account.getUsername(), assetgroup.getLevel() + 2, AssetConstans.PREFIX_ASSET + asset.getId(), AssetConstans.NODE_ACCOUNT, "", false, false, false)).collect(Collectors.toList());
            trees.addAll(accountTrees);
        }
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
}
