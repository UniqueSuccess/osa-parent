package com.goldencis.osa.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统日志（操作、授权、审批）
 * </p>
 *
 * @author limingchao
 * @since 2018-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_log_system")
public class LogSystem extends Model<LogSystem> {

    private static final long serialVersionUID = 1L;

    public LogSystem() {
    }

    public LogSystem(Integer logType, String logPage, String logOperateParam, String logDesc) {
        this.logType = logType;
        this.logPage = logPage;
        this.logOperateParam = logOperateParam;
        this.logDesc = logDesc;
    }

    /**
     * 日志id
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Integer logId;

    /**
     * 操作时间
     */
    private LocalDateTime time;

    /**
     * 管理员IP
     */
    private String ip;

    /**
     * 管理员guid
     */
    private String userId;

    /**
     * 管理员username
     */
    private String userUsername;

    /**
     * 管理员姓名
     */
    private String userName;

    /**
     * 日志类型
     */
    private Integer logType;

    /**
     * 设备id
     */
    private Integer assetId;

    /**
     * 设备名称
     */
    private String assetName;

    /**
     * 设备ip
     */
    private String assetIp;

    /**
     * 对应页面
     */
    private String logPage;

    /**
     * 请求参数
     */
    private String logOperateParam;

    /**
     * 描述
     */
    private String logDesc;

    /**
     * 登录次数
     */
    @TableField(exist = false)
    private Integer loginTimes;

    /**
     * 资源运维次数
     */
    @TableField(exist = false)
    private Integer assetOperations;

    @Override
    protected Serializable pkVal() {
        return this.logId;
    }

}
