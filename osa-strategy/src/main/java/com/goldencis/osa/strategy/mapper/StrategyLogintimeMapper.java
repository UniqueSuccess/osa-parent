package com.goldencis.osa.strategy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.strategy.entity.StrategyLogintime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 策略登录时间表-定义策略登录时间基本信息 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
public interface StrategyLogintimeMapper extends BaseMapper<StrategyLogintime> {

    /**
     * 通过策略guid 获取 策略登录时间表
     */
    List<StrategyLogintime> findStrategyLogintimesByStrategyGuid(String guid);

    /**
     * 通过策略guid删除管控登录
     */
    void deleteStrategyLogintimeByStrategyGuid(String strategyGuid);

    /**
     * 通过星期、策略id获取策略登录时间
     * @param strategyId 策略id
     * @param weekDay 星期（1...7）
     * @return
     */
    StrategyLogintime findStrategyLogointimeByIdAndWeekday(@Param(value = "strategyId")String strategyId, @Param(value = "weekDay")int weekDay);
}
