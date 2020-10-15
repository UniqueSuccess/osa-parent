package com.goldencis.osa.session.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.entity.AssetType;
import com.goldencis.osa.asset.resource.domain.Bs;
import com.goldencis.osa.asset.resource.domain.Cs;
import com.goldencis.osa.asset.resource.domain.Database;
import com.goldencis.osa.asset.service.IAssetTypeService;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.constants.PathConfig;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.mq.MQClient;
import com.goldencis.osa.common.utils.*;
import com.goldencis.osa.core.entity.LogSystem;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.ILogSystemService;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.session.domain.RemoteAppCloseBody;
import com.goldencis.osa.session.domain.RemoteAppInfo;
import com.goldencis.osa.session.domain.StopRemoteApp;
import com.goldencis.osa.session.entity.TerminalSession;
import com.goldencis.osa.session.mapper.TerminalSessionMapper;
import com.goldencis.osa.session.provide.impl.AssetAccountProvider;
import com.goldencis.osa.session.provide.impl.RemoteAppInfoProvider;
import com.goldencis.osa.session.provide.impl.UserInfoProvider;
import com.goldencis.osa.session.service.ITerminalSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
@Service
public class TerminalSessionServiceImpl extends ServiceImpl<TerminalSessionMapper, TerminalSession> implements ITerminalSessionService {

    private final Logger logger = LoggerFactory.getLogger(TerminalSessionServiceImpl.class);
    @Autowired
    private TerminalSessionMapper sessionMapper;
    @Autowired
    private IAssetTypeService assetTypeService;
    @Autowired
    private UserInfoProvider userInfoProvider;
    @Autowired
    private AssetAccountProvider assetAccountProvider;
    @Autowired
    private RemoteAppInfoProvider remoteAppInfoProvider;
    @Autowired
    private MQClient mqClient;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ILogSystemService logSystemService;
    @Value("${spring.redis.host}")
    private String serverIp;
    @Value("${goldencis.osa.service.session.baseUrl}")
    private String sessionBaseUrl;

    @Override
    public JSON generatorLogonInfo4SSH(Asset asset, AssetAccount assetAccount, String configuration, AssetType superType, String logonId) {
        //解析配置信息
        JSONObject configJson = JSONObject.parseObject(configuration);

        //封装设备信息
        JSONObject assetInfo = new JSONObject();
        assetInfo.put("id", logonId);
        assetInfo.put("hostname", asset.getName());
        assetInfo.put("ip", asset.getIp());
        assetInfo.put("port", configJson.getIntValue("port"));
        assetInfo.put("protocol", configJson.getString("protocol"));
        assetInfo.put("logonTools", configJson.getString("logonTools"));
        assetInfo.put("system_users_join", asset.getAccount());
        assetInfo.put("pwd", assetAccount.getPassword());
        assetInfo.put("platform", superType.getName());
        assetInfo.put("os", asset.getTypeName());
        assetInfo.put("is_active", true);
        assetInfo.put("domain", "");
        assetInfo.put("comment", "");
        assetInfo.put("org_id", "");
        assetInfo.put("org_name", "DEFAULT");

        //封装账户信息
        JSONObject userInfo = new JSONObject();
        userInfo.put("id", logonId);
        userInfo.put("name", assetAccount.getUsername());
        userInfo.put("username", assetAccount.getUsername());
        userInfo.put("priority", 10);
        userInfo.put("protocol", configJson.getString("protocol"));
        userInfo.put("comment", "");
        userInfo.put("login_mode", "auto");

        //封装登录信息
        JSONArray userInfoArr = new JSONArray();
        userInfoArr.add(userInfo);

        assetInfo.put("system_users_granted", userInfoArr);
        JSONArray assetInfoArr = new JSONArray();
        assetInfoArr.add(assetInfo);

        return assetInfoArr;
    }

    @Override
    public IPage<TerminalSession> getSessionsInPage(Map<String, String> params) {
        Page page = new Page();

        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);

        //增加默认排序
        if (!params.containsKey("orderType")) {
            params.put("orderType", ConstantsDto.ORDER_TYPE_DESC);
        }
        if (!params.containsKey("orderColumn")) {
            params.put("orderColumn", "date_start");
        }

