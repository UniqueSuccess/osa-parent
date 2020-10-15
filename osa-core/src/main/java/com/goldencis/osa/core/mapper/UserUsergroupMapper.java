package com.goldencis.osa.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.core.entity.UserUsergroup;

/**
 * <p>
 * 用户和用户组关联表 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-10-23
 */
public interface UserUsergroupMapper extends BaseMapper<UserUsergroup> {

    /**
     * 根据用户id删除用户和用户组关联表记录
     */
    void deleteUserUsergroupByUserGuid(String userGuid);

    /**
     * 添加用户和用户组记录
     */
    boolean  saveUserUsergroup(UserUsergroup userUsergroup);

}
