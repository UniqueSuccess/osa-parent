package com.goldencis.osa.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户和用户组关联表
 * </p>
 *
 * @author limingchao
 * @since 2018-10-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_usergroup")
public class UserUsergroup extends Model<UserUsergroup> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户guid
     */
    private String userGuid;

    /**
     * 用户组guid
     */
    private Integer usergroupId;

    public UserUsergroup() {
    }

    public UserUsergroup(String userGuid, Integer usergroupId) {
        this.userGuid = userGuid;
        this.usergroupId = usergroupId;
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
