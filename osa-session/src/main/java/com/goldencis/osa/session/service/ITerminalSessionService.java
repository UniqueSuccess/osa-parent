package com.goldencis.osa.session.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.entity.AssetType;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.session.entity.TerminalSession;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
public interface ITerminalSessionService extends IService<TerminalSession> {

    /**
     * 为SSH登录生成对应的登录信息
     * @param asset 设备
     * @param assetAccount 设备账户
     * @param configuration 单点登录配置信息
     * @param superType
     * @param logonId
     * @return 登录信息
     */
    JSON generatorLogonInfo4SSH(Asset asset, AssetAccount assetAccount, String configuration, AssetType superType, String logonId);

    IPage<TerminalSession> getSessionsInPage(Map<String, String> params);

    /**
     *根据sessionId将session的状态设为完成
     * @param sessionId
     */
    void setSessionFinishedBySessionId(String sessionId);

    /**
     * 如果是实时监控的列表，格式化监控的持续时间。
     * @param records 实时监控的集合
     * @param params
     */
    void formatSessionDuration(List<TerminalSession> records, Map<String, String> params);

    /**
     * 通过设备类型判断是否remoteApp
     * @param type 设备类型
     * @return
     */
    boolean isRemoteApp(AssetType type);

    /**
     * 调用guacamole的接口，阻断由GA发起的会话
     * @param sessionId 需要阻断的sesssionId
     */
    void blockSessionByGuacamole(String sessionId);

    /**
     * 将会话信息保存到数据库
     * @param sessionId 会话id
     * @param jsId 跳板机标识
     */
    void openSessionForRemoteApp(String sessionId, String jsId) throws Exception;

    void closeSessionForRemoteApp(String sessionId, int state);

    void blockSessionForRemoteApp(String sessionId);

    /**
     * 首页--实时会话列表
     */
    IPage<TerminalSession> getRealtimeSession();

    /**
     * 调用session模块的服务
     * @param sessionId 会话的唯一标示
     * @param serviceUrl 服务地址
     * @return 接口响应
     */
    ResultMsg invokeSessionService(String sessionId, String serviceUrl);
}
