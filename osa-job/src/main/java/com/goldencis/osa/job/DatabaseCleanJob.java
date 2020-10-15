package com.goldencis.osa.job;

import com.goldencis.osa.common.utils.ContextUtil;
import com.goldencis.osa.common.utils.DateUtil;
import com.goldencis.osa.core.service.ILogSystemService;
import com.goldencis.osa.system.service.ISystemSetService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class DatabaseCleanJob {

    private ISystemSetService systemSetService;

    private ILogSystemService logSystemService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void clean() {
        systemSetService = (ISystemSetService) ContextUtil.getBean("systemSetServiceImpl");
        logSystemService = (ILogSystemService) ContextUtil.getBean("logSystemServiceImpl");
        logger.debug("DatabaseCleanJob is running...");
        //获取配置信息
        Map<String, Object> data = systemSetService.getPlatform("DataBaseClean");
        String content = String.valueOf(data.get("content"));
        JSONObject json = JSONObject.fromObject(content);
        String status = json.get("status").toString();
        if (status.equals("1")){
            int day = Integer.parseInt("-" + json.get("day").toString());
            String formatDate = DateUtil.getFormatDate(DateUtil.FMT_DAY + " 00:00:00");
            String date = DateUtil.getDateAddDay(day, formatDate, DateUtil.FMT_DATE);
            //清理系统日志
            logSystemService.logClean(date);
        }
    }
}
