package com.goldencis.osa.job;

import com.goldencis.osa.core.entity.QuartzJob;
import com.goldencis.osa.core.mapper.QuartzJobMapper;
import com.goldencis.osa.core.service.impl.TaskService;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 加载所有的job
 *
 * @author shigd
 */
@Component
public class LoadJob {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    QuartzJobMapper quartzJobMapper;

    @Autowired
    TaskService taskService;

    @PostConstruct
    public void init() throws SchedulerException {
        logger.info("LoadTask is started running ");

        List<QuartzJob> list = quartzJobMapper.selectJobList();

        for (QuartzJob temp : list) {
            taskService.addJob(temp);
        }
        logger.info("LoadTask init complete ");
    }
}
