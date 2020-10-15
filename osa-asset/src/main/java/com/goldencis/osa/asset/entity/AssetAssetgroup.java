package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 设备和设备组关联表
 * </p>
 *
 * @author limingchao
 * @since 2018-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_asset_assetgroup")
public class AssetAssetgroup extends Model<AssetAssetgroup> {

    private static final long serialVersionUID = 1L;

    /**
     * 设备id
     */
    private Integer assetId;

    /**
     * 设备组id
     */
    private Integer assetgroupId;


    @Override
    protected Serializable pkVal() {
        return this.assetId;
    }

}
