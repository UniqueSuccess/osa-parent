package com.goldencis.osa.session.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.asset.entity.*;
import com.goldencis.osa.asset.resource.domain.Bs;
import com.goldencis.osa.asset.resource.domain.Linux;
import com.goldencis.osa.asset.resource.domain.Network;
import com.goldencis.osa.asset.resource.domain.Windows;
import com.goldencis.osa.asset.service.*;
import com.goldencis.osa.asset.sso.SSORuleParser;
import com.goldencis.osa.asset.sso.replace.IRulePlaceholder;
import com.goldencis.osa.asset.sso.replace.PlaceholderManager;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.*;
import com.goldencis.osa.core.aop.OsaSystemLog;
import com.goldencis.osa.core.entity.*;
import com.goldencis.osa.core.service.IDictionaryService;
import com.goldencis.osa.core.service.ILogSystemService;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.core.utils.SecurityUtils;
import com.goldencis.osa.session.domain.*;
import com.goldencis.osa.session.entity.TerminalSession;
import com.goldencis.osa.session.service.ITerminalSessionService;
import com.goldencis.osa.strategy.service.IStrategyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
@Api(tags = "会话管理")
@RestController
@RequestMapping("/session")
public class TerminalSessionController {

    private final Logger logger = LoggerFactory.getLogger(TerminalSessionController.class);

    @Autowired
    private ITerminalSessionService sessionService;

    @Autowired
    private IAssetService assetService;

    @Autowired
    private IAssetAccountService assetAccountService;

    @Autowired
    private IAssetTypeService assetTypeService;

    @Autowired
    private IStrategyService strategyService;

    @Autowired
    private IGrantedService grantedService;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ISsoRuleService ssoRuleService;
    @Autowired
    private SSORuleParser ssoRuleParser;
    @Autowired
    private IClientService clientService;
    @Autowired
    private ILogSystemService systemService;
    @Autowired
    private IUserService userService;
    @Autowired
    private PlaceholderManager placeFactory;
    @Autowired
    private IDictionaryService dictionaryService;

    @Value("${spring.redis.host}")
    private String serverIp;

    @Value("${goldencis.osa.service.session.block}")
    private String blockServiceUrl;

    @Value("${goldencis.osa.service.session.status}")
    private String statusServiceUrl;

