package com.goldencis.osa.report.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goldencis.osa.asset.entity.ResourceEntity;
import com.goldencis.osa.asset.mapper.AssetMapper;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.report.service.IResourceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResourceReportServiceImpl implements IResourceReportService {

    @Autowired
    private AssetMapper assetMapper;

    @Override
    public List<Map<String, Object>> getResourceReportChart(Map<String, String> params) {
        List<Map<String, Object>> list = assetMapper.getResourceReportChart(params);
        return list;
    }

    @Override
    public IPage<ResourceEntity> getResourceReportList(Map<String, String> params) {
        IPage<ResourceEntity> page = new Page<>();
        long count = assetMapper.getResourceReportCount(params);
        List<ResourceEntity> list = assetMapper.getResourceReportList(params);
        page.setTotal(count);
        page.setRecords(listFormat(list));
        return page;
    }

    private List<ResourceEntity> listFormat(List<ResourceEntity> list) {
        List<ResourceEntity> resList = new ArrayList<>();
        for (ResourceEntity map : list) {
            ResourceEntity resMap = new ResourceEntity();
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
            resList.add(resMap);
        }
        return resList;
    }
}
