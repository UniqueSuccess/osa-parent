package com.goldencis.osa.asset.excel.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.asset.entity.Granted;
import com.goldencis.osa.asset.excel.IImport;
import com.goldencis.osa.asset.service.IGrantedService;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.entity.UserRole;
import com.goldencis.osa.core.service.IUserRoleService;
import com.goldencis.osa.core.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-04 11:31
 **/
@Component
public class UserDataHandler implements IImport.OldDataHandler {

    @Autowired
    private IUserService userService;
    @Autowired
    private IUserRoleService userRoleService;
    @Autowired
    private IGrantedService grantedService;

    private final List<User> userList = new ArrayList<>();
    private final List<UserRole> userRoleList = new ArrayList<>();
    private final List<Granted> grantedList = new ArrayList<>();

    private boolean enable;

    /**
     * 缓存
     */
    @Override
    public void cache() {
        if (enable()) {
            // 查出所有普通用户
            userRoleList.addAll(userRoleService.list(new QueryWrapper<UserRole>().eq("role_guid", ConstantsDto.ROLE_USER_PID)));
            if (!CollectionUtils.isEmpty(userRoleList)) {
                userList.addAll(userService.list(new QueryWrapper<User>().in("guid", userRoleList.stream().map(UserRole::getUserGuid).collect(Collectors.toList()))));
            }
            grantedList.addAll(grantedService.list(null));
            // 将数据从数据库中移除
            userRoleList.forEach(item -> {
                userRoleService.remove(new QueryWrapper<UserRole>().eq("role_guid", item.getRoleGuid()).eq("user_guid", item.getUserGuid()));
            });
            if (!CollectionUtils.isEmpty(userList)) {
                userService.removeByIds(userList.stream().map(User::getGuid).collect(Collectors.toList()));
            }
            if (!CollectionUtils.isEmpty(grantedList)) {
                grantedService.removeByIds(grantedList.stream().map(Granted::getId).collect(Collectors.toList()));
            }
        }
    }

    /**
     * 清理缓存
     */
    @Override
    public void cleanup() {
        if (enable()) {
            userList.clear();
            grantedList.clear();
        }
    }

    /**
     * 恢复缓存
     */
    @Override
    public void restore() {
        if (enable()) {
            // 刷回用户表
            userService.saveOrUpdateBatch(userList);
            // 刷回授权表
            grantedService.saveOrUpdateBatch(grantedList);
            // 刷回用户角色表
            userRoleService.saveOrUpdateBatch(userRoleList);
            cleanup();
        }
    }

    /**
     * 是否启用
     *
     * @return
     */
    @Override
    public boolean enable() {
        return this.enable;
    }

    /**
     * 配置开关
     *
     * @param enable
     */
    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
