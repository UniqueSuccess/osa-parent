package com.goldencis.osa.strategy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.strategy.entity.StrategyCommand;

import java.util.List;

/**
 * <p>
 *  策略命令表-定义策略命令基本信息  服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
public interface IStrategyCommandService extends IService<StrategyCommand> {

    /**
     * 通过策略guid 获取命令
     */
    List<StrategyCommand> findStrategyCommandsByStrategyGuid(String guid) throws Exception;

    /**
     * 通过策略guid 、类型 获取命令
     * 1阻断会话命令，2待审核命令，3禁止执行命令
     */
    List<StrategyCommand> findStrategyCommandsByStrategyGuidType(String guid, int strategyCommandBlock);

    /**
     * 通过策略guid 获取阻断会话命令
     */
    List<StrategyCommand>  findStrategyCommandBlockByStrategyGuid(String guid);

    /**
     * 通过策略guid 获取待审核命令
     */
    List<StrategyCommand>  findStrategyCommandPendingByStrategyGuid(String guid);

    /**
     * 通过策略guid 获取禁止执行命令
     */
    List<StrategyCommand>  findStrategyCommandProhibitByStrategyGuid(String guid);
}

