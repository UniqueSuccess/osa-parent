package com.goldencis.osa.asset.service;

import com.goldencis.osa.asset.entity.AssetType;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 设备类型,服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-26
 */
public interface IAssetTypeService extends IService<AssetType> {

    /**
     * 获取当前启用的设备类型列表
     * @return 设备类型列表
     */
    List<AssetType> getEnabledAssetTypeList();

    /**
     * 根据id获取最上级的设备类型
     * @param id 查询的id
     * @return 最上级设备类型实体类
     */
    AssetType getMostSuperiorAssetTypeById(@NotNull Integer id);

    /**
     * 获取部分设备类型列表<br>
     * 2018年11月8日10:54:54<br>
     * 产品经理要求,新建设备界面,展示的设备类型列表需要满足以下要求:<br>
     *     1. 默认不展示大类型(pid为null),只展示小类型(pid不为null);<br>
     *     2. 如果大类型下没有小类型,就展示大类型;
     * @return
     */
    List<AssetType> getPartAssetTypeList();
}
