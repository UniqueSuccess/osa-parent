package com.goldencis.osa.asset.excel.impl;

import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAssetgroup;
import com.goldencis.osa.asset.entity.Assetgroup;
import com.goldencis.osa.asset.entity.Granted;
import com.goldencis.osa.asset.excel.IImport;
import com.goldencis.osa.asset.service.IAssetAssetgroupService;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.IAssetgroupService;
import com.goldencis.osa.asset.service.IGrantedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-04 13:54
 **/
@Component
public class AssetDataHandler implements IImport.OldDataHandler {

    private boolean enable;
    @Autowired
    private IAssetService assetService;
    @Autowired
    private IAssetgroupService assetgroupService;
    @Autowired
    private IAssetAssetgroupService assetAssetgroupService;
    @Autowired
    private IGrantedService grantedService;
    /**
     * 临时存储设备
     */
    private final List<Asset> assetList = new ArrayList<>();
    private final List<Assetgroup> assetgroupList = new ArrayList<>();
    private final List<AssetAssetgroup> assetAssetgroupList = new ArrayList<>();
    private final List<Map<String, Object>> userAssetAssetgroupList = new ArrayList<>();
    private final List<Granted> grantedList = new ArrayList<>();

    /**
     * 缓存
     */
    @Override
    public void cache() {
        if (!enable()) {
            return;
        }
        // 暂存设备信息
        List<Integer> idList = assetService.list(null).stream().map(Asset::getId).collect(Collectors.toList());
        assetList.addAll(idList.stream().map(id -> assetService.getAssetDetailById(id)).collect(Collectors.toList()));
        idList.forEach(item -> assetService.realDeleteAssetById(item, false));
        // 暂存设备组信息
        assetgroupList.addAll(assetgroupService.list(null));
        assetgroupService.remove(null);
        // 暂存设备设备组中间表
        assetAssetgroupList.addAll(assetAssetgroupService.list(null));
        assetAssetgroupService.remove(null);
        // 暂存用户设备设备组权限
        userAssetAssetgroupList.addAll(assetService.getUserAssetAssetgroup());
        assetService.deleteUserAssetAssetgroup();
        // 暂存授权信息
        grantedList.addAll(grantedService.list(null));
        grantedService.remove(null);
    }

    /**
     * 清理缓存
     */
    @Override
    public void cleanup() {
        if (!enable()) {
            return;
        }
        assetList.clear();
        assetgroupList.clear();
        assetAssetgroupList.clear();
        userAssetAssetgroupList.clear();
        grantedList.clear();
    }

    /**
     * 恢复缓存
     */
    @Override
    public void restore() {
        if (!enable()) {
            return;
        }
        // 将导入的残留数据清空
        assetService.list(null)
                .stream()
                .map(Asset::getId)
                .collect(Collectors.toList())
                .forEach(id -> assetService.realDeleteAssetById(id, false));
        assetAssetgroupService.remove(null);
        assetList.forEach(item -> {
            assetService.saveOrUpdateAsset(item);
        });
        assetgroupService.saveBatch(assetgroupList);
        assetAssetgroupService.saveBatch(assetAssetgroupList);
        if (!CollectionUtils.isEmpty(userAssetAssetgroupList)) {
            assetService.saveUserAssetAssetgroup(userAssetAssetgroupList);
        }
        if (!CollectionUtils.isEmpty(grantedList)) {
            grantedService.saveOrUpdateBatch(grantedList);
        }
        cleanup();
    }

    /**
     * 是否启用
     *
     * @return
     */
    @Override
    public boolean enable() {
        return enable;
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
