package com.goldencis.osa.report.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.report.constants.FileMonType;
import com.goldencis.osa.report.entity.LogClient;
import com.goldencis.osa.report.entity.LogClientFileType;
import com.goldencis.osa.report.mapper.LogClientMapper;
import com.goldencis.osa.report.service.ILogClientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangtt
 * @since 2019-01-28
 */
@Service
public class LogClientServiceImpl extends ServiceImpl<LogClientMapper, LogClient> implements ILogClientService {

    @Autowired
    LogClientMapper logClientMapper;

    /**
     * 分页获取文件操作列表
     */
    @Override
    public IPage<LogClient> getFileOpsInPage(Map<String, String> params) {

        Page page = new Page();

        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);
        //文件操作类型
        if (! Objects.isNull(paramMap.get("fileOpsType"))){
            //小类型（,分割）
            String logSmallType = String.valueOf(paramMap.get("fileOpsType"));
            if (! StringUtils.isEmpty(logSmallType)) {
                List<Integer> logSmallTypeLists = Arrays.stream(logSmallType.split(";"))
                        .filter(s -> !StringUtils.isEmpty(s))
                        .map(s -> Integer.parseInt(s))
                        .collect(Collectors.toList());
                //添加日志小类型
                paramMap.put("fileOpsTypeLists",logSmallTypeLists);
            }
        }

        int total = logClientMapper.countFileOpsInPage(paramMap);
        List<LogClient> strategies = logClientMapper.getFileOpsInPage(paramMap);
        page.setTotal(total);
        page.setRecords(strategies);
        return page;
    }

    @Override
    public List<LogClientFileType> getLogClientFileType(Map<String, String> params) {
        List<LogClientFileType> logClientFileTypeList = new ArrayList<>();
        for (FileMonType simpleEnum : FileMonType.values()) {
            //屏蔽0打开 7编辑 操作（资源管理器 审计不到）
            if (simpleEnum.getValue()!=0 && simpleEnum.getValue()!=7){
                logClientFileTypeList.add(new LogClientFileType(simpleEnum.getValue(),simpleEnum.getName(),0));
            }
        }
        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);

        List<LogClientFileType> logClientFileTypes = logClientMapper.getLogClientFileType(paramMap);
        if (! ListUtils.isEmpty(logClientFileTypes)){
            logClientFileTypeList.stream().forEach(logClientFileType -> logClientFileTypes.stream().filter(logClientFileType1 -> logClientFileType1.getOptype()!= null)
                    .forEach(logClientFileType2 -> {
                        if (logClientFileType.getOptype() == logClientFileType2.getOptype()){
                            logClientFileType.setOptNums(logClientFileType2.getOptNums());
                        }
                    }));
        }
        return logClientFileTypeList;
    }
}
