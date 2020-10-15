package com.goldencis.osa.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetDb;
import com.goldencis.osa.asset.mapper.AssetDbMapper;
import com.goldencis.osa.asset.resource.AssetResourceType;
import com.goldencis.osa.asset.service.IAssetDbService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.ISsoRuleService;
import com.goldencis.osa.common.utils.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 设备从表(数据库类型设备) 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Service
public class AssetDbServiceImpl extends ServiceImpl<AssetDbMapper, AssetDb> implements IAssetDbService {

    private IAssetService assetService;
    @Autowired
    private ISsoRuleService ssoRuleService;

    @Override
    public AssetResourceType resourceType() {
        return AssetResourceType.ASSETDB;
    }

    @Override
    public void insertAssetResource(AssetDb assetDb) {
        updateAssetResource(assetDb);
    }

    @Override
    public void deleteAssetResource(Integer assetId) {
        baseMapper.delete(assetQueryWrapper(assetId));
    }

    @Override
    public void updateAssetResource(AssetDb entity) {
        Integer assetId = entity.getAssetId();
        Objects.requireNonNull(assetId, "asset_id不能为null");
        if (Objects.isNull(selectAssetResource(assetId))) {
            baseMapper.insert(entity);
        } else {
            baseMapper.update(entity, assetQueryWrapper(assetId));
        }
    }

    @Override
    public AssetDb selectAssetResource(Integer assetId) {
        return baseMapper.selectResourceDetailByAssetId(assetId);
    }

    @Override
    public List<AssetDb> selectListByPublishId(Integer publishId) {
        return baseMapper.selectList(new QueryWrapper<AssetDb>().eq("publish", publishId));
    }

    @Override
    public Asset getPublishByAssetId(Integer assetId) {
        AssetDb assetDb = selectAssetResource(assetId);
        if (Objects.isNull(assetDb)) {
            return null;
        }
        if (Objects.isNull(assetService)) {
            assetService = SpringUtil.getBean(IAssetService.class);
        }
        Asset publish = assetService.getById(assetDb.getPublish());
        publish.setSsoRule(ssoRuleService.getById(assetDb.getOperationTool()));
        return publish;
    }

    @Override
    public boolean checkOperationToolUsed(Integer id) {
        if (Objects.isNull(id)) {
            return false;
        }
        List<AssetDb> list = baseMapper.selectList(new QueryWrapper<AssetDb>().eq("operation_tool", id));
        return !list.isEmpty();

    }

    private QueryWrapper<AssetDb> assetQueryWrapper(Integer assetId) {
        return new QueryWrapper<AssetDb>().eq("asset_id", assetId);
    }
}
