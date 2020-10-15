package com.goldencis.osa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.system.entity.UserAssetAssetgroup;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户、设备/设备组 关联表（操作员管理设备/设备组）  Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-12-07
 */
public interface UserAssetAssetgroupMapper extends BaseMapper<UserAssetAssetgroup> {

    /**
     * 获取设备组列表
     */
    List<UserAssetAssetgroup> getAssetgroupListByUserGuid(String userGuid);

    /**
     * 分页： 统计设备组数量
     */
    int countAssetgroupByUserGuidInPage(Map<String, Object> paramMap);

    /**
     * 分页： 统计设备组数据
     */
    List<UserAssetAssetgroup> getAssetgroupByUserGuidInPage(Map<String, Object> paramMap);

    /**
     * 分页： 统计设备数量
     */
    int countAssetByUserGuidInPage(Map<String, Object> paramMap);

    /**
     * 分页： 统计设备数据
     */
    List<UserAssetAssetgroup> getAssetByUserGuidInPage(Map<String, Object> paramMap);

    /**
     * 获取系统管理员
     */
    List<User> getSystemUsers(Map<String, Object> paramMap);
}
