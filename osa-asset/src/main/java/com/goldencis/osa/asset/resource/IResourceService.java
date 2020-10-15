package com.goldencis.osa.asset.resource;

import com.goldencis.osa.asset.entity.Asset;
import reactor.util.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 资产从表服务层的顶级接口,
 * 统一接口规范
 * @param <T>
 */
public interface IResourceService<T extends AssetResource> {

    AssetResourceType resourceType();

    /**
     * 增
     * @param t
     */
    void insertAssetResource(T t);

    /**
     * 删
     * @param assetId
     */
    void deleteAssetResource(Integer assetId);

    /**
     * 改
     * @param t
     */
    void updateAssetResource(T t);

    /**
     * 查
     * @param assetId
     * @return
     */
    T selectAssetResource(Integer assetId);

    /**
     * 根据应用程序发布器id获取所有的应用程序集合
     * @param publishId
     * @return
     */
    List<T> selectListByPublishId(Integer publishId);

    /**
     * 根据设备id获取自己的应用程序发布器
     * @param assetId
     * @return
     */
    @Nullable
    Asset getPublishByAssetId(@NotNull Integer assetId);

    /**
     * 检验发布工具(发布规则)是否在用
     * @param id 应用程序发布工具(发布规则)id
     * @return 如果在用,返回true;否则,返回false
     */
    boolean checkOperationToolUsed(Integer id);
}
