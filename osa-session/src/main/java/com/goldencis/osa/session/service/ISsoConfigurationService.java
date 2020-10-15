package com.goldencis.osa.session.service;

import com.alibaba.fastjson.JSONArray;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.session.entity.SsoConfiguration;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 单点登录配置表 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
public interface ISsoConfigurationService extends IService<SsoConfiguration> {

    /**
     * 根据设备和账户，查找对应的单点登录配置信息
     * @param asset 设备
     * @param assetAccount 账户
     * @return 配置信息
     */
    SsoConfiguration findSsoConfigurationByAssetAndAccount(Asset asset, AssetAccount assetAccount);

    /**
     * 保存单点登录配置
     * @param assetId 设备
     * @param accountId 账户
     * @param configStr 配置详情，
     */
    void saveConfigureSSO(Asset assetId, AssetAccount accountId, String configStr);

    /**
     * 批量保存单点登录配置
     * @param infoArr 批量配置信息
     * @param configStr 配置详情
     */
    void saveConfigureSSO(JSONArray infoArr, String configStr);
}
