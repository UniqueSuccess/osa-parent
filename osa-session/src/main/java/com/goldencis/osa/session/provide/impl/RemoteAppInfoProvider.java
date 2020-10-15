package com.goldencis.osa.session.provide.impl;

import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.JsonUtils;
import com.goldencis.osa.common.utils.RedisUtil;
import com.goldencis.osa.session.domain.RemoteAppInfo;
import com.goldencis.osa.session.provide.IProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 10:06
 **/
@Component
public class RemoteAppInfoProvider implements IProvider<String, RemoteAppInfo> {

    private final Logger logger = LoggerFactory.getLogger(AssetAccountProvider.class);
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public RemoteAppInfo get(String sessionId) throws Exception {
        Object o = redisUtil.get(generateKey(sessionId));
        if (o instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) o;
            map.forEach((key, value) -> logger.debug("{} : {}", key, value));
            return JsonUtils.mapToPojo(map, RemoteAppInfo.class);
        }
        return null;
    }


    private String generateKey(String osaUser) {
        return ConstantsDto.REDIS_KEY_SESSION + osaUser + ConstantsDto.REDIS_KEY_SSO_LOGON_INFO;
    }
}
