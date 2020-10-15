package com.goldencis.osa.session.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.entity.AssetType;
import com.goldencis.osa.asset.entity.SsoRule;
import com.goldencis.osa.asset.resource.AssetResourceDispatcher;
import com.goldencis.osa.asset.resource.domain.Linux;
import com.goldencis.osa.asset.resource.domain.Windows;
import com.goldencis.osa.asset.service.IAssetAccountService;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.IAssetTypeService;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.core.entity.Dictionary;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.IDictionaryService;
import com.goldencis.osa.core.utils.SecurityUtils;
import com.goldencis.osa.common.config.ClientConfig;
import com.goldencis.osa.session.domain.RemoteAppConfig;
import com.goldencis.osa.session.entity.SsoConfiguration;
import com.goldencis.osa.session.mapper.SsoConfigurationMapper;
import com.goldencis.osa.session.service.ISsoConfigurationService;
import com.goldencis.osa.session.service.ITerminalSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 单点登录配置表 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
@Service
public class SsoConfigurationServiceImpl extends ServiceImpl<SsoConfigurationMapper, SsoConfiguration> implements ISsoConfigurationService {

    private final Logger logger = LoggerFactory.getLogger(SsoConfigurationServiceImpl.class);
    private final IAssetTypeService assetTypeService;
    private final IDictionaryService dictionaryService;
    private final IAssetService assetService;
    private final IAssetAccountService accountService;
    private final ITerminalSessionService terminalSessionService;
    private final AssetResourceDispatcher assetResourceDispatcher;
    @Autowired
    private ClientConfig clientConfig;

    private final String SSO_CONFIGURATION_TEMPLATE = "SSO_CONFIGURATION_TEMPLATE";

    @Autowired
    public SsoConfigurationServiceImpl(IAssetTypeService assetTypeService,
                                       IDictionaryService dictionaryService,
                                       IAssetService assetService,
                                       IAssetAccountService accountService,
                                       ITerminalSessionService terminalSessionService,
                                       AssetResourceDispatcher assetResourceDispatcher) {
        this.assetTypeService = assetTypeService;
        this.dictionaryService = dictionaryService;
        this.assetService = assetService;
        this.accountService = accountService;
        this.terminalSessionService = terminalSessionService;
        this.assetResourceDispatcher = assetResourceDispatcher;
    }

    @Override
    public SsoConfiguration findSsoConfigurationByAssetAndAccount(Asset asset, AssetAccount assetAccount) {
        //获取当前登录用户
        User user = SecurityUtils.getCurrentUser();
        //根据用户guid，设备id，设备账户id查询配置信息
        SsoConfiguration ssoConfiguration = baseMapper.selectOne(new QueryWrapper<SsoConfiguration>().eq("user_guid", user.getGuid()).eq("asset_id", asset.getId()).eq("account_id", assetAccount.getId()));

        //校验存储的配置信息的设备类型，跟设备是否一致。
        AssetType superType = assetTypeService.getMostSuperiorAssetTypeById(asset.getType());
        if (ssoConfiguration == null || !ssoConfiguration.getAssetType().equals(superType.getId())) {
            if (terminalSessionService.isRemoteApp(superType)) {
                return findSsoConfigurationForRemoteApp(asset, assetAccount, superType);
            }
            //如果没有或者不一致，需要从字典表中，取出对应的配置模板
            Dictionary dict = dictionaryService.getDictionaryListByTypeAndName(SSO_CONFIGURATION_TEMPLATE, superType.getName());
            ssoConfiguration = new SsoConfiguration(user.getGuid(), asset.getId(), assetAccount.getId(), superType.getId(), dict.getNickname());
            JSONObject jsonObject = JSONObject.parseObject(dict.getNickname());
            ssoConfiguration.setSsoConfigurationJson(jsonObject);
        }

        ssoConfiguration.setAsset(asset);
        ssoConfiguration.setAssetAccount(assetAccount);
        return ssoConfiguration;
    }

    /**
     * remoteApp配置
     * @param asset
     * @param assetAccount
     * @param superType
     * @return
     */
    private SsoConfiguration findSsoConfigurationForRemoteApp(Asset asset, AssetAccount assetAccount, AssetType superType) {
        Asset publish = assetResourceDispatcher.getPublishByTypeAndId(asset.getId(), superType.getId());
        Objects.requireNonNull(publish, "应用程序发布器不存在,app信息: " + asset);
        SsoRule ssoRule = publish.getSsoRule();
        Objects.requireNonNull(ssoRule, "运维工具不存在,app信息: " + asset);

        RemoteAppConfig config = new RemoteAppConfig();
        config.setPublishId(publish.getId());
        config.setPublishName(publish.getName());
        config.setSsoRuleId(ssoRule.getId());
        config.setSsoRuleName(ssoRule.getName());

        // 根据应用程序发布器的型号,确定url
        Integer type = publish.getType();
        Objects.requireNonNull(type, "应用程序发布器的设备类型不能为空: " + publish);
        if (type == ConstantsDto.ID_WINDOW_SERVER_2003) {
            config.setUrl(clientConfig.getWinServer2003Url());
        } else {
            AssetType assetType = assetTypeService.getMostSuperiorAssetTypeById(type);
            if (new Windows().typeId().equals(assetType.getId())) {
                config.setUrl(clientConfig.getWinServerUrl());
            } else if (new Linux().typeId().equals(assetType.getId())) {
                config.setUrl(clientConfig.getLinuxServerUrl());
            } else {
                logger.warn("不支持的应用程序发布器类型: " + publish);
            }
        }

        String json = JSON.toJSONString(config);
        logger.debug("json : {}", json);
        SsoConfiguration configuration = new SsoConfiguration(SecurityUtils.getCurrentUser().getGuid(), asset.getId(), assetAccount.getId(), superType.getId(), json);
        configuration.setAsset(asset);
        configuration.setAssetAccount(assetAccount);
        configuration.setSsoConfigurationJson(JSONObject.parseObject(json));
        return configuration;
    }

    @Override
    public void saveConfigureSSO(Asset asset, AssetAccount account, String configStr) {
        //获取当前登录用户
        User user = SecurityUtils.getCurrentUser();
        //获取顶级类型
        AssetType superType = assetTypeService.getMostSuperiorAssetTypeById(asset.getType());

        //如果存在，跟新类型和配置信息
        SsoConfiguration ssoConfiguration = baseMapper.selectOne(new QueryWrapper<SsoConfiguration>().eq("user_guid", user.getGuid()).eq("asset_id", asset.getId()).eq("account_id", account.getId()));
        if (ssoConfiguration != null) {
            ssoConfiguration.setAssetType(superType.getId());
            ssoConfiguration.setConfiguration(configStr);
            baseMapper.updateById(ssoConfiguration);
        } else {
            SsoConfiguration config = new SsoConfiguration(user.getGuid(), asset.getId(), account.getId(), superType.getId(), configStr);
            baseMapper.insert(config);
        }
    }

    @Override
    public void saveConfigureSSO(JSONArray infoArr, String configStr) {
        for (int i = 0; i < infoArr.size(); i++) {
            JSONObject info = infoArr.getJSONObject(i);

            Integer assetId = info.getInteger("assetId");
            Asset asset = assetService.getById(assetId);

            Integer accountId = info.getInteger("accountId");
            AssetAccount account = accountService.getById(accountId);

            this.saveConfigureSSO(asset, account, configStr);
        }
    }
}
