package com.goldencis.osa.report.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.core.entity.UserReportEntity;
import com.goldencis.osa.core.mapper.UserMapper;
import com.goldencis.osa.report.service.IUserReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserReportServiceImpl implements IUserReportService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Map<String, Object>> getUserReportChart(Map<String, String> params) {
        List<Map<String, Object>> list = userMapper.getUserReportChart(params);
        return list;
    }

    @Override
    public IPage<UserReportEntity> getUserReportList(Map<String, String> params) {
        IPage<UserReportEntity> page = new Page<>();
        long count = userMapper.getUserReportCount(params);
        List<UserReportEntity> list = userMapper.getUserReportList(params);
        page.setTotal(count);
        page.setRecords(listFormat(list));
        return page;
    }

    private List<UserReportEntity> listFormat(List<UserReportEntity> list) {
        List<UserReportEntity> resList = new ArrayList<>();
        for (UserReportEntity map : list) {
            UserReportEntity resMap = new UserReportEntity();
            resMap.setAccountId(StringUtils.isEmpty(map.getAccountId()) ? "--" : map.getAccountId());
            resMap.setGuid(StringUtils.isEmpty(map.getGuid()) ? "--" : map.getGuid());
            resMap.setCreateTime(StringUtils.isEmpty(map.getCreateTime()) ? "--" : map.getCreateTime());
            resMap.setAssetname(StringUtils.isEmpty(map.getAssetname()) ? "--" : map.getAssetname());
            resMap.setTypename(StringUtils.isEmpty(map.getTypename()) ? "--" : map.getTypename());
            resMap.setIp(StringUtils.isEmpty(map.getIp()) ? "--" : map.getIp());
            resMap.setAccount(StringUtils.isEmpty(map.getAccount()) ? "--" : map.getAccount());
            resMap.setAssetId(StringUtils.isEmpty(map.getAssetId()) ? "--" : map.getAssetId());
            resMap.setUsername(StringUtils.isEmpty(map.getUsername()) ? "--" : map.getUsername());
            resMap.setTruename(StringUtils.isEmpty(map.getTruename()) ? "--" : map.getTruename());
            resMap.setGroupname(StringUtils.isEmpty(map.getGroupname()) ? "--" : map.getGroupname());
            resMap.setStrategy(StringUtils.isEmpty(map.getStrategy()) ? "--" : map.getStrategy());
            resMap.setAssetGroupname(StringUtils.isEmpty(map.getAssetGroupname()) ? "--" : map.getAssetGroupname());
            resList.add(resMap);
        }
        return resList;
    }
}
