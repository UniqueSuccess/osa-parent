package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.goldencis.osa.asset.config.AssetResourceJsonDeserializer;
import com.goldencis.osa.asset.resource.AssetResource;
import com.goldencis.osa.common.entity.FormatTree;
import com.goldencis.osa.common.entity.ResultTree;
import com.goldencis.osa.common.export.annotation.Export;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 设备
 * </p>
 *
 * @author limingchao
 * @since 2018-10-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_asset")
public class Asset extends Model<Asset> implements FormatTree {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 设备类型(关联设备类型表t_asset_type)
     */
    private Integer type;

    /**
     * 设备名称
     */
    @Export(order = 1, header = "设备名称")
    private String name;

    /**
     * 设备ip
     */
    @Export(order = 2, header = "设备IP")
    private String ip;

    /**
     * 编码
     */
    private String encode;

    /**
     * 备注
     */
    @Export(order = 4, header = "备注")
    private String remark;

    /**
     * 管理账号
     */
    private String account;
    /**
     * 管理账号的密码
     */
    private String password;
    /**
     * 是否应用程序发布器(不是:0;是:1; Windows类型特有)
     */
    private Integer isPublish;

    @JsonIgnore
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 创建者guid
     */
    @JsonIgnore
    private String createBy;

    @JsonIgnore
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建者guid
     */
    @JsonIgnore
    private String updateBy;

    /**
     * 设备分类名称
     */
    @Export(order = 0, header = "类型")
    @TableField(exist = false)
    private String typeName;
    /**
     * 所属设备组id
     */
    @TableField(exist = false)
    private Integer groupId;
    /**
     * 所属设备组名称
     */
    @Export(order = 3, header = "设备组")
    @TableField(exist = false)
    private String groupName;

    @TableField(exist = false)
    private Assetgroup assetgroup;
    /**
     * 编码名称
     */
    @TableField(exist = false)
    private String encodeName;
    /**
     * 设备账号集合
     */
    @TableField(exist = false)
    private List<AssetAccount> accounts;
    /**
     * 额外参数
     */
    @TableField(exist = false)
    @JsonDeserialize(using = AssetResourceJsonDeserializer.class)
    private AssetResource extra;

    /**
     * SSO(单点登录)规则集合
     */
    @TableField(exist = false)
    private List<SsoRule> ssoRules;
    /**
     * remoteApp中使用,
     */
    @TableField(exist = false)
    private SsoRule ssoRule;

    /**
     * 设备类型图标
     */
    @TableField(exist = false)
    private String icon;

    /**
     * 是否存在授权信息
     */
    @TableField(exist = false)
    private boolean granted;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public ResultTree formatTree() {
        if (assetgroup != null) {
            return new ResultTree();
        }
        return null;
    }
}
