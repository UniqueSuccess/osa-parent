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
 * @since 2018-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_terminal_replay")
public class TerminalReplay extends Model<TerminalReplay> {

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
     * 该会话开始的相对时间，以秒为单位
     */
    private Integer sessionStart;

    /**
     * 该会话结束的相对时间，以秒为单位
     */
    private Integer sessionEnd;

    /**
     * 回访文件顺序
     */
    private Integer seq;

    /**
     * 视频开始绝对时间14位 04d-02d-02d 02d:02d:02d
     */
    private LocalDateTime startTime;

    /**
     * 视频结束绝对时间14位
     */
    private LocalDateTime endTime;

    /**
     * 视频提交时间14位
     */
    private LocalDateTime createTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
