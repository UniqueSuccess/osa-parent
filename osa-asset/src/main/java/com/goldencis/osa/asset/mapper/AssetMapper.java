package com.goldencis.osa.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.asset.domain.AssetCount;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.HomeAssets;
import com.goldencis.osa.asset.entity.ResourceEntity;
import com.goldencis.osa.asset.params.AssetParams;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-10-25
 */
public interface AssetMapper extends BaseMapper<Asset> {

    Integer countAssetsInPage(AssetParams paramMap);

    List<Asset> getAssetsInPage(AssetParams paramMap);

    Asset getAssetDetailById(@NotNull Integer id);

    /**
     * 过滤除 应用程序发布器之外的设备、账号
     * @param assetIds
     */
    List<Asset> listWithAssetAccount(@Param(value = "assetIds") List<Integer> assetIds);

    List<Asset> getPublishList();

    /**
     * 插入一条数据,并且获取主键
     * @param asset
     */
    void insertAndGetPrimaryKey(Asset asset);

    List<Map<String, Object>> getResourceReportChart(@Param("params") Map<String, String> params);

    long getResourceReportCount(@Param("params")Map<String,String> params);

    List<ResourceEntity> getResourceReportList(@Param("params")Map<String,String> params);

    /**
     *返回设备列表（不包含应用程序发布器）
     */
    List<Asset> listAssetsNotPublis();

    /**
     * 通过 操作员guid 设置的设备权限
     *
     * @param operatorTypeAsset 类型  1,设备 2设备组
     * @param userGuid 操作员guid
     * @return 设备id集合
     */
    List<Integer> findAssetIdsByUserGuid(@Param("operatorTypeAsset")  Integer operatorTypeAsset, @Param("userGuid") String userGuid);

    /**
     * 根据用户权限,获取对应的设备id集合
     * @param userId 用户id
     * @return 设备id集合
     */
    List<Integer> getAssetIdListByUserPermission(@Param(value = "userId") String userId);

    /**
     * 根据用户权限,获取对应的设备组id集合
     * @param userId 用户id
     * @return 设备组id集合
     */
    List<Integer> getAssetGroupIdListByUserPermission(@Param(value = "userId") String userId);

    /**
     * 插入一条系统账号与设备的权限关联
     * @param guid 用户guid
     * @param id 设备id
     */
    void insertUserAssetPermission(@Param(value = "guid") String guid,
                                   @Param(value = "id") Integer id);

    List<AssetCount> infoForHomePage();

    /**
     * 根据每天获取设备数据
     * @param strDate 日期
     */
    List<HomeAssets> getHomeAssetsByDay(String strDate);

    List<Map<String, Object>> getUserAssetAssetgroup();

    void deleteUserAssetAssetgroup();

    void saveUserAssetAssetgroup(List<Map<String, Object>> list);
}
