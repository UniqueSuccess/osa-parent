package com.goldencis.osa.core.entity;

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
 * 功能操作表
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_operation")
public class Operation extends Model<Operation> implements Resource{

    private static final long serialVersionUID = 1L;

    /**
     * 主键（自增长）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 排序
     */
    private Integer compositor;

    /**
     * 功能操作编码
     */
    private String code;

    /**
     * 功能操作url样式
     */
    private String urlPartten;

    /**
     * 请求方式：GET，POST，PUT，DELETE
     */
    private String method;

    /**
     * 功能描述
     */
    private String description;

    /**
     * 父级功能Id
     */
    private Integer parentId;

    /**
     * 是否可见
     */
    private boolean visible;

    /**
     * 角色授权时是否可见
     */
    private boolean grantVisible;

    /**
     * 功能归属的页签id，没有特定归属可为空，全部页签通用为0。
     */
    private Integer navigationId;

    /**
     * 权限类型：1表示页签，2表示功能。
     */
    @TableField(exist = false)
    private int type = 2;

    /**
     * 是否勾选
     */
    @TableField(exist = false)
    private boolean checked = false;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
