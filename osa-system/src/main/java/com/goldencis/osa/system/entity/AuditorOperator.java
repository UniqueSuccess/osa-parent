package com.goldencis.osa.system.entity;

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
 * 审计员、操作员 关联表
 * </p>
 *
 * @author limingchao
 * @since 2018-12-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_auditor_operator")
public class AuditorOperator extends Model<AuditorOperator> {

    private static final long serialVersionUID = 1L;

    /**
     * 审判员guid
     */
    @TableId(value = "auditor_guid" )
    private String auditorGuid;

    /**
     * 操作员guid
     */
    private String operatorGuid;

    public AuditorOperator() {
    }

    public AuditorOperator(String auditorGuid, String operatorGuid) {
        this.auditorGuid = auditorGuid;
        this.operatorGuid = operatorGuid;
    }

    @Override
    protected Serializable pkVal() {
        return this.auditorGuid;
    }

}