    @Value("${goldencis.osa.service.session.query}")
    private String queryServiceUrl;

    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ApiOperation(value = "根据配置信息，生成完整的登录信息，生成token并以token为key存入redis中，设置过期时间，将token回传前端发起连接")
    @PostMapping(value = "/connectWithSsoConfiguration")
    public ResultMsg connectWithSsoConfiguration(HttpServletRequest request, Integer assetId, Integer accountId, String devunique, String configuration) {
        if (Objects.isNull(assetId) || Objects.isNull(accountId) || StringUtils.isEmpty(configuration) || StringUtils.isEmpty(devunique)) {
            return ResultMsg.False("未传递正确的参数！");
        }
        try {
            //校验登录时间是否合法
            User user = SecurityUtils.getCurrentUser();
            boolean flag = strategyService.isUserLoginTime(user.getUsername(), LocalDateTime.now().format(DF));
            if (!flag) {
                LogSystem log = new LogSystem(LogType.STRATEGY_LOGIN.getValue(), "策略-登录", "受限时间内登录", "TerminalSessionController.connectWithSsoConfiguration(..) invoke");
                systemService.saveLog(log);
                return ResultMsg.False("受限时间内无法登录！");
            }

            //获取设备
            Asset asset = assetService.getById(assetId);
            if (asset == null) {
                return ResultMsg.False("未找到对应的设备！");
            }

            //获取账户
            AssetAccount assetAccount = assetAccountService.getById(accountId);
            if (assetAccount == null || !assetAccount.getAssetId().equals(assetId)) {
                return ResultMsg.False("未找到对应的设备账户！");
            }
            //校验用户是否有该设备和账户的权限
            flag = grantedService.checkCurrentUser4AssetAccout(assetId, accountId);
            if (!flag) {
                return ResultMsg.False("当前登录用户未授权使用该设备或账户！");
            }

            //定义返回的登录信息
            JSON info;

            //待补充设备类型的校验

            //获取顶级设备类型
            AssetType superType = assetTypeService.getMostSuperiorAssetTypeById(asset.getType());
            AssetType selfType = assetTypeService.getById(asset.getType());
            if (superType == null || selfType == null) {
                return ResultMsg.False("未找到对应的设备类型！");
            }

            //由于当前BS设备没有Ip，暂时跳过资源Ip的校验
            if (!new Bs().typeId().equals(superType)) {
                //校验登录资源的Ip是否受限
                Platform platform = userService.getPlatformInfo();
                Assets resource = platform.getAccess().getResource();

                // ip黑名单
                if (blockByIp(asset.getIp(), resource.getIp())) {
                    LogSystem log = new LogSystem(LogType.STRATEGY_LOGIN.getValue(), "策略-登录", "受限IP登录", "TerminalSessionController.connectWithSsoConfiguration(..) invoke");
                    systemService.saveLog(log);
                    return ResultMsg.False("当前IP不允许登录系统！");
                }
            }

            //生成登录信息的唯一标示，
            String logonId = UUID.randomUUID().toString();

            SsoUnique unique = new SsoUnique();
            // 更新运维设备唯一标示
            unique.setDevUnique(devunique);
            //更新运维设备ip
            String ip = NetworkUtil.getIpAddress(request);
            unique.setIp(ip);
            //判断设备的类型，生成相应的登录信息
            if (new Linux().typeId().equals(superType.getId()) || new Network().typeId().equals(superType.getId())) {
                //获取设备准确类型
                asset.setTypeName(selfType.getName());

                //为SSH登录生成对应的登录信息
                info = sessionService.generatorLogonInfo4SSH(asset, assetAccount, configuration, superType, logonId);
                unique.setLoginFrom("CO");
            } else if (new Windows().typeId().equals(superType.getId())) {
                JSONObject confJson = JSONObject.parseObject(configuration);
                confJson.put("hostname", asset.getIp());
                info = confJson;
                unique.setLoginFrom("GA");
            } else if (sessionService.isRemoteApp(superType)) {
                info = connectRemoteApp(unique, logonId, request, asset, assetAccount, configuration, superType);
                unique.setLoginFrom("RA");
            } else {
                return ResultMsg.False("生成用户登录信息失败!");
            }

            if (info == null) {
                return ResultMsg.False("生成用户登录信息失败!");
            }

            //以logonId为key存入redis中
            redisUtil.set(ConstantsDto.REDIS_KEY_SESSION + logonId + ConstantsDto.REDIS_KEY_SSO_LOGON_INFO, info);

            //以logonId为key将user的信息存入redis中
            redisUtil.set(ConstantsDto.REDIS_KEY_SESSION + logonId + ConstantsDto.REDIS_KEY_SSO_USER_INFO, user);

            //以logonId为key将account的信息存入redis中
            assetAccount.setAssetName(asset.getName());
            assetAccount.setAssetIp(asset.getIp());
            redisUtil.set(ConstantsDto.REDIS_KEY_SESSION + logonId + ConstantsDto.REDIS_KEY_SSO_ACCOUNT_INFO, assetAccount);

            //以logonId为key将设备唯一标示devunique存入redis中
            redisUtil.set(ConstantsDto.REDIS_KEY_SESSION + logonId + ConstantsDto.REDIS_KEY_SSO_UNIQUE, unique);

            logger.info("[用户" + user.getName() + "，IP" + ip + "，单点登录]");
//            logger.info("[用户" + user.getName() + "登录设备，登录ID：" + logonId + "，设备类型：" + selfType.getName() + "，设备名称" + asset.getName() + "，设备IP：" + asset.getIp() + "，设备账号：" + assetAccount.getUsername() + "，登录方式：XX，登录工具：XX，登录界面：XX，协议：XX，端口：XX。]");
            logger.info("[用户" + user.getName() + "登录设备，登录ID：" + logonId + "，设备类型：" + selfType.getName() + "，设备名称" + asset.getName() + "，设备IP：" + asset.getIp() + "，设备账号：" + assetAccount.getUsername() + "，登录配置为:" + configuration + "]");
            return ResultMsg.ok(logonId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * 链接RemoteApp
     *
     *
     *
     * @param logonId
     * @param asset
     * @param assetAccount 账号信息
     * @param json 配置信息
     * @param superType
     * @return
     */
    private JSON connectRemoteApp(SsoUnique unique, String logonId, HttpServletRequest request, Asset asset, AssetAccount assetAccount, String json, AssetType superType) throws Exception {
        RemoteAppConfig config;
        try {
            config = JsonUtils.jsonToPojo(json, RemoteAppConfig.class);
        } catch (Exception e) {
            logger.error("json转换为RemoteAppConfig失败,json: " + json, e);
            throw e;
        }
        Objects.requireNonNull(config, "json转换为RemoteAppConfig失败,json: " + json);
        if (config.invalid()) {
            throw new Exception("remoteApp不能使用默认的配置,json : " + json);
        }
        RemoteAppInfo remoteAppInfo = new RemoteAppInfo();
        // 跳板机
        Asset publish = assetService.getById(config.getPublishId());
        // 跳板机的ip
        remoteAppInfo.setIp(publish.getIp());
        // 跳板机的账号
        remoteAppInfo.setAccount(dictionaryService.getDefaultJsAccount());
        // 跳板机的密码
        remoteAppInfo.setPassword(dictionaryService.getDefaultJsPassword());
        // 跳板机协议
        remoteAppInfo.setProtocol(config.getProtocol());
        // 跳板机的端口
        remoteAppInfo.setPort(config.getPort());
        // 配置运维用户的ip地址
        remoteAppInfo.setUserIp(NetworkUtil.getIpAddress(request));
        List<IRulePlaceholder> list = placeFactory.place(asset, assetAccount);

        SsoRule ssoRule = ssoRuleService.getById(config.getSsoRuleId());
        String rule = ssoRuleParser.placeHolder(ssoRule.getRule(), list);
        remoteAppInfo.setRule(rule);
        logger.debug("规则------------> {}", rule);
        // 将规则json存入redis中
        redisUtil.set(ConstantsDto.REDIS_KEY_SESSION + logonId + ConstantsDto.REDIS_KEY_RULE, ssoRuleParser.ruleToJson(rule));
        // 更新跳板机的唯一标示
        unique.setJsUnique(getPublishUnique(publish));
        return JSONObject.parseObject(JSON.toJSONString(remoteAppInfo));
    }

    /**
     * 获取应用程序发布器的唯一标示
     * @param publish 应用程序发布器
     * @return
     */
    private String getPublishUnique(Asset publish) {
        Client client = clientService.getOne(new QueryWrapper<Client>().eq("ip", publish.getIp()));
        return Objects.isNull(client) ? null : client.getGuid();
    }

    @ApiOperation("获取实时监控的分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "每页条数", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "searchStr", value = "用户名/IP模糊查询", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "isFinish", value = "是否结束(实时-0/历史-1)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "protocol", value = "协议类型筛选", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "isBlocking", value = "是否阻断筛选", paramType = "query", dataType = "String")
    })
    @GetMapping(value = "/getSessionsInPage")
    public ResultMsg getSessionsInPage(@RequestParam Map<String, String> params) {
        try {
            IPage<TerminalSession> page = sessionService.getSessionsInPage(params);

            if (!ListUtils.isEmpty(page.getRecords())) {
                //如果是实时监控的列表，格式化监控的持续时间。
                sessionService.formatSessionDuration(page.getRecords(), params);
            }

            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation("实时监控中的阻断接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "会话id", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "assetId", value = "设备id", paramType = "query", dataType = "Integer")
    })
    @PostMapping(value = "/blockSessionBySessionId")
    @OsaSystemLog(module = "实时监控阻断", template = "会话id：%s", args = "0", type = LogType.SYSTEM_MONITOR_REALTIME_BLOCK)
    public ResultMsg blockSessionBySessionId(String sessionId) {
        try {
            //获取需要关闭的session
            ResultMsg status = sessionService.invokeSessionService(sessionId, queryServiceUrl);
            if (!ConstantsDto.RESPONSE_SUCCESS.equals(status.getResultCode())) {
                return ResultMsg.False("该会话已经结束!");
            }

            //调用session服务进行阻断
            ResultMsg resultMsg = sessionService.invokeSessionService(sessionId, blockServiceUrl);
            if (!ConstantsDto.RESPONSE_SUCCESS.equals(resultMsg.getResultCode())) {
                return ResultMsg.False("调用服务阻断session失败!");
            }

            TerminalSession session = resultMsg.convertDataToPojo(TerminalSession.class);
            User user = SecurityUtils.getCurrentUser();
            logger.info("用户" + user.getName() + "在控制台发起阻断命令，会话id为" + sessionId + "，运维用户为：" + session.getUserName() + "，运维设备为：" + session.getAssetName() + "，IP：" + session.getAssetAddr() + "，设备账号为：" + session.getAssetAddr() + "，请求服务为：" + session.getLoginFrom());
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    /*@ApiOperation(value = "开启一个remoteApp的会话")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "sessionId", value = "会话id", paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "jsId", value = "跳板机设备的唯一标识", paramType = "query", dataType = "String")
    })
    @PostMapping(value = "/remoteapp/open")
    public ResultMsg openSessionForRemoteApp(@RequestBody RemoteAppOpenBody body) {
        String sessionId = body.getSessionid();
        String jsId = body.getUnique();
        if (StringUtils.isEmpty(sessionId) || StringUtils.isEmpty(jsId)) {
            return ResultMsg.error("参数错误 : " + body);
        }
        try {
            sessionService.openSessionForRemoteApp(sessionId, jsId);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }*/

    /*@ApiOperation(value = "关闭一个remoteApp的会话")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "sessionId", value = "会话id", paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "state", value = "状态(0:正常关闭,1:阻断)", paramType = "query", dataType = "String")
    })
    @PostMapping(value = "/remoteapp/close")
    public ResultMsg closeSessionForRemoteApp(@RequestBody RemoteAppCloseBody body) {
        String sessionId = body.getSessionid();
        if (StringUtils.isEmpty(sessionId)) {
            return ResultMsg.error("参数错误 : " + body);
        }
        try {
            sessionService.closeSessionForRemoteApp(sessionId, body.getState());
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }*/

    @ApiOperation(value = "实时会话",notes = "首页-实时会话")
    @GetMapping(value = "/getRealtimeSession")
    public ResultMsg getRealtimeSession(){
        try{
            IPage<TerminalSession> page = sessionService.getRealtimeSession();
            return ResultMsg.page(page);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    /**
     * 通过ip阻断用户登录
     *
     * @param ip   ip地址
     * @param list ip黑名单
     * @return 如果阻断, 返回true;否则,返回false;
     */
    private boolean blockByIp(String ip, List<String> list) {
        return IpUtil.isIpInAddressRange(ip, list);
    }
}
