package com.goldencis.osa.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户组表
 * </p>
 *
 * @author limingchao
 * @since 2018-10-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_usergroup")
public class Usergroup extends Model<Usergroup> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键.唯一标示
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户组名称
     */
    private String name;

    /**
     * 所属用户组id
     */
    @JsonProperty(value = "pId")
    private Integer pid;

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
     * 创建者guid
     */
    private String createBy;
    /**
     * 该用户组下的用户数量
     */
    @TableField(exist = false)
    private Integer userCount;
    /**
     * 所属用户组名称
     */
    @JsonProperty(value = "pName")
    @TableField(exist = false)
    private String pname;
    @TableField(exist = false)
    private boolean checked = false;
    /**
     * 是否展开子节点
     */
    @TableField(exist = false)
    private boolean expand = false;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
