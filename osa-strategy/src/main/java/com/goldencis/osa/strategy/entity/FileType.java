package com.goldencis.osa.strategy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author limingchao
 * @since 2019-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_file_type")
public class FileType extends Model<FileType> {

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
     * 类型图标
     */
    private String icon;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }


    @TableField(exist = false)
    boolean checked = false;

}
