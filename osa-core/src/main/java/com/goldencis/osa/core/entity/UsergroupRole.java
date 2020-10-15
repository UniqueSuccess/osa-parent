package com.goldencis.osa.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户组角色关联表
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_usergroup_role")
public class UsergroupRole extends Model<UsergroupRole> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户组guid
     */
    @TableId(value = "usergroup_guid", type = IdType.AUTO)
    private String usergroupGuid;

    /**
     * 角色guid
     */
    private String roleGuid;


    @Override
    protected Serializable pkVal() {
        return this.usergroupGuid;
    }

}
