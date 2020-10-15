package com.goldencis.osa.session.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 单点登录配置表
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sso_configuration")
public class SsoConfiguration extends Model<SsoConfiguration> {

    private static final long serialVersionUID = 1L;

    public SsoConfiguration() {
    }

    public SsoConfiguration(String userGuid, Integer assetId, Integer accountId, Integer assetType, String configuration) {
        this.userGuid = userGuid;
        this.assetId = assetId;
        this.accountId = accountId;
        this.assetType = assetType;
        this.configuration = configuration;
    }

    /**
     * 唯一标示
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 配置所属用户的唯一标示
     */
    private String userGuid;

    /**
     * 配置所属设备的唯一标示
     */
    private Integer assetId;

    /**
     * 配置所属账户的唯一标示
     */
    private Integer accountId;

    /**
     * 设备类型
     */
    private Integer assetType;

    /**
     * 单点登录配置项，使用json格式记录，不涉及查询
     */
    private String configuration;

    /**
     * 配置关联的设备
     */
    @TableField(exist = false)
    private Asset asset;

    /**
     * 配置关联的设备账户
     */
    @TableField(exist = false)
    private AssetAccount assetAccount;

    /**
     * ssoConfiguration的json对象
     */
    @TableField(exist = false)
    private JSONObject ssoConfigurationJson;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
