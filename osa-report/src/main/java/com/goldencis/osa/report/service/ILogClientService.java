package com.goldencis.osa.report.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.report.entity.LogClient;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.report.entity.LogClientFileType;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangtt
 * @since 2019-01-28
 */
public interface ILogClientService extends IService<LogClient> {

    /**
     * 分页获取文件操作列表
     */
    IPage<LogClient> getFileOpsInPage(Map<String, String> params);

    /**
     * 获取 类型数量
     * @param params
     */
    List<LogClientFileType> getLogClientFileType(Map<String, String> params);
}
