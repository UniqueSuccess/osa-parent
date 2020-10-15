package com.goldencis.osa.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetCs;
import com.goldencis.osa.asset.mapper.AssetCsMapper;
import com.goldencis.osa.asset.resource.AssetResourceType;
import com.goldencis.osa.asset.service.IAssetCsService;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.ISsoRuleService;
import com.goldencis.osa.common.utils.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 设备从表(C/S应用) 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-15
 */
@Service
public class AssetCsServiceImpl extends ServiceImpl<AssetCsMapper, AssetCs> implements IAssetCsService {

    private IAssetService assetService;
    @Autowired
    private ISsoRuleService ssoRuleService;

    @Override
    public AssetResourceType resourceType() {
        return AssetResourceType.ASSETCS;
    }

    @Override
    public void insertAssetResource(AssetCs assetCs) {
        updateAssetResource(assetCs);
    }

    @Override
    public void deleteAssetResource(Integer assetId) {
        baseMapper.delete(assetQueryWrapper(assetId));
    }

    @Override
    public void updateAssetResource(AssetCs entity) {
        Integer assetId = entity.getAssetId();
        Objects.requireNonNull(assetId, "asset_id不能为null");
        if (Objects.isNull(selectAssetResource(assetId))) {
            baseMapper.insert(entity);
        } else {
            baseMapper.update(entity, assetQueryWrapper(assetId));
        }
    }

    @Override
    public AssetCs selectAssetResource(Integer assetId) {
        return baseMapper.selectResourceDetailByAssetId(assetId);
    }

    @Override
    public List<AssetCs> selectListByPublishId(Integer publishId) {
        return baseMapper.selectList(new QueryWrapper<AssetCs>().eq("publish", publishId));
    }

    @Override
    public Asset getPublishByAssetId(@NotNull Integer assetId) {
        AssetCs assetCs = selectAssetResource(assetId);
        if (Objects.isNull(assetCs)) {
            return null;
        }
        if (Objects.isNull(assetService)) {
            assetService = SpringUtil.getBean(IAssetService.class);
        }
        Asset publish = assetService.getById(assetCs.getPublish());
        publish.setSsoRule(ssoRuleService.getById(assetCs.getOperationTool()));
        return publish;
    }

    @Override
    public boolean checkOperationToolUsed(Integer id) {
        if (Objects.isNull(id)) {
            return false;
        }
        return !baseMapper.selectList(new QueryWrapper<AssetCs>().eq("operation_tool", id)).isEmpty();
    }

    private QueryWrapper<AssetCs> assetQueryWrapper(Integer assetId) {
        return new QueryWrapper<AssetCs>().eq("asset_id", assetId);
    }
}
