package com.goldencis.osa.session.controller;

import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.constants.PathConfig;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.entity.ResultPlay;
import com.goldencis.osa.common.utils.FileDownLoad;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.core.aop.OsaSystemLog;
import com.goldencis.osa.session.entity.TerminalCommandReplay;
import com.goldencis.osa.session.service.ITerminalCommandReplayService;
import com.goldencis.osa.session.service.ITerminalReplayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
@Api(tags = "回放录像控制器")
@RestController
@RequestMapping
public class TerminalReplayController {

    @Autowired
    private ITerminalReplayService replayService;

    @Autowired
    private ITerminalCommandReplayService commandReplayService;

    @ApiOperation(value = "获取回放录像的接口,由于推流无法调动时间轴，改为nginx直接获取资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "month", value = "年月份", paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "filename", value = "文件名", paramType = "path", dataType = "String"),
    })
    @GetMapping(value = "/storage/attachment/replay/{month}/{filename}")
    public void replay(@PathVariable(value = "month") String month, @PathVariable(value = "filename") String filename, HttpServletRequest request, HttpServletResponse response) {
        try {
            String filepath = PathConfig.SESSION_REPLAY_ROOTPATH + PathConfig.SESSION_REPLAY_DIRPATH + File.separator + month + File.separator + filename;
            File file = new File(filepath);
            if (!file.exists()) {
                return;
            }

            FileDownLoad downLoad = new FileDownLoad();
            downLoad.download(response, request, file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "根据sessionId获取回放录像列表的接口")
    @ApiImplicitParam(name = "sessionId", value = "会话id", paramType = "query", dataType = "String")
    @GetMapping(value = "/replay/getReplayListBySessionId")
    @OsaSystemLog(module = "历史监控回放", template = "会话id：%s", args = "0", type = LogType.SYSTEM_MONITOR_HISTORY_VIEW)
    public ResultPlay getReplayListBySessionId(String sessionId) {
        try {
            if (StringUtils.isEmpty(sessionId)) {
                return ResultPlay.False("sessionId不能为空!");
            }

            //生成包含播放列表地址，开始和结束时间的返回实体
            ResultPlay resultPlay  = replayService.generatorPlayList(sessionId);

            return resultPlay;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultPlay.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "根据sessionId获取命令的回放录信息")
    @ApiImplicitParam(name = "sessionId", value = "会话id", paramType = "query", dataType = "String")
    @GetMapping(value = "/replay/getCommandReplayBySessionId")
    @OsaSystemLog(module = "历史监控回放", template = "会话id：%s", args = "0", type = LogType.SYSTEM_MONITOR_HISTORY_VIEW)
    public ResultMsg getCommandReplayBySessionId(String sessionId) {
        try {
            if (StringUtils.isEmpty(sessionId)) {
                return ResultMsg.False("sessionId不能为空!");
            }

            //根据sessionId获取命令的回放录信息
            TerminalCommandReplay replay  = commandReplayService.getCommandReplayBySessionId(sessionId);
            if (replay == null) {
                return ResultMsg.False("sessionId没有对应的命令回放!");
            }

            return ResultMsg.ok(replay);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }
}
