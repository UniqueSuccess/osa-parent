package com.goldencis.osa.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.asset.entity.AssetType;
import com.goldencis.osa.asset.mapper.AssetTypeMapper;
import com.goldencis.osa.asset.service.IAssetTypeService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-26
 */
@Service
public class AssetTypeServiceImpl extends ServiceImpl<AssetTypeMapper, AssetType> implements IAssetTypeService {

    private static final String CACHE_VALUE = "assetType";

    @Override
    @Cacheable(value = CACHE_VALUE, key = "#root.method.name")
    public List<AssetType> getEnabledAssetTypeList() {
        List<AssetType> list = baseMapper.selectList(null);
        Map<Integer, AssetType> map = list.stream().collect(Collectors.toMap(AssetType::getId, assetType -> assetType));
        List<AssetType> collect = list.stream()
                .filter(item -> {
                    // 过滤掉不启用的设备类型;
                    // 如果大类型不启用,小类型也要过滤掉;
                    // 层级关系目前只有两级;
                    Boolean status = item.getStatus();
                    // 先将明确不启用的过滤出去
                    if (Objects.nonNull(status) && !status) {
                        return false;
                    }
                    Integer pid = item.getPid();
                    if (Objects.isNull(pid)) {
                        return true;
                    }
                    AssetType parent = map.get(pid);
                    // 如果大类型不启用,将小类型也过滤掉
                    return Objects.isNull(parent.getStatus()) || parent.getStatus();
                }).collect(Collectors.toList());
        for (AssetType assetType : collect) {
            if (StringUtils.isEmpty(assetType.getStyle())) {
                AssetType p = map.get(assetType.getPid());
                assetType.setStyle(p.getStyle());
            }
        }
        return collect;
    }

    @Override
//    @Cacheable(value = CACHE_VALUE, key = "#id")
    public AssetType getMostSuperiorAssetTypeById(@NotNull Integer id) {
        AssetType assetType = baseMapper.selectById(id);
        if (assetType == null) {
            throw new IllegalArgumentException("没有找到对应的设备类型 -> " + id);
        }
        while (Objects.nonNull(assetType) && Objects.nonNull(assetType.getPid())) {
            assetType = baseMapper.selectById(assetType.getPid());
        }
        return assetType;
    }

    @Override
    public List<AssetType> getPartAssetTypeList() {
        List<AssetType> list = getEnabledAssetTypeList();
        for (int i = 0; i < list.size(); i++) {
            AssetType assetType = list.get(i);
            // 小类型
            if (!Objects.isNull(assetType.getPid())) {
                list.removeIf(item -> item.getId().equals(assetType.getPid()));
            }
        }
        return list;
    }
}
