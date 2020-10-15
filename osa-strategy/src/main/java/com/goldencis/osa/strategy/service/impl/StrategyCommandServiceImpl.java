package com.goldencis.osa.strategy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.strategy.entity.StrategyCommand;
import com.goldencis.osa.strategy.mapper.StrategyCommandMapper;
import com.goldencis.osa.strategy.service.IStrategyCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <p>
 *  策略命令表-定义策略命令基本信息  服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Service
public class StrategyCommandServiceImpl extends ServiceImpl<StrategyCommandMapper, StrategyCommand> implements IStrategyCommandService {

    @Autowired
    StrategyCommandMapper strategyCommandMapper;
    /**
     * 通过策略guid 获取命令
     */
    @Override
    public List<StrategyCommand> findStrategyCommandsByStrategyGuid(String guid) throws Exception {
        if (guid == null || "".equals(guid)){
            throw new IllegalArgumentException("策略id不存在");
        }

        List<StrategyCommand> strategyCommands = strategyCommandMapper.findStrategyCommandsByStrategyGuid(guid);
        return strategyCommands;
    }

    @Override
    public List<StrategyCommand> findStrategyCommandsByStrategyGuidType(String guid, int strategyCommandBlock) {
        if (guid == null || "".equals(guid)){
            throw new IllegalArgumentException("策略id不存在");
        }

        Map<String ,String> map = new HashMap<String ,String>()  ;
        map.put("strategyId" , guid);
        map.put("commandType" , ""+strategyCommandBlock);

        List<StrategyCommand>  strategyCommands = strategyCommandMapper.findStrategyCommandsByStrategyGuidType(map);
        return strategyCommands;
    }

    @Override
    public  List<StrategyCommand>  findStrategyCommandBlockByStrategyGuid(String guid) {
        return findStrategyCommandsByStrategyGuidType(guid, ConstantsDto.STRATEGY_COMMAND_BLOCK);
    }

    @Override
    public  List<StrategyCommand>  findStrategyCommandPendingByStrategyGuid(String guid) {
        return findStrategyCommandsByStrategyGuidType(guid, ConstantsDto.STRATEGY_COMMAND_PENDING);
    }

    @Override
    public  List<StrategyCommand>  findStrategyCommandProhibitByStrategyGuid(String guid) {
        return findStrategyCommandsByStrategyGuidType(guid, ConstantsDto.STRATEGY_COMMAND_PROHIBIT);
    }
}
