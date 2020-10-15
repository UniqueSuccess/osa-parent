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
 * 用户部门关联
 * </p>
 *
 * @author limingchao
 * @since 2018-10-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_department")
public class UserDepartment extends Model<UserDepartment> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户guid
     */
    private String userGuid;

    /**
     * 部门Id
     */
    private Integer departmentId;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
