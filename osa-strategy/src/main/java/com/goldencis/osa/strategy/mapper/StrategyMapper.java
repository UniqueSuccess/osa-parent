package com.goldencis.osa.strategy.mapper;

import com.goldencis.osa.strategy.entity.Strategy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  策略表-定义策略基本信息 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
public interface StrategyMapper extends BaseMapper<Strategy> {
    /**
     * 根据guid查询策略
     * @param guid 用户guid
     * @return 用户对象
     */
    Strategy findStrategyByGuid(String guid);

    /**
     * 根据参数统计策略数量
     */
    int countStrategyInPage(Map<String, Object> params);

    /**
     * 根据参数查询策略集合
     * （只是策略基本表，其他关联无）
     */
    List<Strategy> getStrategyInPage(Map<String, Object> params);
}
