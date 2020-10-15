package com.goldencis.osa.session.mapper;

import com.goldencis.osa.session.entity.TerminalSession;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
public interface TerminalSessionMapper extends BaseMapper<TerminalSession> {

    List<TerminalSession> getMonitorList(Map<String, String> params);

    int getMonitorCount(Map<String, String> params);

    List<TerminalSession> getSessionsInPage(Map<String, Object> paramMap);

    int countSessionsInPage(Map<String, Object> paramMap);
}
