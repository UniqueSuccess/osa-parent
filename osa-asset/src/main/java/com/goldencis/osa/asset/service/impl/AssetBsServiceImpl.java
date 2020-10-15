package com.goldencis.osa.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetBs;
import com.goldencis.osa.asset.mapper.AssetBsMapper;
import com.goldencis.osa.asset.resource.AssetResourceType;
import com.goldencis.osa.asset.service.IAssetBsService;
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
 * 设备从表(B/S应用) 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Service
public class AssetBsServiceImpl extends ServiceImpl<AssetBsMapper, AssetBs> implements IAssetBsService {

    private IAssetService assetService;
    @Autowired
    private ISsoRuleService ssoRuleService;

    @Override
    public AssetResourceType resourceType() {
        return AssetResourceType.ASSETBS;
    }

    @Override
    public void insertAssetResource(AssetBs assetResource) {
        updateAssetResource(assetResource);
    }

    @Override
    public void deleteAssetResource(Integer assetId) {
        baseMapper.delete(assetQueryWrapper(assetId));
    }

    @Override
    public void updateAssetResource(AssetBs entity) {
        Integer assetId = entity.getAssetId();
        Objects.requireNonNull(assetId, "asset_id不能为null");
        if (Objects.isNull(selectAssetResource(assetId))) {
            baseMapper.insert(entity);
        } else {
            baseMapper.update(entity, assetQueryWrapper(assetId));
        }
    }

    @Override
    public AssetBs selectAssetResource(Integer assetId) {
        return baseMapper.selectResourceDetailByAssetId(assetId);
    }

    @Override
    public List<AssetBs> selectListByPublishId(Integer publishId) {
        return baseMapper.selectList(new QueryWrapper<AssetBs>().eq("publish", publishId));
    }

    @Override
    public Asset getPublishByAssetId(@NotNull Integer assetId) {
        AssetBs assetBs = selectAssetResource(assetId);
        if (Objects.isNull(assetBs)) {
            return null;
        }
        if (Objects.isNull(assetService)) {
            assetService = SpringUtil.getBean(IAssetService.class);
        }
        Asset publish = assetService.getById(assetBs.getPublish());
        publish.setSsoRule(ssoRuleService.getById(assetBs.getOperationTool()));
        return publish;
    }

    @Override
    public boolean checkOperationToolUsed(Integer id) {
        return false;
    }

    private QueryWrapper<AssetBs> assetQueryWrapper(Integer assetId) {
        return new QueryWrapper<AssetBs>().eq("asset_id", assetId);
    }
}
