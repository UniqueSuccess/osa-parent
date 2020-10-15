package com.goldencis.osa.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户、设备/设备组 关联表（操作员管理设备/设备组）
 * </p>
 *
 * @author limingchao
 * @since 2018-12-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_asset_assetgroup")
public class UserAssetAssetgroup extends Model<UserAssetAssetgroup> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户主键
     */
    private String userGuid;

    /**
     * 授权类型
     */
    private Integer type;

    /**
     * 设备id
     */
    private Integer assetId;

    /**
     * 设备组id
     */
    private Integer assetgroupId;

    /**
     * 设备名称
     */
    @TableField(exist = false)
    private String assetName;

    /**
     * 设备ip
     */
    @TableField(exist = false)
    private String assetIp;

    /**
     * 设备组名称
     */
    @TableField(exist = false)
    private String assetgroupName;

    /**
     * 设备类型id
     */
    @TableField(exist = false)
    private Integer assettypeId;

    /**
     * 设备类型名称
     */
    @TableField(exist = false)
    private String assettypeName;

    /**
     * 设备类型图片
     */
    @TableField(exist = false)
    private String assettypeIcon;

    /**
     * 设备组名称
     */
    @TableField(exist = false)
    private String assetgroupPName;

    /**
     * 设备组名称
     */
    @TableField(exist = false)
    private Integer assetgroupAssetCount;

    public UserAssetAssetgroup() {
    }

    public UserAssetAssetgroup(String userGuid, Integer type, Integer assetId, Integer assetgroupId) {
        this.type = type;
        this.userGuid = userGuid;
        this.assetId = assetId;
        this.assetgroupId = assetgroupId;
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
