package com.goldencis.osa.core.mapper;

import com.goldencis.osa.core.entity.Usergroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.core.params.UsergroupParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户组表 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-10-23
 */
public interface UsergroupMapper extends BaseMapper<Usergroup> {

    Integer countUsergroupInPage(UsergroupParams params);

    List<Usergroup> getUsergroupInPage(UsergroupParams params);

    /**
     * 根据用户组id获取 父级用户组名字
     * @param usergroupId 用户组id
     * @return 父级用户组名字
     */
    String getPnameByUsergroupId(Integer usergroupId);

    /**
     * 根据用户组id删除授权信息
     * @param groupId
     */
    void deleteGrantedByUserGroupId(@Param(value = "groupId") Integer groupId);

    /**
     * 更新子级用户组的TreePath 替换TreePath路径
     * @param oldTreePath 需要修改的路径部分
     * @param newTreePath 替换的新的路径部分
     * @param oldSelfPath 自身的旧路径
     */
    void updateTreePath(@Param(value = "oldTreePath") String oldTreePath, @Param(value = "newTreePath") String newTreePath, @Param(value = "oldSelfPath") String oldSelfPath);
}
