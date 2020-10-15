package com.goldencis.osa.session.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.common.constants.PathConfig;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.entity.ResultPlay;
import com.goldencis.osa.common.utils.HttpKit;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.session.entity.TerminalReplay;
import com.goldencis.osa.session.mapper.TerminalReplayMapper;
import com.goldencis.osa.session.service.ITerminalReplayService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
@Service
public class TerminalReplayServiceImpl extends ServiceImpl<TerminalReplayMapper, TerminalReplay> implements ITerminalReplayService {

    @Autowired
    private TerminalReplayMapper replayMapper;

    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final String SUBFIX = ".mp4";

    public static final String GENERATOR_REPLAY_URL = "http://127.0.0.1:8001/replay/generatorReplayFileBySessionId";

    @Override
    public ResultPlay generatorPlayList(String sessionId) {
        Integer start = 0;
        Integer end = 0;
        String playListPath = null;
        File file = null;

        List<TerminalReplay> list = replayMapper.selectList(new QueryWrapper<TerminalReplay>().eq("session_id", sessionId).orderByAsc("seq"));

        if (ListUtils.isEmpty(list)) {
            return ResultPlay.False("该会话无视频回放");
        }

        for (int i = 0; i < list.size(); i++) {
            TerminalReplay replay = list.get(i);
            String filename = replay.getFilename();
            if (i == 0) {
                String filepath = replay.getFilepath();
                playListPath = filepath.replaceAll(filename, sessionId + SUBFIX);
                file = new File(PathConfig.SESSION_REPLAY_ROOTPATH + playListPath);
            }

            //给开始时间赋值
            if (i == 0) {
                start = replay.getSessionStart();
            }
            //累加结束时间
            end += replay.getSessionEnd();

            if (!file.exists()) {
                //如果文件不存在，请求附件服务器生成一个该session对应的回访文件。

                //接口地址:/replay/generatorReplayFileBySessionId
                //POST 8001
                Map<String, String> params = new HashMap<>();
                params.put("sessionId", sessionId);
                String res = HttpKit.sendPost(GENERATOR_REPLAY_URL, params);
                ResultMsg msg = ResultMsg.format(res);
                if (!ResultMsg.RESPONSE_TRUE.equals(msg.getResultCode())) {
                    return ResultPlay.False("生成回访视频失败!");
                }
            }
        }
        return ResultPlay.ok(start, end, playListPath);
    }
}