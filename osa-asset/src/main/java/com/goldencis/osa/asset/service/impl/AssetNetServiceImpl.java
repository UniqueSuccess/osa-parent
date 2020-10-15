package com.goldencis.osa.asset.service.impl;

import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetNet;
import com.goldencis.osa.asset.mapper.AssetNetMapper;
import com.goldencis.osa.asset.resource.AssetResourceType;
import com.goldencis.osa.asset.service.IAssetNetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 设备从表(类型为网络设备) 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Service
public class AssetNetServiceImpl extends ServiceImpl<AssetNetMapper, AssetNet> implements IAssetNetService {

    @Override
    public AssetResourceType resourceType() {
        return AssetResourceType.ASSETNET;
    }

    @Override
    public void insertAssetResource(AssetNet assetNet) {
        // do nothing
    }

    @Override
    public void deleteAssetResource(Integer assetId) {
        // do nothing
    }

    @Override
    public void updateAssetResource(AssetNet assetNet) {
        // do nothing
    }

    @Override
    public AssetNet selectAssetResource(Integer assetId) {
        // do nothing
        return null;
    }

    @Override
    public List<AssetNet> selectListByPublishId(Integer publishId) {
        return null;
    }

    @Override
    public Asset getPublishByAssetId(@NotNull Integer assetId) {
        return null;
    }

    @Override
    public boolean checkOperationToolUsed(Integer id) {
        return false;
    }
}
