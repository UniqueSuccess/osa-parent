package com.goldencis.osa.core.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.common.utils.DateUtil;
import com.goldencis.osa.common.utils.HttpKit;
import com.goldencis.osa.common.utils.IpUtil;
import com.goldencis.osa.common.utils.NetworkUtil;
import com.goldencis.osa.core.entity.*;
import com.goldencis.osa.core.service.IPermissionService;
import com.goldencis.osa.core.service.IUkeyService;
import com.goldencis.osa.core.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by limingchao on 2018/9/25.
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * 默认锁定时长(分钟)
     */
    private static final int DEFAULT_LOCK_DURATION = 10;
    /**
     * 默认密码重试次数
     */
    private static final int DEFAULT_TRY_COUNT = 3;
    private Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private IUserService userService;
    @Autowired
    private IUkeyService ukeyService;

    @Autowired
    private IPermissionService permissionService;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        QueryWrapper<com.goldencis.osa.core.entity.User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        com.goldencis.osa.core.entity.User user = userService.getOne(queryWrapper);

        if (user == null) {
            logger.info("此用户名不存在：" + username);
            throw new UsernameNotFoundException("用户名或密码不正确！");
        }

        Platform platform = userService.getPlatformInfo();
        Login login = platform.getAccess().getLogin();

        // 用户名黑名单
        if (blockByUsername(user.getUsername(), login.getUsername())) {
            throw new UsernameNotFoundException("当前用户不允许登录系统");
        }

        // ip黑名单
        if (blockByIp(NetworkUtil.getIpAddress(HttpKit.getRequest()), login.getIp())) {
            throw new UsernameNotFoundException("当前IP不允许登录系统");
        }

        Security security = platform.getSecurity();
        int lockDuration = Objects.nonNull(security.getLockDuration()) ? security.getLockDuration() : DEFAULT_LOCK_DURATION;

        if (!UserStatus.ENABLE.code().equals(user.getStatus())) {
            if (UserStatus.LOCK.code().equals(user.getStatus())) {
                if (Objects.isNull(user.getErrorLoginLastTime())) {
                    logger.info("此用户已经被锁定：" + username);
                    throw new LockedException("用户已被锁定！");
                } else {
                    if (Math.abs(System.currentTimeMillis() - DateUtil.localDateTime2Date(user.getErrorLoginLastTime()).getTime()) < minutesToLong(lockDuration)) {
                        logger.info("此用户已经被锁定：" + username);
                        throw new LockedException("用户已被锁定！");
                    }
                }
            } else if (UserStatus.DISABLE.code().equals(user.getStatus())) {
                logger.info("此用户已经被停用：" + username);
                throw new LockedException("用户已被停用！");
            }
        }

        checkLock(user, security);

        //为当前用户添加角色
        Collection<GrantedAuthority> auths = new ArrayList<>();
        GrantedAuthority sim = new SimpleGrantedAuthority("ROLE_USER");
        auths.add(sim);

        User userDetails = new User(username, user.getPassword(), auths);

        return userDetails;
    }

    private void checkLock(com.goldencis.osa.core.entity.User user, Security security) {
        HttpServletRequest request = HttpKit.getRequest();
        // 执行认证逻辑
        if (authentication(request, user)) {
            user.setStatus(UserStatus.ENABLE.code());
            user.setErrorLoginCount(0);
            userService.updateById(user);
            return;
        }
        // 尝试次数(默认3次)
        int tryCount = Objects.nonNull(security.getTryCount()) ? security.getTryCount() : DEFAULT_TRY_COUNT;
        // 锁定时长(默认10分钟)
        int lockDuration = Objects.nonNull(security.getLockDuration()) ? security.getLockDuration() : DEFAULT_LOCK_DURATION;
        // 目前登录错误的次数
        int errorLoginCount = Objects.nonNull(user.getErrorLoginCount()) ? user.getErrorLoginCount() + 1 : 0;

        // 超过错误次数,将账号锁定
        if (errorLoginCount >= tryCount) {
            user.setStatus(UserStatus.LOCK.code());
            user.setErrorLoginLastTime(LocalDateTime.now());
            user.setErrorLoginCount(0);
            userService.updateById(user);
            throw new UsernameNotFoundException("当前用户已被锁定");
        }
        user.setErrorLoginLastTime(LocalDateTime.now());
        user.setErrorLoginCount(errorLoginCount);
        userService.updateById(user);
        throw new UsernameNotFoundException("用户名或密码错误,还有" + (tryCount - errorLoginCount) + "次机会");
    }

    /**
     * 认证
     *
     * @param request
     * @param user    用户信息
     * @return 如果认证成功, 返回true;否则,返回false;
     */
    private boolean authentication(HttpServletRequest request, com.goldencis.osa.core.entity.User user) {
        String password = request.getParameter("password");
        if (!encoder.matches(password, user.getPassword())) {
            return false;
        }
        if (AuthMethod.USB.equals(AuthMethod.getByCode(user.getAuthenticationMethod()))) {
            String sign = request.getParameter("sign");
            if (StringUtils.isEmpty(sign)) {
                throw new UsernameNotFoundException("获取USBKey失败");
            }
            List<Ukey> ukeyList = ukeyService.list(new QueryWrapper<Ukey>().eq("user_guid", user.getGuid()));
            if (CollectionUtils.isEmpty(ukeyList)) {
                throw new UsernameNotFoundException("当前用户没有绑定任何USBKey");
            }
            for (Ukey ukey : ukeyList) {
                if (sign.equals(ukey.getSign())) {
                    return true;
                }
            }
            throw new UsernameNotFoundException("USBKey不匹配");
        }
        return true;
    }


    /**
     * 通过用户名阻断用户登录
     *
     * @param username 用户名
     * @param list     用户名黑名单
     * @return 如果阻断, 返回true;否则,返回false;
     */
    private boolean blockByUsername(String username, List<String> list) {
        if (CollectionUtils.isEmpty(list) || StringUtils.isEmpty(username)) {
            return false;
        }
        for (String s : list) {
            if (username.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过ip阻断用户登录
     *
     * @param ip   ip地址
     * @param list ip黑名单
     * @return 如果阻断, 返回true;否则,返回false;
     */
    private boolean blockByIp(String ip, List<String> list) {
        return IpUtil.isIpInAddressRange(ip, list);
    }

    /**
     * 将分钟转换为毫秒值
     *
     * @param minutes
     * @return
     */
    private long minutesToLong(int minutes) {
        return minutes * 60 * 1000;
    }

}