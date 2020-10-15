package com.goldencis.osa.session.entity;

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
 *
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_terminal_session")
public class TerminalSession extends Model<TerminalSession> {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标示
     */
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * 用户名
     */
    private String userUsername;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户地址
     */
    private String remoteAddr;

    /**
     * 用户guid
     */
    private String userGuid;

    /**
     * 目标设备名称
     */
    private String assetName;

    /**
     * 目标设备ip
     */
    private String assetAddr;

    /**
     * 设备id
     */
    private Integer assetId;

    /**
     * 目标设备账号
     */
    private String systemUser;

    private String loginFrom;

    /**
     * 是否结束
     */
    private Boolean isFinished;

    /**
     * 是否结束
     */
    private Boolean isBlocking;

    /**
     * 是否有回放
     */
    private Boolean hasReplay;

    /**
     * 是否有命令回放
     */
    private Boolean hasCommandReplay;

    /**
     * 是否有命令
     */
    private Boolean hasCommand;

    /**
     * 是否有文件
     */
    private Boolean hasFile;

    /**
     * 开始时间
     */
    private LocalDateTime dateStart;

    /**
     * 结束时间
     */
    private LocalDateTime dateEnd;

    /**
     * 最后活动时间
     */
    private LocalDateTime dateLastActive;

    private String orgId;

    /**
     * 持续时间
     */
    @TableField(exist = false)
    private String duration;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 终端id
     */
    private String terminalId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
