package com.goldencis.osa.report.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.asset.entity.ResourceEntity;

import java.util.List;
import java.util.Map;

public interface IResourceReportService {
    /**
     * 获取授权资源图表所需信息
     * @param params
     * @return
     */
    public List<Map<String, Object>> getResourceReportChart(Map<String, String> params);


    /**
     * 获取授权资源列表信息
     * @param params
     * @return
     */
    IPage<ResourceEntity> getResourceReportList(Map<String,String> params);
}
