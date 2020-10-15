package com.goldencis.osa.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.entity.UserReportEntity;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息表-定义用户基本信息 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据guid查询用户
     * @param guid 用户guid
     * @return 用户对象
     */
    User findUserByGuid(String guid);

    /**
     * 根据参数统计用户数量
     * @param params 参数map
     * @return 总数
     */
    int countUsersInPage(Map<String, Object> params);

    /**
     * 根据参数查询用户集合
     * @param params 参数map
     * @return 用户集合
     */
    List<User> getUsersInPage(Map<String, Object> params);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    User findUserByUsername(String username);

    /**
     * 通过策略guid查询是否有用户使用策略
     */
    List<User> findUserByStrategyId(String strategyId);


    void updateLastLoginTime(User user);

    /**
     * 根据用户角色 pid 获取用户
     * @param map 用户角色pid
     * @return 用户列表
     */
    List<User> getUsersByType(Map<String, String> map);

    /**
     * 根据权限查询指定用户
     * @param resourceType 资源类型
     * @param resourceId 资源id
     * @param excludeSystem 查询结果中是否排除System超级管理员
     * @return
     */
    List<User> getUserByPermission(@Param(value = "resourceType") int resourceType,
                                   @Param(value = "resourceId") List<Integer> resourceId,
                                   @Param(value = "excludeSystem") boolean excludeSystem);

    /**
     * 更新系统账号信息,目前只会更新名称以及密码两个字段
     * @param user
     */
    void updateSystemUserInfo(User user);

    /**
     * 根据操作员的guid，查找对应的审批员集合（过滤超级管理员）
     * @param guid 操作员的guid
     * @return 审批员集合
     */
    List<User> findAuditorByOperatorGuid(@Param(value = "guid") String guid);

    /**
     * 授权用户图表信息获取
     * @param params
     * @return
     */
    List<Map<String,Object>> getUserReportChart(@Param("params") Map<String,String> params);

    /**
     * 获取授权用户总数
     * @param params
     * @return
     */
    long getUserReportCount(@Param("params") Map<String,String> params);

    /**
     * 获取授权用户列表
     * @param params
     * @return
     */
    List<UserReportEntity> getUserReportList(@Param("params") Map<String,String> params);

    /**
     * 根据用户guid删除授权信息
     * @param guid 用户guid
     */
    void deletePermissionByUserId(@Param(value = "guid") String guid);

    /**
     * 获取管控平台json信息
     * @return
     */
    String getPlatformJsonInfo();

    /**
     * 根据用户名查找默认的管理员用户
     * @param username 用户名
     * @return 角色的关联
     */
    User findDefaultAdminRoleRelationByUsername(@Param(value = "username") String username);

    /**
     * 设置策略时间
     * @param strategyGuid
     */
    LocalDateTime getStrategyUpdateTime(java.lang.String strategyGuid);

    void removeUkeyRelation(String guid);

    /**
     * 通过用户名 获取 用户组名
     */
    String getUsergroupNamesByUserName(String username);
}
