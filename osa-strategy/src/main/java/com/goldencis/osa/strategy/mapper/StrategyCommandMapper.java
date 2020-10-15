package com.goldencis.osa.strategy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.strategy.entity.StrategyCommand;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  策略命令表-定义策略命令基本信息  Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
public interface StrategyCommandMapper extends BaseMapper<StrategyCommand> {

   /**
    * 根据策略guid获取策略指令
    * @param strategyGuid
    * @return
    */
   List<StrategyCommand> findStrategyCommandsByStrategyGuid(String strategyGuid);

   /**
    * 通过策略guid删除命令
    */
   void deleteStrategyCommandByStrategyGuid(String strategyGuid);

   /**
    * 根据guid、类型获取数据
    */
    List<StrategyCommand> findStrategyCommandsByStrategyGuidType(Map<String, String> map);
}
