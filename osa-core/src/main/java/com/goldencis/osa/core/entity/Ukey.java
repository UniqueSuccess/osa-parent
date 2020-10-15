package com.goldencis.osa.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * USBKey
 * </p>
 *
 * @author wangmc
 * @since 2019-01-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_ukey")
public class Ukey extends Model<Ukey> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * USBKey名称
     */
    private String name;

    /**
     * USBKey标识
     */
    private String sign;

    /**
     * 绑定用户guid
     */
    @TableField(value = "user_guid", strategy = FieldStrategy.IGNORED)
    private String userGuid;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 绑定人员名称
     */
    @TableField(exist = false)
    private String uname;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
