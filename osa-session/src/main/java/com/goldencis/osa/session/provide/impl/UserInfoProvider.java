package com.goldencis.osa.session.provide.impl;

import com.alibaba.fastjson.JSONObject;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.RedisUtil;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.session.provide.IProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: guacamole-client
 * @author: wang mc
 * @create: 2018-11-21 11:52
 **/
@Component
public class UserInfoProvider implements IProvider<String, User> {

    private final Logger logger = LoggerFactory.getLogger(AssetAccountProvider.class);
    @Autowired
    private RedisUtil redisUtil;

    @Override
    @SuppressWarnings("unchecked")
    public User get(String loginId) throws Exception {
        Object o = redisUtil.get(generateKey(loginId));
        if (o instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) o;
            JSONObject object = new JSONObject(map);
            User info = new User();
            info.setGuid(object.getString("guid"));
            info.setName(object.getString("name"));
            info.setUsername(object.getString("username"));
            return info;
        }
        logger.debug("redis中userinfo格式错误,session: {}", loginId);
        return null;
    }

    private String generateKey(String osaUser) {
        return ConstantsDto.REDIS_KEY_SESSION + osaUser + ConstantsDto.REDIS_KEY_SSO_USER_INFO;
    }
}
