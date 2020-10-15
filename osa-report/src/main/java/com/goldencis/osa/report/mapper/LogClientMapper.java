package com.goldencis.osa.report.mapper;

import com.goldencis.osa.report.entity.LogClient;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.report.entity.LogClientFileType;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangtt
 * @since 2019-01-28
 */
public interface LogClientMapper extends BaseMapper<LogClient> {

    /**
     * 数量
     */
    int countFileOpsInPage(Map<String, Object> paramMap);

    /**
     * 分页数据
     */
    List<LogClient> getFileOpsInPage(Map<String, Object> paramMap);

    /**
     * 获取文件操作类型
     * @return
     * @param paramMap
     */
    List<LogClientFileType> getLogClientFileType(Map<String, Object> paramMap);
}
