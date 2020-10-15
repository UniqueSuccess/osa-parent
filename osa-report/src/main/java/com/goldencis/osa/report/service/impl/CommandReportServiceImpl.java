package com.goldencis.osa.report.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.DateUtil;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.report.service.ICommandReportService;
import com.goldencis.osa.session.entity.CommandEntity;
import com.goldencis.osa.session.mapper.TerminalCommandMapper;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.*;

@Service
public class CommandReportServiceImpl implements ICommandReportService {

    @Autowired
    private TerminalCommandMapper terminalCommandMapper;

    @Override
    public Map<String, Object> getCommandReportChart(Map<String, String> params) {
        Map<String, Object> resMap = new HashMap<>();
        List<Map<String, Object>> chartList = new ArrayList<>();
        //时间参数处理
        String[] splitTime = params.get("dateTime").split("~");
        String startTime = splitTime[0] + ":00";
        String endTime = splitTime[1] + ":00";

        Map<String, Object> map = timeFormat(startTime, endTime);
        List<String> timeList = (List<String>)map.get("list");

        for (String time : timeList) {
            String[] split = time.split("~");
            long start = DateUtil.strToDate(split[0], DateUtil.FMT_DATE).getTime()/1000;
            long end = DateUtil.strToDate(split[1], DateUtil.FMT_DATE).getTime()/1000;
            params.put("startTime", start + "");
            params.put("endTime", end + "");
            List<Map<String, Object>> list = terminalCommandMapper.getCommandReportChart(params);
            String key = split[0] + "~" + split[1];
            Map<String, Object> tempMap = chartResultFormat(key, list);
            chartList.add(tempMap);
        }
        resMap.put("list", chartList);
        resMap.put("type", map.get("type"));
        return resMap;
    }

    private static Map<String, Object> timeFormat(String startTime, String endTime) {
        Map<String, Object> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        long[] distanceTimes = DateUtil.getDistanceTimes(startTime, endTime);
        String type = "";
        if (distanceTimes[0] == 0 && (distanceTimes[1] < 8 || (distanceTimes[1] == 8 && distanceTimes[2] == 0 && distanceTimes[3] == 0))) {
            type = "hour";
            //小时处理
            String start = startTime.substring(0, 13) + ":00:00";
            String end = endTime.substring(0, 13) + ":00:00";
            if (start.equals(end)) {
                list.add(startTime + "~" + endTime);
            } else {
                String hourTime = "";
                int i = 0;
                while (true) {
                    hourTime = DateUtil.getDateAddMinute(60, start, DateUtil.FMT_DATE);
                    if (i == 0) {
                        list.add(startTime + "~" + hourTime);
                    } else {
                        list.add(start + "~" + hourTime);
                    }
                    if (hourTime.equals(end)){
                        list.add(hourTime + "~" + endTime);
                        break;
                    }
                    start = hourTime;
                    i++;
                }
            }
        } else {
            type = "day";
            //天处理
            String start = startTime.substring(0, 11) + "00:00:00";
            String end = endTime.substring(0, 11) + "00:00:00";
            if (start.equals(end)) {
                list.add(startTime + "~" + endTime);
            } else {
                String hourTime = "";
                int i = 0;
                while (true) {
                    hourTime = DateUtil.getDateAddDay(1, start, DateUtil.FMT_DATE);
                    if (i == 0) {
                        list.add(startTime + "~" + hourTime);
                    } else {
                        list.add(start + "~" + hourTime);
                    }
                    if (hourTime.equals(end)){
                        list.add(hourTime + "~" + endTime);
                        break;
                    }
                    start = hourTime;
                    i++;
                }
            }
        }
        map.put("list", list);
        map.put("type", type);
        return map;
    }

    private Map<String, Object> chartResultFormat(String key, List<Map<String, Object>> result) {
        long approve = 0, forbid = 0, block = 0,invalid = 0;
        if (!ListUtils.isEmpty(result)) {
            for (Map<String, Object> map : result) {
                String status = map.get("status").toString();
                int num = Integer.parseInt(map.get("num").toString());
                switch (status) {
                    case "3":
                        forbid = num;
                        break;
                    case "4":
                        block = num;
                        break;
                    case "5":
                        invalid = num;
                        break;
                    default:
                        approve += num;
                        break;
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("approve", approve);
        map.put("forbid", forbid);
        map.put("block", block);
        map.put("invalid", invalid);
        return map;
    }

    @Override
    public IPage<CommandEntity> getCommandReportList(Map<String, String> params) {
        IPage<CommandEntity> page = new Page<>();
        //参数处理
        String[] split = params.get("dateTime").split("~");
        String orderType = params.get("orderType");
        String orderColumn = params.get("orderColumn");

        long start = DateUtil.strToDate(split[0] + ":00", DateUtil.FMT_DATE).getTime()/1000;
        long end = DateUtil.strToDate(split[1] + ":00", DateUtil.FMT_DATE).getTime()/1000;
        params.put("startTime", start + "");
        params.put("endTime", end + "");
        params.put("orderType", StringUtils.isEmpty(orderType) ? "desc" : orderType);
        params.put("orderColumn", StringUtils.isEmpty(orderColumn) ? "timestamp" : orderColumn);
        if (params.containsKey("status") && !StringUtils.isEmpty(params.get("status"))){
            params.put("status", params.get("status").replace(";", ","));
        }
        long count = terminalCommandMapper.getCommandReportCount(params);
        List<CommandEntity> list =  terminalCommandMapper.getCommandReportList(params);
        page.setTotal(count);
        page.setRecords(listFormat(list));
        return page;
    }

    private List<CommandEntity> listFormat(List<CommandEntity> list) {
        List<CommandEntity> resList = new ArrayList<>();
        for (CommandEntity map : list) {
            CommandEntity resMap = new CommandEntity();
            resMap.setInput(StringUtils.isEmpty(map.getInput()) ? "--" : map.getInput());
            resMap.setStatus(StringUtils.isEmpty(map.getStatus()) ? "--" : map.getStatus());
            resMap.setTimestamp(StringUtils.isEmpty(map.getTimestamp()) ? "--" : map.getTimestamp());
            resMap.setAsset(StringUtils.isEmpty(map.getAsset()) ? "--" : map.getAsset());
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
