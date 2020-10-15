package com.goldencis.osa.core.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.core.entity.Platform;
import com.goldencis.osa.core.entity.User;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息表-定义用户基本信息 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
public interface IUserService extends IService<User> {

    /**
     * 通过guid查询用户信息
     *
     * @param guid 用户guid
     * @return 用户对象
     */
    User findUserByGuid(String guid);

    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户对象
     */
    User findUserByUserName(String username);

    /**
     * 将请求中的查询参数转化为包装类
     *
     * @param params 请求中的参数Map
     * @return 查询的包装类
     */
    QueryWrapper<User> parseParams2QueryWapper(Map<String, String> params);

    /**
     * 检查用户名是否重复
     *
     * @param user 请求中提交的用户对象
     * @return 是否可用，true为该账户名可用，false为该账户名不可用。
     */
    boolean checkUserNameDuplicate(User user);

    /**
     * 新建用户
     *
     * @param user 用户对象
     */
    void saveUser(User user);

    /**
     * 根据guid编辑用户
     *
     * @param user 用户对象
     */
    void updateUserByGuid(User user);

    /**
     * 根据用户guid删除用户
     *
     * @param guid 用户guid
     */
    User deleteUserByGuid(String guid);

    /**
     * 通过用户guidIds批量删除用户接口
     */
    String deleteUsersByIds(List<String> guids);

    /**
     * 获取当前登录用户
     *
     * @return
     */
    User getCurrentUser();

    /**
     * 自定义分页查询
     *
     * @param params 查询查询
     * @return 分页对象
     */
    IPage<User> getUsersInPage(Map<String, String> params);

    /**
     * 为用户添加默认密码
     *
     * @param user 用户对象
     */
    void setDefaultPassword(User user);

    /**
     * 为普通用户设置普通用户的角色
     *
     * @param user 用户对象
     * @param type
     */
    void setNormalRole4User(User user, Integer... type);

    /**
     * 更新最后登录时间
     *
     * @param user 用户信息
     */
    void updateLastLoginTime(User user);

    /**
     * 根据当前登录用户获取其所在用户组id的集合
     *
     * @return
     */
    List<Integer> getUsergroupIdListByCurrentUser();

    /**
     * 通过用户名获取用户登录方式信息
     *
     * @param username 用户名
     * @return
     */
    Map<String, Object> checkUserLogonMethodByUsername(String username);

    /**
     * 获取所有操作员
     *
     * @return 操作员列表
     */
    List<User> getAllOperatorList();

    /**
     * 保存系统用户
     *
     * @param user 系统用户
     */
    User saveSystemUser(User user);

    /**
     * 修改系统用户
     *
     * @param user 系统用户
     */
    User updateSystemUser(User user);

    /**
     * 根据权限查询指定用户
     *
     * @param resourceType  资源类型
     * @param resourceId    资源id
     * @param excludeSystem 查询结果中是否排除System超级管理员
     * @return
     */
    List<User> getUserByPermission(int resourceType, List<Integer> resourceId, boolean excludeSystem);

    /**
     * 根据权限查询指定用户
     *
     * @param resourceType  资源类型
     * @param resourceId    资源id
     * @return
     */
    List<User> getUserByPermission(int resourceType, List<Integer> resourceId);

    /**
     * 根据权限查询指定用户
     *
     * @param resourceType  资源类型
     * @param resourceId    资源id
     * @param excludeSystem 查询结果中是否排除System超级管理员
     * @return
     */
    List<User> getUserByPermission(int resourceType, boolean excludeSystem, int... resourceId);

    /**
     * 根据权限查询指定用户
     *
     * @param resourceType  资源类型
     * @param resourceId    资源id
     * @return
     */
    List<User> getUserByPermission(int resourceType, int... resourceId);

    /**
     * 根据用户名查询是否，该用户是否是管理员
     * @param username 用户名
     * @return 是否是管理员
     */
    boolean isAdministratorByUsername(String username);

    /**
     * 根据用户名查询是否，该用户是否是默认的管理员
     * @param username 用户名
     * @return 是否是管理员
     */
    boolean isDefaultAdministratorByUsername(String username);

    /**
     * 根据用户名查询是否，该用户是否是运维用户
     * @param username 用户名
     * @return 是否是运维用户
     */
    boolean isNormalUserByUsername(String username);

    /**
     * 获取管控平台信息
     * @return
     */
    Platform getPlatformInfo();

    /**
     *修改密码
     * @param guid 用户guid
     * @param oldPwd 原密码
     * @param newPwd 新密码
     */
    String updateUserPwd(String guid, String oldPwd, String newPwd);

    /**
     * 更新当前登录session所关联的唯一设备标识
     * @param devunique 唯一设备标识
     * @param session 当前登录session
     */
    void updateOnlineSessionDevunique(String devunique, HttpSession session);

    /**
     * 查询对应用户guid和设备唯一标示的用户在线状态
     * @param guid 用户guid
     * @param devUnique 唯一设备标识
     * @return
     */
    boolean queryUserStatus(String guid, String devUnique);

    /**
     * 根据用户guid，查询对应的策略并返回
     * @param guid 用户guid
     * @return 策略的json
     */
    JSONObject findUserPolicyByUserguid(String guid);

    /**
     * 用于用户登录登录之后 Security
     */
    User findUserByUserName4Security(String username);
}
