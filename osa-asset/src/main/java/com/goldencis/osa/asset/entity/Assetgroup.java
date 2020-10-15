package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.goldencis.osa.common.entity.FormatTree;
import com.goldencis.osa.common.entity.ResultTree;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 设备组
 * </p>
 *
 * @author limingchao
 * @since 2018-10-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_assetgroup")
public class Assetgroup extends Model<Assetgroup> implements FormatTree {

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
     * 上级id
     */
    @JsonProperty(value = "pId")
    private Integer pid;

    /**
     * 上级名称
     */
    @TableField(exist = false)
    private String pname;

    /**
     * 设备数量
     */
    @TableField(exist = false)
    private Integer assetCount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 路径
     */
    private String treePath;

    /**
     * 节点层级,必须从0开始
     */
    private Integer level;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 前端树结构中，是否选中
     */
    @TableField(exist = false)
    private boolean checked;

    /**
     * 创建者guid
     */
    private String createBy;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public ResultTree formatTree() {
        return new ResultTree(this.getId().toString(), this.getName(), this.getLevel(), this.getPid() != null ? this.getPid().toString() : null, false,false,false);
    }
}
