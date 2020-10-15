package com.goldencis.osa.session.provide.impl;

import com.alibaba.fastjson.JSONObject;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.RedisUtil;
import com.goldencis.osa.session.provide.IProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: guacamole-server
 * @author: wang mc
 * @create: 2018-11-16 13:32
 **/
@Component
public class AssetAccountProvider implements IProvider<String, AssetAccount> {

    private final Logger logger = LoggerFactory.getLogger(AssetAccountProvider.class);

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public AssetAccount get(String s) throws Exception {
        Map<String, Object> map = (Map<String, Object>) redisUtil.get(generateKey(s));
        JSONObject object = new JSONObject(map);
        AssetAccount account = new AssetAccount();
        account.setId(object.getInteger("id"));
        account.setUsername(object.getString("username"));
        account.setPassword(object.getString("password"));
        account.setAssetName(object.getString("assetName"));
        account.setAssetId(object.getInteger("assetId"));
        account.setAssetIp(object.getString("assetIp"));
        return account;
    }

    private String generateKey(String osaUser) {
        return ConstantsDto.REDIS_KEY_SESSION + osaUser + ConstantsDto.REDIS_KEY_SSO_ACCOUNT_INFO;
    }
}
