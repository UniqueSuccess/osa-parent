package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author limingchao
 * @since 2018-10-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_asset_account")
public class AssetAccount extends Model<AssetAccount> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 资产id
     */
    private Integer assetId;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否托管(是:1,否:0)
     */
    private Integer trusteeship;
    /**
     * 设备名称
     */
    @TableField(exist = false)
    private String trusteeshipName;
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
     * 设备类型名称
     */
    @TableField(exist = false)
    private String assetTypeName;
    /**
     * 设备组名称
     */
    @TableField(exist = false)
    private String assetGroupName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
