package com.goldencis.osa.asset.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.asset.domain.AssetCount;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.Assetgroup;
import com.goldencis.osa.asset.entity.HomeAssetsWeek;
import com.goldencis.osa.asset.params.AssetParams;
import com.goldencis.osa.common.entity.ResultTree;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备模块, 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-25
 */
public interface IAssetService extends IService<Asset> {

    void getAssetsInPage(IPage<Asset> page, AssetParams params);

    /**
     * 根据id删除对应的设备
     * @param id 设备id
     * @return 删除的状态码，-1,表示设备存在授权信息，不允许删除；0表示删除成功
     */
    void deleteAssetById(Integer id);

    /**
     * 根据id删除对应的设备
     * @param id 设备id
     * @param callback 回调接口
     */
    void deleteAssetById(Integer id, Callback callback);

    /**
     * 根据设备id删除一批设备
     * @param list 设备id集合
     */
    String deleteAssetsByIdList(List<Integer> list);

    /**
     * 真正的删除一条设备信息,不经过任何校验的那种,
     * @param id 设备id
     */
    void realDeleteAssetById(@NotNull Integer id);

    /**
     * 真正的删除一条设备信息,不经过任何校验的那种,
     * @param id 设备id
     * @param log 是否记录日志信息
     */
    void realDeleteAssetById(@NotNull Integer id, boolean log);

    /**
     * 根据设备id获取设备详情
     * @param id
     * @return
     */
    Asset getAssetDetailById(@NotNull Integer id);

    /**
     * 更新或者保存一条设备信息
     * @param asset
     */
    void saveOrUpdateAsset(Asset asset);

    /**
     * 更新或者保存一条设备信息
     * @param asset
     */
    void saveOrUpdateAsset(Asset asset, Callback callback);

    /**
     * 获取设备集合，级联获取对应的设备账户集合
     * @return 设备集合,级联设备账户集合
     * @param assetIds
     */
    List<Asset> listWithAssetAccount(List<Integer> assetIds);

    /**
     * 将设备组、设备、设备账户拼接为结构树
     * @param groupList 设备组集合
     * @param assetList 设备集合,级联设备账户集合
     * @return 结构树
     */
    List<ResultTree> formatTreeWithAccountAndAssetAndGroup(List<Assetgroup> groupList, List<Asset> assetList);

    /**
     * 初始化虚拟组
     * @param collect 需要添加虚拟组的ResultTree集合
     * @return 虚拟组的集合
     */
    List<ResultTree> initNihilityList(List<ResultTree> collect);

    /**
     * 根据用户guid设置选中的设备账号
     */
    List<ResultTree> setSelectedByGuid(String guid, List<ResultTree> trees, boolean isGroup, boolean isUserGroup);

    /**
     * 获取应用程序发布器列表
     * @return
     */
    List<Asset> getPublishList();

    /**
     * 检查应用程序发布器是否在用
     */
    boolean checkPublish(Integer assetId);

    /**
     * 返回设备列表（过滤应用程序发布器）
     * @return
     */
    List<Asset> listAssetsNotPublis();

    /**
     * 根据操作员设置设备权限
     * @param trees 设备组、设备、设备账号树
     */
    List<ResultTree> setOperatorAssetByUserGuid(List<ResultTree> trees);

    /**
     * 根据操作员设置设备组权限
     * @param userGuid 操作员guid
     */
    List<ResultTree> getGrantedAssetgroupTreeByGuid(String userGuid);

    /**
     * 将设备保存入库,然后获取返回的id
     * @param asset
     * @return
     */
    Integer saveAssetAndGetPrimaryKey(@NotNull Asset asset);

    /**
     * 首页-资源数量
     */
    List<AssetCount> infoForHomePage();

    /**
     * 将设备组下的设备，插入并加入设备集合
     * @param groupList 设备组
     * @param assetList 设备集合
     */
    void findAssetsInGroupToList(List<Assetgroup> groupList, List<Asset> assetList);

    /**
     * 首页--本周运维次数
     * @return
     */
    List<HomeAssetsWeek> getHomeAssetsWeek();

    /**
     * 查询所有操作员设备设备组权限
     * @return
     */
    List<Map<String, Object>> getUserAssetAssetgroup();

    /**
     * 保存所有操作员设备设备组权限
     * @param list
     */
    void saveUserAssetAssetgroup(List<Map<String, Object>> list);

    /**
     * 删除所有操作员设备设备组权限
     * @return
     */
    void deleteUserAssetAssetgroup();

    /**
     * 更新设备的时候,返回处理结果
     */
    interface Callback {
        /**
         * 处理结果
         * @param result
         */
        void result(String result);
    }
}