        //添加状态的查询条件
        QueryUtils.addQueryConditionBySplitString4Str(paramMap, "isBlocking", "isBlockingList", ";");

        //添加协议的查询条件
        QueryUtils.addQueryConditionBySplitString4Str(paramMap, "protocol", "protocolList", ";");

        //统计会话总数量
        int total = sessionMapper.countSessionsInPage(paramMap);
        //带参数的分页查询
        List<TerminalSession> sessionList = sessionMapper.getSessionsInPage(paramMap);

        page.setTotal(total);
        page.setRecords(sessionList);
        return page;
    }

    @Override
    public void setSessionFinishedBySessionId(String sessionId) {
        TerminalSession session = new TerminalSession();
        session.setId(sessionId);
        session.setIsFinished(true);
        sessionMapper.updateById(session);
    }

    @Override
    public void formatSessionDuration(List<TerminalSession> records, Map<String, String> params) {
        LocalDateTime now = LocalDateTime.now();

        records.forEach(session -> {
            LocalDateTime startTime = session.getDateStart();
            Duration duration;
            if (ConstantsDto.IS_FINISHED.equals(params.get("isFinish"))) {
                LocalDateTime dateTime = session.getDateEnd();
                //如果已经结束，但是没有结束时间，将当前时间作为结束时间
                if (dateTime == null) {
                    dateTime = now;
                    //通知服务更新会话结束时间
                    session.setDateEnd(now);
                    sessionMapper.updateById(session);
                }
                duration = Duration.between(startTime, dateTime);
            } else {
                duration = Duration.between(startTime, now);
            }
            long seconds = duration.getSeconds();
            String durationStr = DateUtil.formatSecondInPattern(seconds);
            session.setDuration(durationStr);
        });
    }

    @Override
    public boolean isRemoteApp(AssetType type) {
        AssetType superType = assetTypeService.getMostSuperiorAssetTypeById(type.getId());
        return superType.getId().equals(new Cs().typeId())
                || superType.getId().equals(new Database().typeId())
                || superType.getId().equals(new Bs().typeId());
    }

    @Override
    public void blockSessionByGuacamole(String sessionId) {
        Map<String, String> params = new HashMap<>();
        params.put("session", sessionId);
        HttpKit.sendGet(PathConfig.GUACAMOLE_BLOCK_URL, params);
    }

    @Override
    public void openSessionForRemoteApp(String sessionId, String jsId) throws Exception {
        String loginFrom = ConstantsDto.CONNECT_TYPE_REMOTEAPP;
        User userInfo = userInfoProvider.get(sessionId);
        if (Objects.isNull(userInfo)) {
            throw new Exception(String.format("从Redis获取用户信息失败,sessionId:%s;jumpServerId:%s", sessionId, jsId));
        }
        AssetAccount account = assetAccountProvider.get(sessionId);
        if (Objects.isNull(account)) {
            throw new Exception(String.format("从Redis获取设备账号信息失败,sessionId:%s;jumpServerId:%s", sessionId, jsId));
        }
        RemoteAppInfo remoteAppInfo = remoteAppInfoProvider.get(sessionId);
        if (Objects.isNull(remoteAppInfo)) {
            throw new Exception(String.format("从Redis获取登录信息失败,sessionId:%s;jumpServerId:%s", sessionId, jsId));
        }
        TerminalSession session = new TerminalSession();
        session.setId(sessionId);
        session.setUserUsername(userInfo.getUsername());
        session.setUserName(userInfo.getName());
        session.setRemoteAddr(remoteAppInfo.getUserIp());
        session.setUserGuid(userInfo.getGuid());
        session.setAssetName(account.getAssetName());
        session.setAssetAddr(account.getAssetIp());
        session.setAssetId(account.getAssetId());
        session.setSystemUser(account.getUsername());
        session.setLoginFrom(loginFrom);
        session.setIsFinished(false);
        session.setIsBlocking(false);
        session.setHasReplay(false);
        session.setHasCommand(false);
        session.setHasFile(false);
        LocalDateTime now = LocalDateTime.now();
        session.setDateStart(now);
        session.setDateLastActive(now);
        session.setProtocol(remoteAppInfo.getProtocol());
        session.setTerminalId(jsId);
        this.save(session);
        // 开启录屏
        publishMessageForPs(sessionId, ConstantsDto.PS_START);
        // 记录一条开始运维的日志
        String logOperateParam = "登录" + account.getAssetName();
        LogSystem log = new LogSystem(LogType.OPERATION_LOGININ.getValue(), "运维-登录", logOperateParam, "TerminalSessionServiceImpl.openSessionForRemoteApp()");
        log.setAssetId(account.getAssetId());
        log.setAssetName(account.getAssetName());
        log.setAssetIp(account.getAssetIp());
        logSystemService.saveLog(log);
    }

    @Override
    public void closeSessionForRemoteApp(String sessionId, int state) {
        TerminalSession session = this.getById(sessionId);
        Objects.requireNonNull(session, "会话信息不存在");
        if (state == RemoteAppCloseBody.STATE_BLOCK) {
            session.setIsBlocking(true);
        }
        session.setIsFinished(true);
        LocalDateTime now = LocalDateTime.now();
        session.setDateEnd(now);
        session.setDateLastActive(now);
        this.updateById(session);
        // 关闭录屏
        publishMessageForPs(sessionId, ConstantsDto.PS_STOP);
        // 记录一条结束运维的日志
        String logOperateParam = "登出" + session.getAssetName();
        LogSystem log = new LogSystem(LogType.OPERATION_LOGININ.getValue(), "运维-登出", logOperateParam, "TerminalSessionServiceImpl.closeSessionForRemoteApp()");
        log.setAssetId(session.getAssetId());
        log.setAssetName(session.getAssetName());
        log.setAssetIp(session.getAssetAddr());
        logSystemService.saveLog(log);
    }

    @Override
    public void blockSessionForRemoteApp(String sessionId) {
        TerminalSession session = this.getById(sessionId);
        if (Objects.isNull(session)) {
            return;
        }
        String terminalId = session.getTerminalId();
        boolean publish = mqClient.publish(terminalId, "stopremoteapp", JSON.toJSONString(new StopRemoteApp(sessionId)));
        logger.debug("推送关闭remoteApp的mq消息完成{}, 频道: {}; sessionid: {}", publish, terminalId, session);
    }

    /**
     * 首页--实时会话列表
     */
    @Override
    public IPage<TerminalSession> getRealtimeSession() {
        Page page = new Page();
        List<TerminalSession> sessionList = sessionMapper.selectList(new QueryWrapper<TerminalSession>().eq("is_finished",0));
        page.setTotal(sessionList.size());
        page.setRecords(sessionList);
        return page;
    }

    @Override
    public ResultMsg invokeSessionService(String sessionId, String serviceUrl) {
        HashMap<String, String> param = new HashMap<>();
        JSONObject json = new JSONObject();
        json.put("sessionId", sessionId);
        param.put("argv", json.toJSONString());
        String res = HttpClientUtil.doPost(sessionBaseUrl + serviceUrl, param);
        return ResultMsg.format(res);
    }

    /**
     * 发送一条录屏信息
     * @param session 会话id
     * @param state 状态
     */
    private void publishMessageForPs(String session, String state) {
        try {
            Object unique = redisUtil.get(ConstantsDto.REDIS_KEY_SESSION + session + ConstantsDto.REDIS_KEY_SSO_UNIQUE);
            Map uniqueMap = (Map)unique;
            String channel = (String) uniqueMap.get(ConstantsDto.REDIS_KEY_SSO_DEVUNIQUE);
            //生成对应的mq通知
            JSONObject news = new JSONObject();
            news.put("status", state);
            news.put("sessionId", session);
            //{"status":"stop","sessionId":"0d02d475-5ba9-4947-8b12-d584647c76f1"}
            mqClient.publish(channel, "sessionStatus", news.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("通知录屏失败，session" + session + " state:" + state, e);
        }
    }
}
