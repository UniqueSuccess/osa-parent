package com.goldencis.osa.approval.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *  具体审批流程主表--定义审批公共信息
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_approval_flow")
public class ApprovalFlow extends Model<ApprovalFlow> {

    private static final long serialVersionUID = 1L;

    /**
     * 所属审批流程id
     */
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * 审批流程定义类型
     */
    private Integer definitionId;

    /**
     * 授权方式：1代表(设备->用户)，2代表(设备组->用户)，3代表(设备->用户组)，代表(设备组->用户组)，5代表(删除设备)
     */
    private Integer grantedMethod;

    /**
     * 审批流程名称
     */
    private String name;

    /**
     * 审批流程的执行状态，-1为审批被驳回，0为审批进行中，1为审批通过
     */
    private Integer status;

    /**
     * 审批流程执行的环节id
     */
    private Integer pointId;

    /**
     * 申请人guid
     */
    private String applicantId;

    /**
     * 申请人用户名
     */
    private String applicantUsername;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 涉及对象数量
     */
    @JsonProperty(value = "grantedAssetNum")
    private int relationNum = 0;

    /**
     * 申请原因
     */
    private String reason;

    /**
     * 申请提交时间
     */
    private LocalDateTime applyTime;

    /**
     * 审批终结时间
     */
    private LocalDateTime finishTime;

    /**
     * 审批配置超时时间
     */
    private Long approvalExpireTime;

    /**
     * 审批配置结果
     */
    private Integer approvalExpireResult;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @TableField(exist = false)
    private List<ApprovalFlowInfoGranted> approvalFlowInfoGrantedList;

    @TableField(exist = false)
    private List<ApprovalFlowInfoCommand> approvalFlowInfoCommands;

    /**
     * 命令审批-- 命令内容
     */
    @TableField(exist = false)
    private String commandContent;

    /**
     * 审批备注
     */
    @TableField(exist = false)
    private String approvalRemark;

    /**
     * 审批有效时间(返回s)
     */
    @TableField(exist = false)
    private Long effectiveTime;


    public ApprovalFlow(String id ,Integer definitionId, String name, Integer pointId, String applicantId, String applicantUsername, String applicantName, String reason, LocalDateTime applyTime) {
        this.id = id;
        this.definitionId = definitionId;
        this.name = name;
        this.pointId = pointId;
        this.applicantId = applicantId;
        this.applicantUsername = applicantUsername;
        this.applicantName = applicantName;
        this.reason = reason;
        this.applyTime = applyTime;
    }

    public ApprovalFlow() {
    }
}
