package com.goldencis.osa.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultLog;
import com.goldencis.osa.core.entity.UserUsergroup;
import com.goldencis.osa.core.entity.Usergroup;
import com.goldencis.osa.core.mapper.UsergroupMapper;
import com.goldencis.osa.core.params.UsergroupParams;
import com.goldencis.osa.core.service.IUserUsergroupService;
import com.goldencis.osa.core.service.IUsergroupService;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.core.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户组表 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-23
 */
@Service
public class UsergroupServiceImpl extends ServiceImpl<UsergroupMapper, Usergroup> implements IUsergroupService {

    /**
     * 顶级用户组id
     */
    private static final int DEFAULT_USERGROUP_ID = 1;

    @Autowired
    private UsergroupMapper usergroupMapper;

    @Autowired
    private IUserUsergroupService userUsergroupService;

    @Override
    public void saveUsergroup(Usergroup group) {
        verifyUsergroupData(group);
        if (!checkUsergroupNameDuplicate(group)) {
            throw new IllegalArgumentException("用户组名称不可用");
        }
        Usergroup parent = usergroupMapper.selectById(group.getPid());
        if (parent == null) {
            throw new IllegalArgumentException("上级用户组不存在");
        }
        group.setTreePath(parent.getTreePath() + parent.getId() + ConstantsDto.SEPARATOR_COMMA);
        group.setCreateTime(LocalDateTime.now());
        group.setStatus(1);
        group.setLevel(parent.getLevel() + 1);
        group.setCreateBy(SecurityUtils.getCurrentUser().getGuid());
        usergroupMapper.insert(group);
    }

    @Override
    public String deleteUsergroupById(@NotNull Integer groupId) {
        if (groupId == DEFAULT_USERGROUP_ID) {
            throw new IllegalArgumentException("顶级用户组不可删除");
        }
        Usergroup usergroup = usergroupMapper.selectById(groupId);
        if (Objects.isNull(usergroup)){
            throw new IllegalArgumentException("用户组不存在");
        }
        Map<String, Object> map = new HashMap<>(1);
        map.put("pid", groupId);
        // 存在下级用户组时,不可删除
        List<Usergroup> usergroups = usergroupMapper.selectByMap(map);
        if (usergroups != null && !usergroups.isEmpty()) {
            throw new IllegalArgumentException("存在下级用户组,不能删除");
        }
        // 该用户组内存在成员时,不可删除
        QueryWrapper<UserUsergroup> wrapper = new QueryWrapper<>();
        wrapper.eq("usergroup_id", groupId);
        List<UserUsergroup> list = userUsergroupService.list(wrapper);
        if (list != null && !list.isEmpty()) {
            throw new IllegalArgumentException("该用户组下存在用户,不能删除");
        }
        // 删除用户组的授权信息
        baseMapper.deleteGrantedByUserGroupId(groupId);
        usergroupMapper.deleteById(groupId);
        return usergroup.getName();
    }

    @Override
    public void updateUsergroupById(@NotNull Integer id, Usergroup temp) {
        verifyUsergroupData(temp);

        Usergroup group = usergroupMapper.selectById(id);
        if (group == null) {
            throw new IllegalArgumentException("用户组不存在");
        }
        Integer pid = temp.getPid();
        if (Objects.nonNull(pid) && pid.equals(id)){
            throw new IllegalArgumentException("用户组不能选自己作为父节点");
        }

        // 检查用户组名称
        temp.setId(id);
        if (!checkUsergroupNameDuplicate(temp)) {
            throw new IllegalArgumentException("用户组名称不可用");
        }
        // 更新用户组名称
        String name = temp.getName();
        if (!StringUtils.isEmpty(name)) {
            group.setName(name);
        }
        // 更新上级用户组(treePath)
        // 顶级用户组不能更改上级用户组
        if (group.getId() != DEFAULT_USERGROUP_ID && pid != null) {
            Usergroup parent = usergroupMapper.selectById(pid);
            if (parent == null) {
                throw new IllegalArgumentException("不存在的上级用户组");
            }

            if (!parent.getTreePath().equals(group.getTreePath()) // 同级的用户组treePath相同
                    && parent.getTreePath().contains(group.getTreePath() + parent.getId() + ConstantsDto.SEPARATOR_COMMA) // 避免选择自己的子节点
            ) {
                throw new IllegalArgumentException("用户组不能选子节点作为父节点");
            }

            String oldTreePath = group.getTreePath();
            String oldSelfPath = oldTreePath + group.getId() + ConstantsDto.SEPARATOR_COMMA + "%";


            group.setPid(parent.getId());
            group.setTreePath(parent.getTreePath() + parent.getId() + ConstantsDto.SEPARATOR_COMMA);
            group.setLevel(parent.getLevel() + 1);
            group.setRemark(temp.getRemark());

            String newTreePath = group.getTreePath();
            //更新子级用户组的TreePath
            usergroupMapper.updateTreePath(oldTreePath, newTreePath, oldSelfPath);
        }
        usergroupMapper.updateById(group);
    }

