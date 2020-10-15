package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.goldencis.osa.asset.resource.AssetResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 设备从表(类型为网络设备)
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_asset_net")
public class AssetNet extends Model<AssetNet> implements AssetResource {

    private static final long serialVersionUID = 1L;

    /**
     * 设备id
     */
    private Integer assetId;

    /**
     * 协议类型(FTP,VNC,SSH等)
     */
    private String protocolType;

    /**
     * 端口号
     */
    private Integer port;

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
