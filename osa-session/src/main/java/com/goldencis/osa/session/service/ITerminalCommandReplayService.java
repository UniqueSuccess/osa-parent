package com.goldencis.osa.session.service;

import com.goldencis.osa.session.entity.TerminalCommandReplay;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-12-05
 */
public interface ITerminalCommandReplayService extends IService<TerminalCommandReplay> {

    /**
     * 根据sessionId获取命令的回放录信息
     * @param sessionId 会话id
     * @return
     */
    TerminalCommandReplay getCommandReplayBySessionId(String sessionId);
}
