package com.goldencis.osa.core.service.impl;

import com.goldencis.osa.core.entity.QuartzJob;
import com.goldencis.osa.core.factory.QuartzJobFactory;
import com.goldencis.osa.core.factory.QuartzJobFactoryDisallowConcurrentExecution;
import com.goldencis.osa.core.mapper.QuartzJobMapper;
import com.goldencis.osa.core.utils.TaskUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @author shigd
 */
@Service
public class TaskService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    final static String SCHEDULED_JOB = "scheduleJob";

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private QuartzJobMapper quartzJobMapper;

    /**
     * @param jobName
     * @param jobGroup
     * @return
     */
    public QuartzJob getJob(String jobName, String jobGroup) {
        QuartzJob job = null;
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        CronTrigger trigger;
        try {
            trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger != null) {
                job = new QuartzJob();
                job.setJobName(jobName);
                job.setJobGroup(jobGroup);
                job.setDescription("tigger:" + trigger.getKey());
                job.setNextTime(trigger.getNextFireTime());
                job.setPreviousTime(trigger.getPreviousFireTime());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                job.setJobStatus(triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    job.setCronEx(cronExpression);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return job;
    }

    /**
     * @return
     * @throws SchedulerException
     */
    public List<QuartzJob> getAllJobs() throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<QuartzJob> jobList = new ArrayList<>();
        for (JobKey jobKey : jobKeys) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                QuartzJob job = new QuartzJob();
                job.setJobName(jobKey.getName());
                job.setJobGroup(jobKey.getGroup());
                job.setDescription("tigger:" + trigger.getKey());

                job.setNextTime(trigger.getNextFireTime());
                job.setPreviousTime(trigger.getPreviousFireTime());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                job.setJobStatus(triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    job.setCronEx(cronExpression);
                }
                jobList.add(job);
            }
        }
        return jobList;
    }

    /**
     * @return
     * @throws SchedulerException
     */
    public List<QuartzJob> getRunningJob() throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
        List<QuartzJob> jobList = new ArrayList<QuartzJob>(executingJobs.size());
        for (JobExecutionContext executingJob : executingJobs) {
            QuartzJob job = new QuartzJob();
            JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            Trigger trigger = executingJob.getTrigger();
            job.setJobName(jobKey.getName());
            job.setJobGroup(jobKey.getGroup());
            job.setDescription("tigger:" + trigger.getKey());
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            job.setJobStatus(triggerState.name());
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                String cronExpression = cronTrigger.getCronExpression();
                job.setCronEx(cronExpression);
            }
            jobList.add(job);
        }
        return jobList;
    }

    public boolean addJob(QuartzJob job) throws SchedulerException {
        if (job == null || !QuartzJob.STATUS_RUNNING.equals(job.getJobStatus())) {
            return false;
        }
        if (!TaskUtils.isValidExpression(job.getCronEx())) {
            logger.error("corn is error " + job.getJobName() + "," + job.getJobGroup() + ")," + job.getCronEx());
            return false;
        } else {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (null == trigger) {
                Class<? extends Job> clazz = QuartzJob.IS_CONCURRENT == job.getConcurrent() ? QuartzJobFactory.class : QuartzJobFactoryDisallowConcurrentExecution.class;
                JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getJobName(), job.getJobGroup()).build();
                jobDetail.getJobDataMap().put(SCHEDULED_JOB, job);
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronEx());
                trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                scheduler.scheduleJob(jobDetail, trigger);
            } else {
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronEx());
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        }
        return true;
    }

    /**
     * pause job
     *
     * @param scheduleJob
     * @return
     */
    public boolean pauseJob(QuartzJob scheduleJob) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        try {
            scheduler.pauseJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * resume job
     *
     * @param scheduleJob
     * @return
     */
    public boolean resumeJob(QuartzJob scheduleJob) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        try {
            scheduler.resumeJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * delete job
     */
    public boolean deleteJob(QuartzJob scheduleJob) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        try {
            scheduler.deleteJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @param scheduleJob
     * @throws SchedulerException
     */
    public void updateCronExpression(QuartzJob scheduleJob,int flag) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronEx());
        if(flag == 1){
            scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        }
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        scheduler.rescheduleJob(triggerKey, trigger);
        quartzJobMapper.updateCron(scheduleJob);
    }
}