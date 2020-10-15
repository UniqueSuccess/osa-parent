package com.goldencis.osa.strategy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.strategy.entity.StrategyLogintime;

import java.util.List;

/**
 * <p>
 *策略登录时间表-定义策略登录时间基本信息  服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
public interface IStrategyLogintimeService extends IService<StrategyLogintime> {

    /**
     * 通过策略guid 获取登录时间
     */
    List<StrategyLogintime> findStrategyLogintimesByStrategyGuid(String guid) ;

}
