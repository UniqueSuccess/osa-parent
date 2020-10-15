package com.goldencis.osa.core.utils;

import com.goldencis.osa.common.utils.ContextUtil;
import com.goldencis.osa.core.entity.QuartzJob;
import org.apache.commons.lang.StringUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;


/**
 * 通过反射注入类,执行job
 * 一开始spring 并不能注入job中,注释的代码是需要注入的mapper层或者service层到job具体类
 * 可以在job类中获取spring中的.就ok了
 *
 * @author shigd
 */
public class TaskUtils {
    private final static Logger logger = LoggerFactory.getLogger(TaskUtils.class);

    public static void invokMethod(QuartzJob scheduleJob) {
        Object object = null;
        Class<?> clazz = null;
       /* Object objectI = null;
        Class<?> clazzI = null;
        Class<?> arClazz[]=new Class<?>[1];*/
        if (StringUtils.isNotBlank(scheduleJob.getBeanId())) {
            object = ContextUtil.getBean(scheduleJob.getBeanId());
        } else if (StringUtils.isNotBlank(scheduleJob.getJobClass())) {
            try {
                clazz = Class.forName(scheduleJob.getJobClass());
                object = clazz.newInstance();

             /*   clazzI = Class.forName(scheduleJob.getInversionClass());
                objectI = clazzI.newInstance();
                arClazz[0] = clazzI;*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (object == null) {
            logger.error(" Not found job class" + scheduleJob.getJobName() + "!  Instance failed");
            return;
        }
      /*  if (objectI == null) {
            logger.error(" Not found inversion class" + scheduleJob.getJobName() + "!  Instance failed");
         return;
        }    */
        clazz = object.getClass();
        Method method = null;
        //Method methodI = null;
        try {
            method = clazz.getDeclaredMethod(scheduleJob.getMethodName());
            // methodI = clazz.getDeclaredMethod(scheduleJob.getInversionMethodName(),arClazz);
        } catch (NoSuchMethodException e) {
            logger.error("Task name = [" + scheduleJob.getJobName() + "] started failed! checked method name");
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (method != null) {
            try {
                //methodI.invoke(object,objectI);
                method.invoke(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * check cron expression
     *
     * @param cronExpression
     * @return
     */
    public static boolean isValidExpression(final String cronExpression) {
        CronTriggerImpl trigger = new CronTriggerImpl();
        try {
            trigger.setCronExpression(cronExpression);
            Date date = trigger.computeFirstFireTime(null);
            return date != null && date.after(new Date());
        } catch (ParseException e) {
        }
        return false;
    }
}
