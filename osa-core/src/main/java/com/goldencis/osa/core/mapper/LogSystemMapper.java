package com.goldencis.osa.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.core.entity.LogSystem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-11-30
 */
public interface LogSystemMapper extends BaseMapper<LogSystem> {

    /**
     * 获取日志数量
     */
    int countLogSystemInPage(Map<String, Object> paramMap);

    /**
     * 获取日志
     */
    List<LogSystem> getLogSystemInPage(Map<String, Object> paramMap);

    /**
     * 日志清理
     * @param date
     */
    void logClean(@Param("date") String date);

    /**
     * 获取活跃账号Top5
     */
    List<LogSystem> getLoginTimesTop5(Map<String, Integer> paramMap);

    /**
     * 获取运维日志
     */
    List<LogSystem> getLoginOperationTop(Map<String, Integer> paramMap);

    /**
     * 获取资源运维次数Top5
     */
    List<LogSystem> getAssetOperationsTop5(Map<String, Integer> paramMap);

    /**
     * 获取用户总数
     */
    Integer getUserNums();

    /**
     * 获取设备总数
     */
    Integer getAssetNums();

    /**
     * 在线会话数量
     */
    Integer getSessionOnlineNums();

    /**
     * 已审批 数量
     */
    Integer getApprovedNums();

    /**
     * 待审批 数量
     */
    Integer getUnApprovedNums();
}
