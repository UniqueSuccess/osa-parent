package com.goldencis.osa.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.common.entity.ResultLog;
import com.goldencis.osa.core.entity.Usergroup;
import com.goldencis.osa.core.params.UsergroupParams;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 用户组表 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-23
 */
public interface IUsergroupService extends IService<Usergroup> {

    /**
     * 保存用户组
     * @param usergroup
     */
    void saveUsergroup(Usergroup usergroup);

    /**
     * 根据用户组id删除对应的用户组
     * @param groupId 用户组id
     */
    String deleteUsergroupById(@NotNull Integer groupId);

    /**
     * 根据用户组id更新对应的用户组<br>
     * 目前只会更新用户组名称以及上级用户组两个属性
     * @param id 用户组id
     * @param usergroup
     */
    void updateUsergroupById(@NotNull Integer id, Usergroup usergroup);

    /**
     * 根据用户的guid获取用户所属的用户组集合
     * @param guid
     * @return
     */
    List<Usergroup> getUsergroupListByUserGuid(@NotNull String guid);

    /**
     * 根据用户组id获取所有子用户组集合
     * @param id
     * @return
     */
    List<Usergroup> getGroupListByPid(@NotNull Integer id);

    /**
     * 检查数据库中是否存在重名的用户组
     * @param usergroup
     * @return 是否可用，true为该账户名可用，false为该账户名不可用。
     */
    boolean checkUsergroupNameDuplicate(Usergroup usergroup);

    /**
     * 分页查询用户组
     * @param page
     * @param params
     */
    void getUsergroupInPage(IPage<Usergroup> page, UsergroupParams params);

    /**
     * 根据用户guid获取用户组树
     * @param guid
     * @return
     */
    List<Usergroup> getUsergroupListByGuid(String guid);

    /**
     * 根据用户组guid 获取用户组树
     */
    List<Usergroup> getUsergroupListByUserGroupGuid(Integer guid);

    /**
     * 批量删除用户组
     * @param list 用户组id的集合
     * @return 删除成功的数量
     */
    ResultLog deleteBatch(List<Integer> list);
}
