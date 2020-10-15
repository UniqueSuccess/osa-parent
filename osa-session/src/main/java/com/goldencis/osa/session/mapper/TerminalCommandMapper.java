package com.goldencis.osa.session.mapper;

import com.goldencis.osa.session.entity.CommandEntity;
import com.goldencis.osa.session.entity.TerminalCommand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-11-09
 */
public interface TerminalCommandMapper extends BaseMapper<TerminalCommand> {

    List<TerminalCommand> queryCommandList(Map<String,String> params);

    int queryCommandCount(Map<String,String> params);

    int countCommandsInPage(Map<String, String> params);

    List<TerminalCommand> getCommandsInPage(Map<String, String> params);

    List<Map<String,Object>> getCommandReportChart(@Param("params") Map<String,String> params);

    long getCommandReportCount(@Param("params") Map<String,String> params);

    List<CommandEntity> getCommandReportList(@Param("params") Map<String,String> params);
}
