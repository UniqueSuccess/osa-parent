package com.goldencis.osa.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.core.entity.HomeCounts;
import com.goldencis.osa.core.entity.LogSystem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统日志（操作、授权、审批）  服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-12-04
 */
public interface ILogSystemService extends IService<LogSystem> {
    /**
     * 获取操作日志分页接口
     */
    IPage<LogSystem> getLogSystemsInPage(Map<String, String> params);

    /**
     * 登录次数前5名
     * @return
     */
    List<LogSystem> getLoginTimesTop5();

    /**
     * 保存日志接口
     * @param log
     */
    void saveLog(LogSystem log);

    /**
     * 登录运维日志前20
     */
    List<LogSystem> getLoginOperationTop();

    /**
     * 日志清理
     * @param date
     */
    void logClean(String date);

    /**
     * 资源运维日志前5
     */
    List<LogSystem> getAssetOperationsTop5();

    /**
     * 获取首页数量
     * @return
     */
    HomeCounts getHomeCounts();
}
