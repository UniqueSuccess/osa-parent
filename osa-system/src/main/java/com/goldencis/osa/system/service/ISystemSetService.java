package com.goldencis.osa.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.common.entity.MailConfig;
import com.goldencis.osa.core.entity.Platform;
import com.goldencis.osa.system.domain.LogServer;
import com.goldencis.osa.system.domain.AccessControl;
import com.goldencis.osa.system.entity.ApprovalExpire;
import com.goldencis.osa.system.entity.SystemSet;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shigd
 * @since 2018-12-27
 */
public interface ISystemSetService extends IService<SystemSet> {

    /**
     * 数据库中邮件服务器配置的code
     */
    String MAIL_CONFIG = "MailConfig";

    /**
     * 数据库中准入控制配置的code
     */
    String ACCESS_CONTROL = "AccessControlConfig";

    /**
     * 更新管控平台json
     * @param json
     */
    void updatePlatformJson(String json);

    String convertPlatformToJson(Platform platform) throws Exception;

    /**
     * 保存网口状态信息
     * @param netstat
     */
    void updateLogServer(LogServer netstat);

    /**
     * 获取网口状态信息
     * @return 网口状态信息
     */
    Map<String,Object> getNetStat();

    /**
     * 获取配置信息
     * @param code
     * @return
     */
    Map<String,Object> getPlatform(String code);

    /**
     * 获取审批配置
     * @return
     */
    ApprovalExpire getApprovalExpire();

    /**
     * 备份升级文件夹
     * @param dirPath 需要备份的文件夹目录
     */
    void backupFolder(String dirPath);

    /**
     * 更新邮件服务器信息
     * @param mailConfig
     */
    void updateMailConfig(MailConfig mailConfig);

    MailConfig getMailConfig() throws Exception;

    /**
     * 获取准入控制的配置信息
     * @return
     */
    AccessControl getAccessControlConfig();

    /**
     * 更新准入控制的配置信息
     * @param config
     */
    void updateAccessControlConfig(AccessControl config);

}
