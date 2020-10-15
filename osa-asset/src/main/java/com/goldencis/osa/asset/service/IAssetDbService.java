package com.goldencis.osa.asset.service;

import com.goldencis.osa.asset.entity.AssetDb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.asset.resource.IResourceService;

/**
 * <p>
 * 设备从表(数据库类型设备) 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
public interface IAssetDbService extends IService<AssetDb>, IResourceService<AssetDb> {

}
