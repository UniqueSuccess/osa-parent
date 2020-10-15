package com.goldencis.osa.strategy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 策略命令表-定义策略命令基本信息
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_strategy_command")
public class StrategyCommand extends Model<StrategyCommand> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 命令类型，1阻断会话命令，2待审核命令，3禁止执行命令
     */
    private Integer type;

    /**
     * 命令内容
     */
    private String commandContent;

    /**
     * 策略guid
     */
    private String strategyId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人guid
     */
    private String createBy;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public StrategyCommand(Integer type, String commandContent, String strategyId, LocalDateTime createTime, String createBy) {
        this.type = type;
        this.commandContent = commandContent;
        this.strategyId = strategyId;
        this.createTime = createTime;
        this.createBy = createBy;
    }

    public StrategyCommand() {
    }
}
