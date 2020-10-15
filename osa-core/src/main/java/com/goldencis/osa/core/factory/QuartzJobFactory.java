package com.goldencis.osa.core.factory;

import com.goldencis.osa.core.entity.QuartzJob;
import com.goldencis.osa.core.utils.TaskUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 可同步
 *
 * @author shigd
 */
public class QuartzJobFactory implements Job {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        QuartzJob scheduleJob = (QuartzJob) context.getMergedJobDataMap().get("scheduleJob");
        logger.info("Running job name is " + scheduleJob.getJobName());
        TaskUtils.invokMethod(scheduleJob);
    }
}
