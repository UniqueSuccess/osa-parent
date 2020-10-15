package com.goldencis.osa.core.entity;

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
 * 角色权限表
 * </p>
 *
 * @author limingchao
 * @since 2018-12-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_role_permission")
public class RolePermission extends Model<RolePermission> {

    private static final long serialVersionUID = 1L;

    public RolePermission() {
    }

    public RolePermission(String roleGuid, Integer permissionId) {
        this.roleGuid = roleGuid;
        this.permissionId = permissionId;
    }

    /**
     * 角色guid
     */
    @TableId(value = "role_guid", type = IdType.UUID)
    private String roleGuid;

    /**
     * 权限id
     */
    private Integer permissionId;


    @Override
    protected Serializable pkVal() {
        return this.roleGuid;
    }

}
