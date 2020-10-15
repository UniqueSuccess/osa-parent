package com.goldencis.osa.report.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.session.entity.CommandEntity;

import java.util.List;
import java.util.Map;

public interface ICommandReportService {
    /**
     * 获取违规命令图表所需信息
     * @param params
     * @return
     */
    public Map<String, Object> getCommandReportChart(Map<String, String> params);


    /**
     * 获取违规命令列表信息
     * @param params
     * @return
     */
    IPage<CommandEntity> getCommandReportList(Map<String, String> params);
}
