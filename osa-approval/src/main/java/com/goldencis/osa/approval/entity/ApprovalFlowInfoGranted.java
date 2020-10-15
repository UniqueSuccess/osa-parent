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

/**
 * <p>
 * 具体审批流程授权关联表--定义审批授权信息
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_approval_flow_info_granted")
public class ApprovalFlowInfoGranted extends Model<ApprovalFlowInfoGranted> implements IApprovalInfo {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属审批流程id
     */
    private String flowId;

    /**
     * 授权唯一标识，对应t_granted id
     */
    private Integer grantedId;

    /**
     * 设备类型唯一标识
     */
    private Integer assettypeId;

    /**
     * 设备类型名称
     */
    private String assettypeName;

    /**
     * 设备id
     */
    private Integer assetId;

    /**
     * 设备名称
     */
    private String assetName;

    /**
     * 设备ip地址
     */
    private String assetIp;

    /**
     * 设备账号id
     */
    private Integer assetAccountId;

    /**
     * 设备账号name
     */
    private String assetAccountName;

    /**
     * 设备组id
     */
    private Integer assetgroupId;

    /**
     * 设备组名称
     */
    private String assetgroupName;

    /**
     * 设备数
     */
    private Integer assetgroupRelationNumber;

    /**
     * 设备组父级id
     */
    private Integer assetgroupPid;

    /**
     * 设备组父级名称
     */
    private String assetgroupPname;

    /**
     * 用户guid
     */
    private String userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户名
     */
    private String userUsername;

    /**
     * 用户组ids
     */
    private String usergroupIds;

    /**
     * 用户组名称s
     */
    private String usergroupNames;

    /**
     * 用户数
     */
    private Integer usergroupRelationNumber;

    /**
     * 用户组所属组id
     *
     * */
    private Integer usergroupPid;

    /**
     * 用户组所属组名称
     */
    private String usergroupPname;

    /**
     * 审批流程定义类型
     */
    @TableField(exist = false)
    private Integer definitionId;

    /**
     * 审批授权类型
     */
    @TableField(exist = false)
    private Integer grantedMethod;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
