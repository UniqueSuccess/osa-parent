package com.goldencis.osa.approval.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.goldencis.osa.approval.handle.IApprovalInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 具体审批流程命令关联表--定义审批命令信息
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_approval_flow_info_command")
public class ApprovalFlowInfoCommand extends Model<ApprovalFlowInfoCommand> implements IApprovalInfo {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 流程表唯一标识
     */
    private String flowId;

    /**
     * 需审核命令唯一标识，对应t_terminal_command id
     */
    private String terminalCommandId;

    /**
     * 需审核命令唯一标识，对应t_terminal_command id
     */
    @TableField(exist = false)
    private String terminalCommandContent;

    /**
     * 申请人 --id
     */
    @TableField(exist = false)
    private String applicantId;

    /**
     * 申请人 --用户名
     */
    @TableField(exist = false)
    private String applicantUsername;

    /**
     * 申请人 -- 姓名
     */
    @TableField(exist = false)
    private String applicantName;

    /**
     * 申请时间
     */
    @TableField(exist = false)
    private LocalDateTime applyTime;

    /**
     * 用户 -- guid
     */
    @TableField(exist = false)
    private String userId;

    /**
     * 用户 -- 用户名
     */
    @TableField(exist = false)
    private String userUsername;

    /**
     * 用户 -- 名称
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户 -- 用户组ids
     */
    @TableField(exist = false)
    private String usergroupIds;

    /**
     * 用户 -- 用户组名字s
     */
    @TableField(exist = false)
    private String usergroupNames;

    /**
     * 用户 -- 用户远程ip
     */
    @TableField(exist = false)
    private String userRemoteIp;

    /**
     * 设备 -- 设备组id
     */
    @TableField(exist = false)
    private Integer assetgroupId;

    /**
     * 设备 -- 设备组名字
     */
    @TableField(exist = false)
    private String assetgroupName;

    /**
     * 设备 -- 设备id
     */
    @TableField(exist = false)
    private Integer assetId;

    /**
     * 设备 -- 设备ip
     */
    @TableField(exist = false)
    private String assetIp;

    /**
     * 设备 -- 设备名
     */
    @TableField(exist = false)
    private String assetName;

    /**
     * 设备 -- 设备类型名字
     */
    @TableField(exist = false)
    private String assettypeName;

    /**
     * 设备 -- 设备类型id
     */
    @TableField(exist = false)
    private Integer assettypeId;

    /**
     * 设备 -- 设备账号
     */
    @TableField(exist = false)
    private String assetAccountName;

    /**
     * 审批结果
     */
    @TableField(exist = false)
    private String approvalRemark;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
