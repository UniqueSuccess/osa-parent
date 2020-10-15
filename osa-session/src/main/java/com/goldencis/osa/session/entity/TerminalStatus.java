package com.goldencis.osa.session.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author limingchao
 * @since 2018-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_terminal_status")
public class TerminalStatus extends Model<TerminalStatus> {

    private static final long serialVersionUID = 1L;

    public TerminalStatus() {
    }

    public TerminalStatus(String id, Integer sessionOnline, Double cpuUsed, Double memoryUsed, Integer connections, Integer threads, Double bootTime, LocalDateTime dateCreated, String terminalId) {
        this.id = id;
        this.sessionOnline = sessionOnline;
        this.cpuUsed = cpuUsed;
        this.memoryUsed = memoryUsed;
        this.connections = connections;
        this.threads = threads;
        this.bootTime = bootTime;
        this.dateCreated = dateCreated;
        this.terminalId = terminalId;
    }

    /**
     * 唯一标示
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 在线的session数量
     */
    private Integer sessionOnline;

    /**
     * cpu使用率
     */
    private Double cpuUsed;

    /**
     * 内存使用率
     */
    private Double memoryUsed;

    /**
     * 连接数
     */
    private Integer connections;

    /**
     * 线程数
     */
    private Integer threads;

    /**
     * 启动时间
     */
    private Double bootTime;

    /**
     * 数据提交时间
     */
    private LocalDateTime dateCreated;

    /**
     * 关联终端id
     */
    private String terminalId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
