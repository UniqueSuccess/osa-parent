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
import java.time.LocalDateTime;

/**
 * <p>
 * 设备授权表
 * </p>
 *
 * @author limingchao
 * @since 2018-11-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_granted")
public class Granted extends Model<Granted> {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 授权类型：1代表整个设备组权限，2代表整个设备权限，3代表账户权限
     */
    private Integer type;

    /**
     * 授权设备组id
     */
    private Integer assetgroupId;

    /**
     * 授权设备id
     */
    private Integer assetId;

    /**
     * 授权账户id
     */
    private Integer accountId;

    /**
     * 状态：0代表 待审批，1代表已授权，-1代表审批拒绝
     */
    private Integer status;

    /**
     * 用户guid
     */
    private String userId;

    /**
     * 用户组guid
     */
    private Integer usergroupId;

    /**
     * 审批id
     */
    private String approveId;

    /**
     * 是否删除：0默认不删除；1 删除
     */
    private Integer isdelete;

    /**
     * 创建人guid
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 设备组名称
     */
    @TableField(exist = false)
    private String assetgroupname;

    /**
     * 设备名称
     */
    @TableField(exist = false)
    private String assetname;

    /**
     * 设备ip地址
     */
    @TableField(exist = false)
    private String assetip;

    /**
     * 设备账号名
     */
    @TableField(exist = false)
    private String accountname;

    /**
     * 设备类型
     */
    @TableField(exist = false)
    private String assettypename;

    /**
     * 账号权限（是否托管）
     */
    @TableField(exist = false)
    private String trusteeship;

    /**
     * 审核状态
     */
    @TableField(exist = false)
    private String statusname;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public Granted(Integer type, Integer assetgroupId) {
        this.type = type;
        this.assetgroupId = assetgroupId;
    }

    public Granted(Integer type, Integer assetId, Integer accountId) {
        this.type = type;
        this.assetId = assetId;
        this.accountId = accountId;
    }

    public Granted() {
    }
}
