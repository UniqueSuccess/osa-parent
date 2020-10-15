package com.goldencis.osa.system.mapper;

import com.goldencis.osa.system.entity.SystemSet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author shigd
 * @since 2018-12-27
 */
public interface SystemSetMapper extends BaseMapper<SystemSet> {
    /**
     * 根据类型查询告警通知配置信息
     */
    Map<String, Object> getPlatform(@Param("code") String code);
    /**
     * 保存系统设置信息
     */
    void addSystemSet(SystemSet systemSetDO);
    /**
     * 删除系统设置信息
     */
    void deleteSystemSet(@Param("code") String code);

    void updatePlatformJson(@Param(value = "json") String json);

    void updateLogServer(@Param(value = "content") String content);

    void updateMailConfig(@Param(value = "content") String mailConfig);
}
