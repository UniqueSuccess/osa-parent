package com.goldencis.osa.asset.resource;

import com.goldencis.osa.asset.entity.Asset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.util.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-01 14:14
 **/
@Component
public class AssetResourceDispatcher {

    @Autowired
    private IAssetTypeParser assetTypeParser;
    @Autowired
    private Map<Integer, IResourceService<? extends AssetResource>> serviceMap;

    @Nullable
    public AssetResource getResourceByTypeAndAssetId(Integer assetType, Integer assetId) {
        IResourceService service = getServiceByAssetType(assetType);
        return Objects.nonNull(service) ? service.selectAssetResource(assetId) : null;
    }

    /**
     * 根据应用程序发布器的id获取该发布器上所有的应用程序设备
     * @param publishId 发布器id
     * @return
     */
    public List<AssetResource> getResourceListByPublish(Integer publishId) {
        List<AssetResource> list = new ArrayList<>();
        serviceMap.values().forEach(iResourceService -> {
            List<? extends AssetResource> assetResources = iResourceService.selectListByPublishId(publishId);
            if (Objects.nonNull(assetResources)) {
                list.addAll(assetResources);
            }
        });
        return list;
    }

    /**
     * 根据设备id删除所有从表中的信息
     * @param assetId 设备id
     */
    public void deleteResourceByAssetId(@NotNull Integer assetId) {
        serviceMap.values().forEach(iResourceService -> iResourceService.deleteAssetResource(assetId));
    }

    public void deleteResourceByTypeAndAssetId(Integer assetType, Integer assetId) {
        IResourceService<? extends AssetResource> service = getServiceByAssetType(assetType);
        if (Objects.nonNull(service)) {
            service.deleteAssetResource(assetId);
        }
    }

    @SuppressWarnings("unchecked")
    public void saveResourceByType(AssetResource resource, Integer assetType) {
        IResourceService service = getServiceByAssetType(assetType);
        if (Objects.nonNull(service)) {
            service.insertAssetResource(resource);
        }
    }

    public Asset getPublishByTypeAndId(Integer assetId, Integer assetType) {
        IResourceService<? extends AssetResource> service = getServiceByAssetType(assetType);
        if (Objects.nonNull(service)) {
            return service.getPublishByAssetId(assetId);
        }
        return null;
    }

    public boolean checkOperationToolUsed(Integer id) {
        for (IResourceService<? extends AssetResource> service : serviceMap.values()) {
            if (service.checkOperationToolUsed(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 刷新从表信息
     * @param asset
     */
    public void refreshResource(Asset asset) {
        Integer id = asset.getId();
        Objects.requireNonNull(id, "asset id can not be null");
        deleteResourceByAssetId(id);
        AssetResource extra = asset.getExtra();
        if (Objects.nonNull(extra)) {
            extra.setAssetId(id);
            saveResourceByType(extra, asset.getType());
        }
    }

    /**
     * 获取设备类型
     * @param assetType
     * @return
     */
    public AssetResourceType getAssetResourceType(Integer assetType) {
        if (Objects.isNull(assetType)) {
            return null;
        }
        return assetTypeParser.parse(assetType);
    }

    @Nullable
    private IResourceService<? extends AssetResource> getServiceByAssetType(Integer assetType) {
        // 将assetType转换为resourceType
        AssetResourceType resourceType = assetTypeParser.parse(assetType);
        return Objects.nonNull(resourceType) ? getResourceService(resourceType.getValue()) : null;
    }

    /**
     * 根据资源类型，获取对应的mapper
     * @param resourceType 资源类型
     * @return 对应的mapper
     */
    private IResourceService<? extends AssetResource> getResourceService(Integer resourceType) {
        return serviceMap.get(resourceType);
    }

}
