package com.goldencis.osa.strategy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.strategy.entity.StrategyLogintime;
import com.goldencis.osa.strategy.mapper.StrategyLogintimeMapper;
import com.goldencis.osa.strategy.service.IStrategyLogintimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 策略登录时间表-定义策略登录时间基本信息 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Service
public class StrategyLogintimeServiceImpl extends ServiceImpl<StrategyLogintimeMapper, StrategyLogintime> implements IStrategyLogintimeService {

    @Autowired
    StrategyLogintimeMapper strategyLogintimeMapper;

    @Override
    public List<StrategyLogintime> findStrategyLogintimesByStrategyGuid(String guid){
        if (guid == null || "".equals(guid)){
            throw new IllegalArgumentException("策略id不存在");
        }
        List<StrategyLogintime> strategyLogintimes = strategyLogintimeMapper.findStrategyLogintimesByStrategyGuid(guid);
        return strategyLogintimes;
    }
}
