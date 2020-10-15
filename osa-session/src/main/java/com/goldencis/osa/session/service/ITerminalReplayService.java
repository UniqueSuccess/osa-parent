package com.goldencis.osa.session.service;

import com.goldencis.osa.common.entity.ResultPlay;
import com.goldencis.osa.session.entity.TerminalCommandReplay;
import com.goldencis.osa.session.entity.TerminalReplay;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
public interface ITerminalReplayService extends IService<TerminalReplay> {

    /**
     * 生成包含播放列表地址，开始和结束时间的返回实体
     * @param sessionId 会话id
     * @return
     */
    ResultPlay generatorPlayList(String sessionId);
}
