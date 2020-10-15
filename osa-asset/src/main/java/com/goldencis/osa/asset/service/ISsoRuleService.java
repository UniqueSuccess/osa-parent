package com.goldencis.osa.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.asset.entity.SsoRule;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;

/**
 * <p>
 * SSO(单点登录)规则 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
public interface ISsoRuleService extends IService<SsoRule> {

    /**
     * 根据设备id获取SSO规则集合
     * @param assetId
     * @return
     */
    List<SsoRule> getSSORuleListByAssetId(@NotNull Integer assetId);

    SsoRule importSSORuleFromFile(File file) throws Exception;
}
