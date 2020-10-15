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
 * 具体审批流程审批结果关联表--定义审批结果信息
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_approval_detail")
public class ApprovalDetail extends Model<ApprovalDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 审批环节名称
     */
    private String name;

    /**
     * 所属审批流程id
     */
    private String flowId;

    /**
     * 所属审批节点id
     */
    private Integer pointId;

    /**
     * 上一个环节的id，每个流程的起始节点该字段为0
     */
    private Integer seniorId;

    /**
     * 该节点审批人guid
     */
    private String approver;

    /**
     * 审批结果，-1为审批被驳回，0为审批进行中，1为审批通过，若null则未开始。
     */
    private Integer result;

    /**
     * 审批意见
     */
    private String remark;

    /**
     * 是否标准环节
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

    public ApprovalDetail(){}

    public ApprovalDetail(String name, String flowId, Integer pointId, Integer seniorId, String approver, Integer result, String remark, Integer standard, LocalDateTime modifyTime) {
        this.name = name;
        this.flowId = flowId;
        this.pointId = pointId;
        this.seniorId = seniorId;
        this.approver = approver;
        this.result = result;
        this.remark = remark;
        this.standard = standard;
        this.modifyTime = modifyTime;
    }
}
