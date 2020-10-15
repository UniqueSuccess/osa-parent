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
 * @since 2018-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_terminal_command_replay")
public class TerminalCommandReplay extends Model<TerminalCommandReplay> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键（自增长）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件存储路径
     */
    private String filepath;

    /**
     * 会话的唯一标示
     */
    private String sessionId;

    /**
     * 回放提交时间
     */
    private LocalDateTime createTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
