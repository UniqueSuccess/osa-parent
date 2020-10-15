package com.goldencis.osa.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.common.entity.ResultTree;
import com.goldencis.osa.task.entity.TaskAsset;

import java.util.List;

/**
 * <p>
 * 定时改密计划 服务类
 * </p>
 *
 * @author limingchao
 * @since 2019-01-19
 */
public interface ITaskAssetService extends IService<TaskAsset> {


    /**
     * 计划id
     */
    String TASK_ID = "1";
    /**
     * 计划名称
     */
    String TASK_NAME = "计划名称";
    String JOB_NAME = "ChangePasswordJob";
    String GROUP_NAME = "DEFAULT";

    /**
     * 刷新自动改密的任务
     * @param task
     */
    void refreshTaskAsset(TaskAsset task);

    /**
     * 获取自动改密计划详情
     * @return
     */
    TaskAsset detail();

    /**
     * 获取设备列表树
     * @return
     */
    List<ResultTree> getAssetListTree();
}
