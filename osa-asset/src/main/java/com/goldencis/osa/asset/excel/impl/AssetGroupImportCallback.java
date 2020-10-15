package com.goldencis.osa.asset.excel.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.asset.entity.Assetgroup;
import com.goldencis.osa.asset.excel.IImport;
import com.goldencis.osa.asset.service.IAssetgroupService;
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
 * @create: 2018-12-13 11:51
 **/
@Component
public class AssetGroupImportCallback implements IImport.Callback<Assetgroup> {

    private final Logger logger = LoggerFactory.getLogger(AssetGroupImportCallback.class);

    @Autowired
    private IAssetgroupService service;

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
     * @param name       名称
     * @param parent 上级部门
     */
    @Override
    public void save(String name, Assetgroup parent) {
        if (StringUtils.isEmpty(name)) {
            logger.warn("name is empty");
            return;
        }
        Assetgroup temp = findByName(name);
        String treePath = ",";
        int level = 0;
        if (Objects.nonNull(parent)) {
            treePath = parent.getTreePath() + parent.getId() + ",";
            level = parent.getLevel() + 1;
        }
        Assetgroup usergroup;
        if (Objects.nonNull(temp)) {
            usergroup = temp;
        } else {
            usergroup = new Assetgroup();
        }
        usergroup.setName(name);
        usergroup.setLevel(level);
        usergroup.setTreePath(treePath);
        usergroup.setPid(Objects.nonNull(parent) ? parent.getId() : null);
        service.saveOrUpdate(usergroup);
    }

    /**
     * 通过名称查找指定的部门
     *
     * @param name
     * @return
     */
    @Override
    public Assetgroup findByName(String name) {
        List<Assetgroup> list = service.list(new QueryWrapper<Assetgroup>().eq("name", name));
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }
}
