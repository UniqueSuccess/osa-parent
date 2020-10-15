package com.goldencis.osa.core.mapper;

import com.goldencis.osa.core.entity.QuartzJob;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 定时任务信息表 Mapper 接口
 * </p>
 *
 * @author shigd
 * @since 2018-12-28
 */
public interface QuartzJobMapper extends BaseMapper<QuartzJob> {

    /***
     * 查询所有job
     * *****/
    List<QuartzJob> selectJobList();

    /***
     * 更改job
     * *****/
    void updateCron(@Param("scheduleJob") QuartzJob scheduleJob);
}
