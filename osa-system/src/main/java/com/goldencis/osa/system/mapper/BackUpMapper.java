package com.goldencis.osa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.system.entity.BackUp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author shigd
 * @since 2018-12-27
 */
public interface BackUpMapper extends BaseMapper<BackUp> {

    /**
     * 查询备份总数
     */
    Long getBackUpCnt(@Param("backup") BackUp backUpDO, @Param("param") Map<String, Object> param);

    /**
     * 查询备份列表
     */
    List<BackUp> getBackUpList(@Param("backup") BackUp backUpDO, @Param("param") Map<String, Object> param);

    /**
     * 新增数据库备份
     */
    void addBackUp(BackUp backUpDO);
    /**
     * 根据名称校验名称是否存在
     */
    Long isExitBackUpName(@Param("name") String name);
    /**
     * 根据id查询备份信息
     */
    Map<String,Object> getBackUpById(@Param("id") String id);
    /**
     * 根据id删除备份信息
     */
    void deleteBackUp(String id);
    /**
     * 获取备份中的数据信息
     */
    List<Map<String,String>> getBackUpExec();
    /**
     * 修改备份数据状态
     */
    void updateBackupStatus(@Param("param") Map<String, String> param);
}
