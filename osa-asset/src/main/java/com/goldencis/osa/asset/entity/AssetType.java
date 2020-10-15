package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 设备类型
 * </p>
 *
 * @author limingchao
 * @since 2018-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_asset_type")
public class AssetType extends Model<AssetType> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 父级类型id
     */
    @JsonProperty(value = "pId")
    private Integer pid;

    /**
     * 节点层级,必须从0开始
     */
    private Integer level;

    /**
     * 排序用
     */
    private Integer compositor;

    /**
     * 是否启用(启动:1,不启用:0)
     */
    private Boolean status;
    /**
     * 前端界面样式（取值:cs,bs,db,net）
     */
    private String style;
    /**
     * 图标
     */
    private String icon;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
