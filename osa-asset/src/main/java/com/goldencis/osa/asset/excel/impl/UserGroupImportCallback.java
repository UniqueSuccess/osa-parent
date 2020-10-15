package com.goldencis.osa.asset.excel.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.asset.excel.IImport;
import com.goldencis.osa.core.entity.Usergroup;
import com.goldencis.osa.core.service.IUsergroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-10 14:55
 **/
@Component
public class UserGroupImportCallback implements IImport.Callback<Usergroup> {

    private final Logger logger = LoggerFactory.getLogger(UserGroupImportCallback.class);
    @Autowired
    IUsergroupService service;

    /**
     * 导入之前,先清理数据库中之前的数据
     */
    @Override
    public void cleanup() {
        // 允许覆盖时,不需要清空数据库
        if (allowCover()) {
            return;
        }
        service.remove(null);
    }

    /**
     * 是否允许覆盖
     *
     * @return
     */
    @Override
    public boolean allowCover() {
        return false;
    }

    /**
     * 保存到数据库中
     *
     * @param name
     * @param parent
     */
    @Override
    public void save(String name, Usergroup parent) {
        if (StringUtils.isEmpty(name)) {
            logger.warn("name is empty");
            return;
        }
        Usergroup temp = findByName(name);
        String treePath = ",";
        int level = 0;
        if (Objects.nonNull(parent)) {
            treePath = parent.getTreePath() + parent.getId() + ",";
            level = parent.getLevel() + 1;
        }
        Usergroup usergroup;
        if (Objects.nonNull(temp)) {
            usergroup = temp;
        } else {
            usergroup = new Usergroup();
        }
        usergroup.setName(name);
        usergroup.setLevel(level);
        usergroup.setTreePath(treePath);
        usergroup.setPid(Objects.nonNull(parent) ? parent.getId() : null);
        service.saveOrUpdate(usergroup);
    }

    /**
     * 通过名称查找指定的用户组
     *
     * @param name
     * @return
     */
    @Override
    public Usergroup findByName(String name) {
        List<Usergroup> list = service.list(new QueryWrapper<Usergroup>().eq("name", name));
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }
}
