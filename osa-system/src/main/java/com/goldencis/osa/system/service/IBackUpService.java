package com.goldencis.osa.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.system.entity.BackUp;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shigd
 * @since 2018-12-27
 */
public interface IBackUpService extends IService<BackUp> {
    /**
     * 查询备份总数
     */
    Long getBackUpCnt(BackUp backUpDO, Map<String, Object> param);

    /**
     * 查询备份列表
     */
    List<BackUp> getBackUpList(BackUp backUpDO, Map<String, Object> param);
    /**
     * 新增备份
     */
    void addBackUp(BackUp backUpDO);
    /**
     * 根据名称校验名称是否存在
     */
    boolean isExitBackUpName(String name);
    /**
     * 设置自动备份
     */
    void addAutoBackUp(BackUp backUpDO);
    /**
     * 获取自动备份设置信息
     */
    Map<String, Object> getAutoBackUp();
    /**
     * 删除备份
     */
    String deleteBackUp(String ids);
    /**
     *定时执行备份脚本
     */
    void backUpExec();
    /**
     *查询备份文件路径
     */
    String getBackupFilePath(String id);

    /**
     * 数据库维护设置
     * @param day
     */
    void databaseClean(String day);
}
