package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.goldencis.osa.asset.resource.AssetResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 设备从表(数据库类型设备)
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_asset_db")
public class AssetDb extends Model<AssetDb> implements AssetResource {

    private static final long serialVersionUID = 1L;

    /**
     * 设备id
     */
    private Integer assetId;

    /**
     * 数据库名
     */
    private String dbName;

    /**
     * 服务名(目前这个字段无用)
     */
    @Deprecated
    private String serverName;
    /**
     * 端口
     */
    private int port;

    /**
     * 系统账号登录(启用:on;不启用:off)
     */
    private String systemAccountLogin;

    /**
     * 应用程序发布器
     */
    private Integer publish;

    /**
     * 运维客户端
     */
    private Integer operationTool;

    @TableField(exist = false)
    private String publishName;
    @TableField(exist = false)
    private String operationToolName;
    @Override
    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    @Override
    public Integer getAssetId() {
        return this.assetId;
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