    @Override
    public List<Usergroup> getUsergroupListByUserGuid(@NotNull String guid) {
        List<UserUsergroup> list = userUsergroupService.list(new QueryWrapper<UserUsergroup>().eq("user_guid", guid));
        return usergroupMapper.selectList(new QueryWrapper<Usergroup>().in("id", list.stream().map(UserUsergroup::getUsergroupId).collect(Collectors.toList())));
    }

    @Override
    public List<Usergroup> getGroupListByPid(@NotNull Integer id) {
        Usergroup parent = baseMapper.selectOne(new QueryWrapper<Usergroup>().eq("id", id));
        if (parent == null) {
            return new ArrayList<>(0);
        }
        List<Usergroup> list = new ArrayList<>();
        list.add(parent);
        List<Usergroup> children = baseMapper.selectList(new QueryWrapper<Usergroup>().eq("pid", id));
        if (children != null) {
            list.addAll(children);
        }
        return list;
    }

    @Override
    public boolean checkUsergroupNameDuplicate(Usergroup usergroup) {
        if (usergroup == null || usergroup.getName() == null || usergroup.getName().isEmpty()) {
            return false;
        }
        List<Usergroup> list = baseMapper.selectList(new QueryWrapper<Usergroup>().eq("name", usergroup.getName()));
        if (CollectionUtils.isEmpty(list)) {
            return true;
        }
        // 说明是同一条数据
        for (Usergroup one : list) {
            if (one.getId().equals(usergroup.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void getUsergroupInPage(IPage<Usergroup> page, UsergroupParams params) {
        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        int total = baseMapper.countUsergroupInPage(params);
        List<Usergroup> list = baseMapper.getUsergroupInPage(params);
        page.setTotal(total);
        page.setRecords(list);
    }

    @Override
    public List<Usergroup> getUsergroupListByGuid(String guid) {
        List<Usergroup> list = this.list(null);
        list.forEach(item -> {
            // 2018年11月8日13:55:47修改: 应产品经理要求,默认展开第一级
            if (Objects.isNull(item.getPid())) {
                item.setExpand(true);
            }
        });
        if (StringUtils.isEmpty(guid)) {
            return list;
        }
        List<UserUsergroup> userUsergroups = userUsergroupService.list(new QueryWrapper<UserUsergroup>().eq("user_guid", guid));
        for (Usergroup usergroup : list) {
            for (UserUsergroup userUsergroup : userUsergroups) {
                if (usergroup.getId().equals(userUsergroup.getUsergroupId())) {
                    usergroup.setChecked(true);
                    break;
                }
            }
        }
        return list;
    }

    @Override
    public List<Usergroup> getUsergroupListByUserGroupGuid(Integer guid) {
        List<Usergroup> list = this.list(null);
        if (guid == null) {
            return list;
        }
        for (Usergroup usergroup : list) {
            if (usergroup.getId() == guid ) {
                usergroup.setChecked(true);
            }
        }
        return list;
    }

    @Override
    public ResultLog deleteBatch(List<Integer> list) {
        // 先排序,从level最低的开始删
        List<Usergroup> collect = list.stream()
                .map(this::getById)
                .filter(Objects::nonNull)
                .sorted((o1, o2) -> o2.getLevel() - o1.getLevel())
                .collect(Collectors.toList());
        // 记录删除成功的数量
        AtomicInteger successCount = new AtomicInteger();

        //日志
        StringBuffer usergroupNames = new StringBuffer();
        collect.forEach(item -> {
            try {
               String usergroupName = this.deleteUsergroupById(item.getId());
                usergroupNames.append(usergroupName+ ConstantsDto.SEPARATOR_COMMA);
                successCount.getAndIncrement();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        usergroupNames.deleteCharAt(usergroupNames.length() - 1);
        return  new ResultLog(successCount.get(),usergroupNames.toString());
    }

    /**
     * 保存数据验证
     */
    void verifyUsergroupData(Usergroup usergroup) {
        if (! com.goldencis.osa.common.utils.StringUtils.isInLength(usergroup.getName(),30)){
            throw new IllegalArgumentException("用户组名称最大长度为30");
        }
        if (! com.goldencis.osa.common.utils.StringUtils.isInLength(usergroup.getRemark(),200)){
            throw new IllegalArgumentException("用户组备注最大长度为200");
        }
    }

}
