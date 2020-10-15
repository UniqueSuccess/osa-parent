package com.goldencis.osa.session.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.session.entity.TerminalCommandReplay;
import com.goldencis.osa.session.mapper.TerminalCommandReplayMapper;
import com.goldencis.osa.session.service.ITerminalCommandReplayService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-12-05
 */
@Service
public class TerminalCommandReplayServiceImpl extends ServiceImpl<TerminalCommandReplayMapper, TerminalCommandReplay> implements ITerminalCommandReplayService {

    @Override
    public TerminalCommandReplay getCommandReplayBySessionId(String sessionId) {
        return baseMapper.selectOne(new QueryWrapper<TerminalCommandReplay>().eq("session_id", sessionId));
    }
}
