package com.goldencis.osa.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.common.entity.MailConfig;
import com.goldencis.osa.common.utils.FileUtils;
import com.goldencis.osa.common.utils.IpUtil;
import com.goldencis.osa.common.utils.JsonUtils;
import com.goldencis.osa.core.entity.*;
import com.goldencis.osa.core.service.IDictionaryService;
import com.goldencis.osa.system.access.AccessControlManager;
import com.goldencis.osa.system.domain.AccessControl;
import com.goldencis.osa.system.domain.LogServer;
import com.goldencis.osa.system.entity.ApprovalExpire;
import com.goldencis.osa.system.entity.SystemSet;
import com.goldencis.osa.system.mapper.SystemSetMapper;
import com.goldencis.osa.system.service.ISystemSetService;
import com.goldencis.osa.system.utils.EthToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author shigd
 * @since 2018-12-27
 */
@Service
public class SystemSetServiceImpl extends ServiceImpl<SystemSetMapper, SystemSet> implements ISystemSetService {

    @Autowired
    private SystemSetMapper systemSetMapper;
    @Autowired
    IDictionaryService dictionaryService;
    @Autowired
    private AccessControlManager accessControlManager;
    private static final String BG_NAME = "bg_img.png";

    /**
     * 更新管控平台json
     *
     * @param json
     */
    @Override
    public void updatePlatformJson(String json) {
        baseMapper.updatePlatformJson(json);
    }

    @Override
    public String convertPlatformToJson(Platform platform) throws Exception {
        Objects.requireNonNull(platform);
        // 校验ip
        checkIpInvalid(platform);
        Security security = platform.getSecurity();
        if ((Objects.isNull(security.getNumber()) || !security.getNumber())
                && (Objects.isNull(security.getCapital()) || !security.getCapital())
                && (Objects.isNull(security.getLowercase()) || !security.getLowercase())) {
            throw new IllegalArgumentException("数字,小写字母,大写字母至少勾选一个");
        }
        return JsonUtils.objectToJson(platform);
    }

    @Override
    public void updateLogServer(LogServer logServer) {
        JSONObject json = new JSONObject();
        json.put("name", logServer.getName());
        json.put("url", logServer.getUrl());
        baseMapper.updateLogServer(json.toJSONString());
    }

    @Override
    public void updateMailConfig(MailConfig mailConfig) {
        baseMapper.updateMailConfig(JsonUtils.objectToJson(mailConfig));
    }

    @Override
    public MailConfig getMailConfig() throws Exception {
        SystemSet set = this.getOne(new QueryWrapper<SystemSet>().eq("code", MAIL_CONFIG));
        String content = set.getContent();
        MailConfig config = JsonUtils.jsonToPojo(content, MailConfig.class);
        return config;
    }

    @Override
    public AccessControl getAccessControlConfig() {
        SystemSet set = this.getOne(new QueryWrapper<SystemSet>().eq("code", ACCESS_CONTROL));
        try {
            return JsonUtils.jsonToPojo(set.getContent(), AccessControl.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateAccessControlConfig(AccessControl config) {
        List<AccessControl.IpBlack> ips = config.getIps();
        if (!CollectionUtils.isEmpty(ips)) {
            for (AccessControl.IpBlack ip : ips) {
                if (!IpUtil.validate(ip.getIp())) {
                    throw new IllegalArgumentException("非法的IP地址：" + ip.getIp());
                }
            }
        }
        String json = JsonUtils.objectToJson(config);
        SystemSet entity = new SystemSet();
        entity.setContent(json);
        entity.setCode(ACCESS_CONTROL);
        entity.setType(ACCESS_CONTROL);
        entity.setUpdateTime(LocalDateTime.now());
        this.update(entity, new QueryWrapper<SystemSet>().eq("code", ACCESS_CONTROL));

        // 更新本地文件
        if (Objects.nonNull(config.getStatus()) && config.getStatus()) {
            accessControlManager.turnOn();
        } else {
            accessControlManager.turnOff();
        }
        accessControlManager.local();
        accessControlManager.ports(config.getPorts());
        accessControlManager.servers(ips);
    }

    @Override
    public Map<String, Object> getNetStat() {
        Map<String, Object> data = new HashMap<>();
        //设备负载信息
        try {
            data.put("eth0", EthToolUtil.checkEthx("eth0") ? "1" : "0");
            data.put("eth1", EthToolUtil.checkEthx("eth1") ? "1" : "0");
            data.put("eth2", EthToolUtil.checkEthx("eth2") ? "1" : "0");
            data.put("eth3", EthToolUtil.checkEthx("eth3") ? "1" : "0");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public Map<String, Object> getPlatform(String code) {
        return systemSetMapper.getPlatform(code);
    }

    /**
     * 获取审批配置
     *
     * @return
     */
    @Override
    public ApprovalExpire getApprovalExpire() {
        ApprovalExpire approvalExpire = new ApprovalExpire();
        List<Dictionary> approval_expire_time = dictionaryService.getDictionaryListByType("APPROVAL_EXPIRE_TIME");
        List<Dictionary> approval_expire_result = dictionaryService.getDictionaryListByType("APPROVAL_EXPIRE_RESULT");
        approvalExpire.setApprovalExpireTime(approval_expire_time);
        approvalExpire.setApprovalExpireResult(approval_expire_result);
        return approvalExpire;
    }

    @Override
    public void backupFolder(String dirPath) {
        File file = new File(dirPath);
        if (file.exists()) {
            //将当前的文件夹备份。
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-hhmmss");
            String date = dateTimeFormatter.format(LocalDateTime.now());
            FileUtils.backupFolder(dirPath, dirPath + "_bak_" + date);
        }
    }

    private void checkIpInvalid(Platform platform) {
        Access access = platform.getAccess();
        Login login = access.getLogin();
        for (String ip : login.getIp()) {
            if (!IpUtil.validate(ip)) {
                throw new IllegalArgumentException(String.format("登录黑名单ip格式不正确 %s", ip));
            }
        }
        Assets resource = access.getResource();
        for (String ip : resource.getIp()) {
            if (!IpUtil.validate(ip)) {
                throw new IllegalArgumentException(String.format("设备黑名单ip格式不正确 %s", ip));
            }
        }
    }


}
