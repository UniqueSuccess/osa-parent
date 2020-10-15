package com.goldencis.osa.asset.mapper;

import com.goldencis.osa.asset.entity.SsoRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * SSO(单点登录)规则 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
public interface SsoRuleMapper extends BaseMapper<SsoRule> {

    List<SsoRule> getSSORuleListByAssetId(Integer assetId);
}
