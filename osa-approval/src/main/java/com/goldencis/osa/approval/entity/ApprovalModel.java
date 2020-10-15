package com.goldencis.osa.approval.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 审批流程模板步骤--定义审批流程
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_approval_model")
public class ApprovalModel extends Model<ApprovalModel> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 审批环节名称
     */
    private String name;

    /**
     * 审批人guid，多个时以";"隔开
     */
    private String approvers;

    /**
     * 所属审批流程id
     */
    private Integer definitionId;

    /**
     * 上一个环节的id，每个流程的起始节点该字段为0
     */
    private Integer seniorId;

    /**
     * 是否标准环节,1为标准环节，0为严格环节
     */
    private Integer standard;

    /**
     * 最后修改时间
     */
    private LocalDateTime modifyTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
