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
 * 终端表
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_terminal")
public class Terminal extends Model<Terminal> {

    private static final long serialVersionUID = 1L;

    public Terminal() {
    }

    public Terminal(String id, String name, String remoteAddr, Integer sshPort, Integer httpPort, Boolean isAccepted, Boolean isDeleted, LocalDateTime dateCreated) {
        this.id = id;
        this.name = name;
        this.remoteAddr = remoteAddr;
        this.sshPort = sshPort;
        this.httpPort = httpPort;
        this.isAccepted = isAccepted;
        this.isDeleted = isDeleted;
        this.dateCreated = dateCreated;
    }

    /**
     * 唯一标示
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 终端名称
     */
    private String name;

    /**
     * 终端地址
     */
    private String remoteAddr;

    /**
     * ssh端口
     */
    private Integer sshPort;

    /**
     * HTTP端口
     */
    private Integer httpPort;

    /**
     * 是否对接
     */
    private Boolean isAccepted;

    /**
     * 是否删除(逻辑)
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime dateCreated;

    /**
     * 备注
     */
    private String comment;

    /**
     * 用户guid
     */
    private String userguid;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
