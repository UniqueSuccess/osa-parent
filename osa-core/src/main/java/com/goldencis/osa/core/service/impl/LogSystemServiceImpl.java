package com.goldencis.osa.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.HttpKit;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.common.utils.NetworkUtil;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.core.entity.HomeCounts;
import com.goldencis.osa.core.entity.LogSystem;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.mapper.LogSystemMapper;
import com.goldencis.osa.core.service.ILogSystemService;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.core.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统日志（操作、授权、审批）  服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-12-04
 */
@Service
public class LogSystemServiceImpl extends ServiceImpl<LogSystemMapper, LogSystem> implements ILogSystemService {

    @Autowired
    LogSystemMapper logSystemMapper;

    /**
     * 获取操作日志分页接口
     */
    @Override
    @Transactional(readOnly = true)
    public IPage<LogSystem> getLogSystemsInPage(Map<String, String> params) {
        Page page  =  new Page();
        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String,Object> paramMap = QueryUtils.formatPageParams(params);
        if (Objects.isNull(paramMap.get("logBigType"))){
            throw new IllegalArgumentException("系统日志类型为空");
        }

        List<Integer> logTypeLists = new ArrayList<>();
        int logType = Integer.parseInt(String.valueOf(paramMap.get("logBigType")));
        if (ConstantsDto.LOG_SYSTEM_OPERATE == logType){
            //操作
            logTypeLists = LogType.getAllSystemOperateLogType().stream().map(LogType::getValue).collect(Collectors.toList());
        } else if (ConstantsDto.LOG_SYSTEM_GRANTED == logType){
            //授权
            logTypeLists = LogType.getAllGrantedLogType().stream().map(LogType::getValue).collect(Collectors.toList());
        } else if (ConstantsDto.LOG_SYSTEM_APPROVAL == logType){
            //审批
            logTypeLists = LogType.getAllApprovalLogType().stream().map(LogType::getValue).collect(Collectors.toList());
        } else if (ConstantsDto.LOG_OPERATION_STRATEGY == logType) {
            logTypeLists = LogType.getAllStrategyLogType().stream().map(LogType::getValue).collect(Collectors.toList());
        } else if (ConstantsDto.LOG_OPERATION_OPERATE == logType) {
            logTypeLists = LogType.getAllOperationLogType().stream().map(LogType::getValue).collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("日志类型不正确");
        }
        //添加日志大类型
        paramMap.put("logBigTypeLists",logTypeLists);

        //小类型
        if (! Objects.isNull(paramMap.get("logSmallType"))){
            //小类型（,分割）
            String logSmallType = String.valueOf(paramMap.get("logSmallType"));
            if (! StringUtils.isEmpty(logSmallType)) {
                List<Integer> logSmallTypeLists = Arrays.stream(logSmallType.split(";"))
                        .filter(s -> !StringUtils.isEmpty(s))
                        .map(s -> Integer.parseInt(s))
                        .collect(Collectors.toList());
                //添加日志小类型
                paramMap.put("logSmallTypeLists",logSmallTypeLists);
            }
        }

        //统计日志总数量
        int total = logSystemMapper.countLogSystemInPage(paramMap);

        //带参数的分页查询
        List<LogSystem> logSystemList = logSystemMapper.getLogSystemInPage(paramMap);
        page.setTotal(total);
        page.setRecords(logSystemList);
        return page;
    }

    @Override
    public void saveLog(LogSystem log) {
        log.setTime(LocalDateTime.now());
        String ip = NetworkUtil.getIpAddress(HttpKit.getRequest());
        log.setIp(ip);
        User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            return;
        }
        String userUserName = user.getUsername();
        String userName = user.getName();
        log.setUserId(user.getGuid());
        log.setUserUsername(userUserName);
        log.setUserName(userName);

        logSystemMapper.insert(log);
    }

    @Override
    public void logClean(String date) {
        logSystemMapper.logClean(date);
    }

    //region-- -------------------------首页接口-----start-----------------------------------------------------
    /**
     * 活跃账号TOP5
     * @return
     */
    @Override
    public List<LogSystem> getLoginTimesTop5() {
        Map<String,Integer> paramMap = new HashMap<>();
        paramMap.put("loginType", LogType.OPERATION_LOGININ.getValue());
        //带参数的分页查询
        List<LogSystem> logSystemList = logSystemMapper.getLoginTimesTop5(paramMap);
        return logSystemList;
    }


    /**
     * 运维记录 TOP20
     */
    @Override
    public List<LogSystem> getLoginOperationTop() {
        Map<String,Integer> paramMap = new HashMap<>();
        paramMap.put("loginType", LogType.OPERATION_LOGININ.getValue());
        List<LogSystem> logSystemList = logSystemMapper.getLoginOperationTop(paramMap);
        return logSystemList;
    }

    /**
     * 资源运维TOP5
     */
    @Override
    public List<LogSystem> getAssetOperationsTop5() {
        Map<String,Integer> paramMap = new HashMap<>();
        paramMap.put("loginType", LogType.OPERATION_LOGININ.getValue());
        List<LogSystem> logSystemList = logSystemMapper.getAssetOperationsTop5(paramMap);
        return logSystemList;
    }

    /**
     * 获取首页数量统计
     * @return
     */
    @Override
    public HomeCounts getHomeCounts() {
        HomeCounts homeCounts = new HomeCounts();
        homeCounts.setUserNums(logSystemMapper.getUserNums());
        homeCounts.setUserOnlineNums(0);
        homeCounts.setAssetNums(logSystemMapper.getAssetNums());
        homeCounts.setSessionOnlineNums(logSystemMapper.getSessionOnlineNums());
        homeCounts.setApprovedNums(logSystemMapper.getApprovedNums());
        homeCounts.setUnApprovedNums(logSystemMapper.getUnApprovedNums());
        return homeCounts;
    }
    //region---------------------------首页接口-----end-----------------------------------------------------
}
