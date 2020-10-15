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
 * 设备从表(C/S应用)
 * </p>
 *
 * @author limingchao
 * @since 2018-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_asset_cs")
public class AssetCs extends Model<AssetCs> implements AssetResource {

    private static final long serialVersionUID = 1L;

    /**
     * 设备id
     */
    private Integer assetId;

    /**
     * 应用程序发布器
     */
    private Integer publish;

    /**
     * 运维工具
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
