package com.goldencis.osa.report.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.core.entity.UserReportEntity;

import java.util.List;
import java.util.Map;

public interface IUserReportService {
    /**
     * 获取授权用户图表所需信息
     * @param params
     * @return
     */
    public List<Map<String, Object>> getUserReportChart(Map<String, String> params);

    /**
     * 获取授权用户列表信息
     * @param params
     * @return
     */
    IPage<UserReportEntity> getUserReportList(Map<String,String> params);
}
