package com.goldencis.osa.job;

import com.goldencis.osa.common.constants.PathConfig;
import com.goldencis.osa.common.utils.ContextUtil;
import com.goldencis.osa.common.utils.DateUtil;
import com.goldencis.osa.system.entity.BackUp;
import com.goldencis.osa.system.service.IBackUpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackUpJob {

    private IBackUpService backUpService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void backUp() {
        backUpService = (IBackUpService) ContextUtil.getBean("backUpServiceImpl");
        logger.debug("BackUpTask is running...");
        //开始执行备份
        String filePath = PathConfig.HOMEPATH + "/backup/auto" + System.currentTimeMillis() + ".sql";
        BackUp backUpDO = new BackUp();
        String name = "自动备份-" + DateUtil.getFormatDate("yyyyMMdd-HHmm");
        backUpDO.setName(name);
        backUpDO.setType("0");
        backUpDO.setMark("定时备份");
        backUpDO.setFilePath(filePath);
        backUpService.addBackUp(backUpDO);
    }
}
