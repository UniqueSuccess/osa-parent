package com.goldencis.osa.session.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

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
@TableName("t_terminal_command")
public class TerminalCommand extends Model<TerminalCommand> {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 用户名
     */
    private String user;

    /**
     * 设备名称
     */
    private String asset;

    /**
     * 账户名称
     */
    private String systemUser;

    /**
     * 输入
     */
    private String input;

    /**
     * 输出
     */
    private String output;

    /**
     * 关联session_id
     */
    private String session;

    /**
     * 命令状态：0代表禁止，1代表已正常，-1代表审批拒绝，2代表审批通过
     */
    private Integer status;

    /**
     * 审批id
     */
    private Integer approveId;

    /**
     * 提交时间
     */
    private Integer timestamp;

    @TableField(exist = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    private String orgId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
