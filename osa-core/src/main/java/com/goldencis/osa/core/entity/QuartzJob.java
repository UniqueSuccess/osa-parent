package com.goldencis.osa.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 定时任务信息表
 * </p>
 *
 * @author shigd
 * @since 2018-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_quartz_job")
public class QuartzJob extends Model<QuartzJob> {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_RUNNING = "1";
    public static final String STATUS_NOT_RUNNING = "0";
    public static final int IS_CONCURRENT = 1;
    public static final int NOT_CONCURRENT = 0;

    /**
     * 任务id
     */
    @TableId(value = "job_id", type = IdType.AUTO)
    private String jobId;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * 任务状态0未开启
     */
    private String jobStatus;

    /**
     * 是否并发0不是
     */
    private Integer concurrent;

    private String cronEx;

    /**
     * 描述
     */
    private String description;

    /**
     * spring中id如果没有根据job_class查找
     */
    private String beanId;

    /**
     * 包名加类名
     */
    private String jobClass;

    private String methodName;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 上次执行时间
     */
    private Date previousTime;

    /**
     * 下次时间
     */
    private Date nextTime;

    private String inversionMethodName;

    private String inversionClass;


    @Override
    protected Serializable pkVal() {
        return this.jobId;
    }

}
