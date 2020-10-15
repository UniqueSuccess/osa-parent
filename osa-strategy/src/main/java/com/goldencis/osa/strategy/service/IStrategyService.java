package com.goldencis.osa.strategy.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.strategy.entity.ProtocolControl;
import com.goldencis.osa.strategy.entity.Strategy;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 策略表-定义策略基本信息  服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
public interface IStrategyService extends IService<Strategy> {
    /**
     * 自定义分页查询
     *
     * @param params 查询查询
     * @return 分页对象
     */
    IPage<Strategy> getStrategyInPage(Map<String, String> params);

    /**
     * 通过guid获取策略信息
     * @param guid
     * @return
     */
    Strategy findStrategyByGuid(String guid) throws Exception;

    /**
     * 新增策略
     * @param strategy
     */
    void saveStrategy(Strategy strategy) throws Exception;

    /**
     * 编辑策略
     * @param strategy
     */
    void editStrategy(Strategy strategy) throws Exception;

    /**
     * 通过guid删除策略信息
     */
    Strategy deleteStrategyByGuid(String guid) throws Exception;

    /**
     * 批量删除策略信息
     */
   String deleteStrategiesByGuids(List<String> strategyIds) throws Exception;

    /**
     * 获取所有策略
     */
    List<Strategy> getStrategyAll();

    /**
     * 通过用户名username、当前时间 确定用户是否可以登录
     * true: 可以登录， false：不可以登录
     * @param username：用户名
     * @param datetime：yyyy-MM-dd hh:mm:ss
     */
    boolean isUserLoginTime(String username, String datetime);
    

    /**
     * 根据用户名username、指令 确定是何种命令
     * @param username：用户名
     * @param inputCommand：输入命令
     */
    int getCommandTypeByUsername(String username, String inputCommand);

    /**
     * 获取协议控制
     * @param type 类型
     * @param guid 策略guid,策略中勾选的,checked为true
     * @return
     */
    List<ProtocolControl> getProtocolControl(String type, String guid);

    /**
     * 返回策略数据对象
     * @param guid
     * @return
     */
    JSONObject findPolicyByGuid(String guid);
}
