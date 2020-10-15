package com.goldencis.osa.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.DateUtil;
import com.goldencis.osa.common.utils.HttpKit;
import com.goldencis.osa.common.utils.JsonUtils;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.core.entity.*;
import com.goldencis.osa.core.mapper.RoleMapper;
import com.goldencis.osa.core.mapper.UserMapper;
import com.goldencis.osa.core.mapper.UserRoleMapper;
import com.goldencis.osa.core.mapper.UserUsergroupMapper;
import com.goldencis.osa.core.service.IUkeyService;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.core.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 用户信息表-定义用户基本信息 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UsergroupServiceImpl usergroupService;

    @Autowired
    private DictionaryServiceImpl dictionaryService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserUsergroupMapper userUsergroupMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IUkeyService ukeyService;

    //密码验证
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(readOnly = true)
    public User findUserByGuid(String guid) {
        return userMapper.findUserByGuid(guid);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByUserName(String username) {
//        User user = userMapper.findUserByUsername(username);
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByUserName4Security(String username) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        user.setUsergroupsName(userMapper.getUsergroupNamesByUserName(username));
        return user;
    }

    @Override
    public QueryWrapper<User> parseParams2QueryWapper(Map<String, String> params) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        //增加查询条件
        String searchStr = params.get("searchStr");
        if (!StringUtils.isEmpty(searchStr)) {
            wrapper.nested(w -> w.like("name", searchStr).or().like("guid", searchStr));
        }

        //增加时间条件
        QueryUtils.setQeryTimeByParamsMap(wrapper, params, "create_time");

        //增加排序条件，默认按创建时间的倒序排列
        QueryUtils.setQeryOrderByParamsMap(wrapper, params, ConstantsDto.ORDER_TYPE_DESC, "create_time");
        return wrapper;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkUserNameDuplicate(User user) {
        User preUser = this.findUserByUserName(user.getUsername());

        //判断数据库是否有该记录，不存在即可用，返回true，如果有继续判断
        if (preUser != null) {
            //比较两个对象的id，若一致，是同一个对象没有改变名称的情况，返回可用true。
            if (preUser.getGuid().equals(user.getGuid())) {
                return true;
            }
            //若果不同，说明为两个用户，名称重复，不可用，返回false
            return false;
        }
        return true;
    }

    /**
     * 分页获取用户数据
     *
     * @param params 查询查询
     */
    @Override
    public IPage<User> getUsersInPage(Map<String, String> params) {
        Page page = new Page();

        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);

        //增加默认排序
        if (!params.containsKey("orderType")) {
            params.put("orderType", ConstantsDto.ORDER_TYPE_DESC);
        }
        if (!params.containsKey("orderColumn")) {
            params.put("orderColumn", "last_login_time");
        }

        if (paramMap.containsKey("usergroupPid")) {
            Integer pid = Integer.valueOf((String) paramMap.get("usergroupPid"));
            paramMap.put("usergroupList", new ArrayList<Integer>() {
                {
                    add(pid);
                }
            });
        }

        //增加角色限定
        Integer roleType = dictionaryService.getRoleType(!StringUtils.isEmpty(paramMap.get("roleType")) ? (String) paramMap.get("roleType") : ConstantsDto.TYPE_NORMAL);
        List<Role> roleList = roleMapper.selectList(new QueryWrapper<Role>().eq("type", roleType));

        if (!ListUtils.isEmpty(roleList)) {
            paramMap.put("roleList", roleList.stream().map(Role::getGuid).collect(Collectors.toList()));
        }

        //添加状态的查询条件
        this.addStatusQueryCondition(paramMap);

        //添加authenticationMethod的条件
        this.addAuthenticationMethodQueryCondition(paramMap);
        // 添加用户id筛选条件
        this.addUserIdListQueryCondition(paramMap);

        //统计用户总数量
        int total = userMapper.countUsersInPage(paramMap);
        //带参数的分页查询
        List<User> userList = userMapper.getUsersInPage(paramMap);

        page.setTotal(total);
        page.setRecords(userList);

        return page;
    }

    private void addUserIdListQueryCondition(Map<String, Object> paramMap) {
        if (!StringUtils.isEmpty(paramMap.get("userIds"))) {
            String userIds = (String) paramMap.get("userIds");
            List<String> list = Arrays.stream(userIds.split(",")).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(list)) {
                paramMap.put("userIdList", list);
            }
        }
    }

    private void addAuthenticationMethodQueryCondition(Map<String, Object> paramMap) {
        if (!StringUtils.isEmpty(paramMap.get("authenticationMethod"))) {
            String authenticationMethodStr = (String) paramMap.get("authenticationMethod");
            String[] split = authenticationMethodStr.split(";");
            List<String> authenticationMethodList = new ArrayList<>();
            for (String authenticationMethod : split) {
                authenticationMethodList.add(authenticationMethod);
            }

            if (!ListUtils.isEmpty(authenticationMethodList)) {
                paramMap.put("authenticationMethodList", authenticationMethodList);
            }
        }
    }

    private void addStatusQueryCondition(Map<String, Object> paramMap) {
        if (!StringUtils.isEmpty(paramMap.get("status"))) {
            String statusStr = (String) paramMap.get("status");
            String[] split = statusStr.split(";");
            List<String> statusList = new ArrayList<>();
            for (String status : split) {
                statusList.add(status);
            }

            if (!ListUtils.isEmpty(statusList)) {
                paramMap.put("statusList", statusList);
            }
        }
    }

    /**
     * 新增用户
     * @param user 用户对象
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void saveUser(User user) {
        verifyUserData(user, ConstantsDto.ROLE_USER_PID);

        user.setGuid(UUID.randomUUID().toString());
        user.setVisible(ConstantsDto.CONST_TRUE);
        user.setCreateTime(LocalDateTime.now());
        user.setCreateBy(SecurityUtils.getCurrentUser().getGuid());
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(SecurityUtils.getCurrentUser().getGuid());

        // 绑定ukey
        bindUkey(user);

        //为用户设置密码
        this.validatePassword(user);

        //为用户设置角色
        this.validateRoles(user);

        this.save(user);

        //保存用户组的关联
        saveUserUsergroup(user);
        //保存角色的关联
        saveUserRole(user);
    }

    /**
     * 保存数据验证
     */
    void verifyUserData(User user, String type) {
        //系统用户、用户验证
        if (!com.goldencis.osa.common.utils.StringUtils.isInLength(user.getUsername(), 30)) {
            throw new IllegalArgumentException(type.equals(ConstantsDto.ROLE_SYSTEM_PID) ? "账号名称最大长度为30" : "用户名最大长度为30");
        }
        if (!com.goldencis.osa.common.utils.StringUtils.isInLength(user.getName(), 45)) {
            throw new IllegalArgumentException(type.equals(ConstantsDto.ROLE_SYSTEM_PID) ? "账户名称最大长度为45" : "姓名最大长度为45");
        }

        if (!type.equals(ConstantsDto.ROLE_SYSTEM_PID)) {
            //用户验证
            if (!com.goldencis.osa.common.utils.StringUtils.isInLength(user.getPhone(), 15)) {
                throw new IllegalArgumentException("电话最大长度为15");
            }
            if (!com.goldencis.osa.common.utils.StringUtils.isInLength(user.getEmail(), 50)) {
                throw new IllegalArgumentException("邮箱最大长度为50");
            }
        }
    }

    private void bindUkey(User user) {
        if (StringUtils.isEmpty(user.getGuid())) {
            throw new IllegalArgumentException("用户GUID不能为空");
        }
        // 校验认证方式
        if (Objects.isNull(user.getAuthenticationMethod())) {
            throw new IllegalArgumentException("认证方式不能为空");
        }
        AuthMethod authMethod = AuthMethod.getByCode(user.getAuthenticationMethod());
        if (Objects.isNull(authMethod)) {
            throw new IllegalArgumentException("不存在的认证方式");
        }
        // 解除之前绑定了的ukey
        List<Ukey> list = ukeyService.list(new QueryWrapper<Ukey>().eq("user_guid", user.getGuid()));
        if (!CollectionUtils.isEmpty(list)) {
            for (Ukey item : list) {
                item.setUserGuid(null);
                ukeyService.updateById(item);
            }
        }
        // 认证方式为:密码+第三方USBKEY
        if (AuthMethod.USB.equals(authMethod)) {
            if (StringUtils.isEmpty(user.getUkeyId())) {
                throw new IllegalArgumentException("认证方式选择了USBKey,但是没有指定USBKey");
            }
            Ukey ukey = ukeyService.getById(user.getUkeyId());
            if (Objects.isNull(ukey)) {
                throw new IllegalArgumentException("不存在的USBKey");
            }
            if (!StringUtils.isEmpty(ukey.getUserGuid())) {
                if (!ukey.getUserGuid().equals(user.getGuid())) {
                    throw new IllegalArgumentException("该USBKey已经绑定其他用户");
                } else {
                    // 该Ukey已经绑定了当前用户,不需要任何处理
                    return;
                }
            }
            // 更新ukey绑定
            ukey.setUserGuid(user.getGuid());
            ukeyService.updateById(ukey);
        }
    }

    /**
     * 保存系统 用户
     * @param user 系统用户
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public User saveSystemUser(User user) {
        verifyUserData(user, ConstantsDto.ROLE_SYSTEM_PID);

        user.setGuid(UUID.randomUUID().toString());
        user.setVisible(ConstantsDto.CONST_TRUE);
        user.setCreateTime(LocalDateTime.now());
        user.setCreateBy(SecurityUtils.getCurrentUser().getGuid());
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(SecurityUtils.getCurrentUser().getGuid());

        //系统用户不涉及认证方式已经USBKey的绑定。设置默认的认证方式为密码
        user.setAuthenticationMethod(1);
        //为用户设置密码
        this.validatePassword(user);
        //设置status 状态 : 11可用
        user.setStatus(11);
        //设置角色类型
        user.setRoleIds(user.getRoleType());
        this.save(user);
        //保存角色
        saveUserRole(user);
        return user;
    }


    /**
     * 为用户设置密码
     * @param user 用户
     */
    private void validatePassword(User user) {
        //如果设置了使用默认密码，为用户添加默认密码。
        if (ConstantsDto.SWITCH_ON.equals(user.getDefaultPassword())) {
            this.setDefaultPassword(user);
        } else {//否则，对前台的密码进行加密
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }

    /**
     * 为用户设置角色
     * @param user 用户
     */
    private void validateRoles(User user) {
        //为普通用户设置普通用户的角色
        Integer type = dictionaryService.getRoleType(!StringUtils.isEmpty(user.getRoleType()) ? user.getRoleType() : ConstantsDto.TYPE_NORMAL);
        this.setNormalRole4User(user, type);
    }

    /**
     * 根据用户guid更新系统用户
     *
     * @param user 用户对象
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void updateUserByGuid(User user) {
        verifyUserData(user, ConstantsDto.ROLE_USER_PID);

        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(SecurityUtils.getCurrentUser().getGuid());

        // 绑定ukey
        bindUkey(user);

        //为用户设置密码
        if (StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(null);
        } else {
            this.validatePassword(user);
        }
        //更新用户记录
        this.updateById(user);

        //根据用户id删除用户和用户组关联表记录
        deleteUserUsergroupByUserGuid(user.getGuid());
        //保存用户组的设置
        saveUserUsergroup(user);
    }

    /**
     * 修改系统用户
     * @param user 系统用户
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public User updateSystemUser(User user) {
        verifyUserData(user, ConstantsDto.ROLE_SYSTEM_PID);

        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(SecurityUtils.getCurrentUser().getGuid());
        //密码加密
        if (!StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        baseMapper.updateSystemUserInfo(user);
        return this.getById(user.getGuid());
    }

    /**
     * 根据权限查询指定用户
     *
     * @param resourceType  资源类型
     * @param resourceId    资源id
     * @param excludeSystem 查询结果中是否排除System超级管理员
     * @return
     */
    @Override
    public List<User> getUserByPermission(int resourceType, List<Integer> resourceId, boolean excludeSystem) {
        List<Integer> list = resourceId.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return baseMapper.getUserByPermission(resourceType, list, excludeSystem);
    }

    /**
     * 根据权限查询指定用户
     * @param resourceType 资源类型
     * @param resourceId 资源id
     * @return
     */
    @Override
    public List<User> getUserByPermission(int resourceType, List<Integer> resourceId) {
        return this.getUserByPermission(resourceType, resourceId, true);
    }

    /**
     * 根据权限查询指定用户
     *
     * @param resourceType  资源类型
     * @param excludeSystem 查询结果中是否排除System超级管理员
     * @param resourceId    资源id
     * @return
     */
    @Override
    public List<User> getUserByPermission(int resourceType, boolean excludeSystem, int... resourceId) {
        return this.getUserByPermission(resourceType, Arrays.stream(resourceId).boxed().collect(Collectors.toList()), excludeSystem);
    }

    /**
     * 根据权限查询指定用户
     *
     * @param resourceType 资源类型
     * @param resourceId   资源id
     * @return
     */
    @Override
    public List<User> getUserByPermission(int resourceType, int... resourceId) {
        return this.getUserByPermission(resourceType, true, resourceId);
    }

    @Override
    public boolean isAdministratorByUsername(String username) {
        //根据用户名查找管理员角色的关联
        UserRole userRole = userRoleMapper.findAdminRoleRelationByUsername(username);
        return !(userRole == null);
    }

    @Override
    public boolean isDefaultAdministratorByUsername(String username) {
        User user = userMapper.findDefaultAdminRoleRelationByUsername(username);
        return !(user == null);
    }

    @Override
    public boolean isNormalUserByUsername(String username) {
        //根据用户名查找普通角色的关联
        UserRole userRole = userRoleMapper.findNormalRoleRelationByUsername(username);
        return !(userRole == null);
    }

    /**
     * 删除单个用户
     *
     * @param guid 用户guid
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public User deleteUserByGuid(String guid) {
        User user = baseMapper.selectById(guid);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (ConstantsDto.USER_SYSTEM_ID.equals(guid) || ConstantsDto.USER_ADMIN_ID.equals(guid) || ConstantsDto.USER_OPERATOR_ID.equals(guid) || ConstantsDto.USER_AUDITOR_ID.equals(guid)) {
            throw new IllegalArgumentException("系统用户不能删除");
        }

        //根据用户id删除用户和用户组关联表记录
        deleteUserUsergroupByUserGuid(guid);
        //根据用户id删除用户、角色关联信息表
        deleteUserRoleByUserGuid(guid);
        // 删除用户授权
        baseMapper.deletePermissionByUserId(guid);
        // 解除USBKey关系
        baseMapper.removeUkeyRelation(guid);
        this.removeById(guid);

        return user;
    }

    /**
     * 批量删除用户
     *
     * @param guids 用户guids
     */
    @Override
    public String deleteUsersByIds(List<String> guids) {
        if (ListUtils.isEmpty(guids)) {
            throw new IllegalArgumentException("用户guid不存在");
        }
        StringBuffer userNames = new StringBuffer();
        for (String guid : guids) {
            User user = deleteUserByGuid(guid);
            userNames.append(user.getName() + ",");
        }
        userNames.deleteCharAt(userNames.length() - 1);
        return userNames.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        return SecurityUtils.getCurrentUser();
    }

    @Override
    public void setDefaultPassword(User user) {
        user.setPassword(ConstantsDto.DEFAULT_PWD);
    }

    @Override
    public void setNormalRole4User(User user, Integer... type) {
        if (type != null && type.length > 0) {
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < type.length; i++) {
                //查询类型对应的角色集合
                List<Role> roleList = roleMapper.selectList(new QueryWrapper<Role>().eq("type", type[i]));

                //拼成id字符串
                for (Role role : roleList) {
                    sb.append(role.getGuid() + ConstantsDto.SEPARATOR_COMMA);
                }
            }

            if (!StringUtils.isEmpty(sb.toString())) {
                //剔除最后一位并赋值
                sb.deleteCharAt(sb.length() - 1);
                user.setRoleIds(sb.toString());
            }
        }
    }

    @Override
    public void updateLastLoginTime(User user) {
        baseMapper.updateLastLoginTime(user);
    }

    @Override
    public List<Integer> getUsergroupIdListByCurrentUser() {
        List<UserUsergroup> relationList = userUsergroupMapper.selectList(new QueryWrapper<UserUsergroup>().eq("user_guid", SecurityUtils.getCurrentUser().getGuid()));
        if (!ListUtils.isEmpty(relationList)) {
            return relationList.stream().map(UserUsergroup::getUsergroupId).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Map<String, Object> checkUserLogonMethodByUsername(String username) {
        User user = baseMapper.selectOne(new QueryWrapper<User>().eq("username", username));

        if (user == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        List<UserRole> userRoleRelations = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_guid", user.getGuid()));
        boolean isOperationUser = false;
        if (!ListUtils.isEmpty(userRoleRelations)) {
            for (UserRole userRole : userRoleRelations) {
                if ("4".equals(userRole.getRoleGuid())) {
                    isOperationUser = true;
                }
            }
        }
        map.put("isOperationUser", isOperationUser);
        map.put("authenticationMethod", user.getAuthenticationMethod());
        return map;
    }

    /**
     * 获取所有操作员列表
     * @return 操作员列表
     */
    @Override
    public List<User> getAllOperatorList() {
        List<User> operators = getUsersByType(ConstantsDto.ROLE_OPERATOR_PID);
        return operators;
    }

    @Override
    public Platform getPlatformInfo() {
        String json = baseMapper.getPlatformJsonInfo();
        return JsonUtils.jsonToPojo(json, Platform.class);
    }

    /**
     * 修改密码
     * @param guid 用户guid
     * @param oldPwd 原密码
     * @param newPwd 新密码
     */
    @Override
    public String updateUserPwd(String guid, String oldPwd, String newPwd) {
        if (StringUtils.isEmpty(guid) || StringUtils.isEmpty(oldPwd) || StringUtils.isEmpty(newPwd)) {
            throw new IllegalArgumentException("用户id、原密码、新密码不允许为空");
        }
        if (oldPwd.equals(newPwd)) {
            throw new IllegalArgumentException("新密码与原密码一致");
        }
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("guid", guid));
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!encoder.matches(oldPwd, user.getPassword())) {
            throw new IllegalArgumentException("原密码不正确");
        }
        user.setPassword(passwordEncoder.encode(newPwd));
        userMapper.updateById(user);
        return "用户名：" + user.getUsername() + " ，用户名称：" + user.getName();
    }

    @Override
    public void updateOnlineSessionDevunique(String devunique, HttpSession session) {
        Map<String, HttpSession> onlineDevuniqueMap = (Map<String, HttpSession>) session.getServletContext().getAttribute("onlineDevuniqueMap");
        if (onlineDevuniqueMap != null) {
            session.setAttribute("devunique", devunique);
            onlineDevuniqueMap.put(devunique, session);
        }
    }

    @Override
    public boolean queryUserStatus(String guid, String devUnique) {
        Map<String, HttpSession> onlineDevuniqueMap = (Map<String, HttpSession>) HttpKit.getRequest().getSession().getServletContext().getAttribute("onlineDevuniqueMap");
        if (onlineDevuniqueMap.containsKey(devUnique)) {
            String username = (String) onlineDevuniqueMap.get(devUnique).getAttribute("username");
            if (!StringUtils.isEmpty(username)) {
                User user = this.findUserByUserName(username);
                return user.getGuid().equals(guid);
            }
        }
        return false;
    }

    @Override
    public JSONObject findUserPolicyByUserguid(String guid) {
        JSONObject result  = new JSONObject();
        if (StringUtils.isEmpty(guid)){
            result.put("msg", "userGuid or devUnique can not be null!");
            return result;
        }

        User user = userMapper.selectById(guid);
        if (Objects.isNull(user)){
            result.put("msg", "userGuid does not exists!");
            return result;
        }

        //完成策略数据库表设计后添加
        //添加策略id
        java.lang.String strategyGuid = user .getStrategy();
        result.put("policyGuid", strategyGuid);
        //添加时间戳
        LocalDateTime strategyUpdateTime =  userMapper.getStrategyUpdateTime(strategyGuid);
        long time = DateUtil.getTimestampByLocalDateTime(strategyUpdateTime);
        result.put("policyStamp", String.valueOf(time));
        return result;
    }

    /**
     * 根据用户角色pid 获取不同角色的用户列表
     * @param pid 用户角色pid
     * @return 用户列表
     */
    public List<User> getUsersByType(String pid) {
        Map<String, String> map = new HashMap<>();
        map.put("pid", pid);
        List<User> userList = userMapper.getUsersByType(map);
        return userList;
    }


    /**
     * 根据用户id删除用户、用户组关联表
     * @param userGuid
     */
    private void deleteUserUsergroupByUserGuid(String userGuid) {
        if (userGuid != null) {
            userUsergroupMapper.deleteUserUsergroupByUserGuid(userGuid);
        }
    }

    /**
     * 添加用户和用户组关联表记录
     */
    private void saveUserUsergroup(User user) {
        if (!StringUtils.isEmpty(user.getUsergroupIds())) {
            String[] idArr = user.getUsergroupIds().split(ConstantsDto.SEPARATOR_COMMA);
            Stream.of(idArr).map(usergroupId -> new UserUsergroup(user.getGuid(), Integer.valueOf(usergroupId))).forEach(userUsergroup -> userUsergroupMapper.saveUserUsergroup(userUsergroup));
        }
    }

    /**
     * 根据用户id删除用户、角色关联表
     *
     * @param userGuid 用户guid
     */
    private void deleteUserRoleByUserGuid(String userGuid) {
        if (userGuid != null) {
            userRoleMapper.deleteUserRoleByUserGuid(userGuid);
        }
    }

    /**
     * 添加用户、角色关联表记录
     */
    private void saveUserRole(User user) {
        if (!StringUtils.isEmpty(user.getRoleIds())) {
            String[] idArr = user.getRoleIds().split(ConstantsDto.SEPARATOR_COMMA);
            Stream.of(idArr).map(roleId -> new UserRole(user.getGuid(), roleId)).forEach(userRole -> userRoleMapper.saveUserRole(userRole));
        }
    }
}