package com.goldencis.osa.strategy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 策略登录时间表-定义策略登录时间基本信息
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_strategy_logintime")
public class StrategyLogintime extends Model<StrategyLogintime> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 策略guid
     */
    private String strategyId;

    /**
     * 周期类型(1周一，2 周二，3周三...7周日)
     */
    private Integer dayType;

    /**
     * 开始时间
     */
    private LocalTime startHourtime;

    /**
     * 结束时间
     */
    private LocalTime endHourtime;

    /**
     * 星期、时间对应表【废弃】
     */
    private String timeZone;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建者guid
     */
    private String createBy;

    public StrategyLogintime(String strategyId, String timeZone, LocalDateTime createTime, String createBy) {
        this.strategyId = strategyId;
        this.timeZone = timeZone;
        this.createTime = createTime;
        this.createBy = createBy;
    }

    public StrategyLogintime(String strategyId, Integer dayType, LocalTime startHourtime, LocalTime endHourtime, LocalDateTime createTime, String createBy) {
        this.strategyId = strategyId;
        this.dayType = dayType;
        this.startHourtime = startHourtime;
        this.endHourtime = endHourtime;
        this.createTime = createTime;
        this.createBy = createBy;
    }

    public StrategyLogintime() {
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
