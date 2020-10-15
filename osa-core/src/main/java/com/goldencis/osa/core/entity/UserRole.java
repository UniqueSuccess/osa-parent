package com.goldencis.osa.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户角色关联表
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_role")
public class UserRole extends Model<UserRole> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户guid
     */
    private String userGuid;

    /**
     * 角色guid
     */
    private String roleGuid;

    public UserRole() {
    }

    public UserRole(String userGuid, String roleGuid) {
        this.userGuid = userGuid;
        this.roleGuid = roleGuid;
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
