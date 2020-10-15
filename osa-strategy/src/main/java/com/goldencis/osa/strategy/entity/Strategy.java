package com.goldencis.osa.strategy.entity;

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
import java.util.List;

/**
 * <p>
 *  策略表-定义策略基本信息
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_strategy")
public class Strategy extends Model<Strategy> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键.唯一标示
     */
    @TableId(value = "guid", type = IdType.UUID)
    private String guid;

    /**
     * 策略名称
     */
    private String name;

    /**
     * 会话选项
     */
    private String sessionType;

    /**
     * RDP选项
     */
    private String rdp;

    /**
     * SSH选项
     */
    private String ssh;

    /**
     * 屏幕水印 json
     */
    private String screenWatermark;

    /**
     * 文件审计 json
     */
    private String fileMon;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建者guid
     */
    private String createBy;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改者guid
     */
    private String updtaeBy;

    @Override
    protected Serializable pkVal() {
        return this.guid;
    }

    /**
     * 策略命令控制（阻断会话命令）
     */
    @TableField(exist = false)
    private List<String> strategyCommandBlock;

    /**
     * 策略命令控制（待审核会话命令）
     */
    @TableField(exist = false)
    private List<String> strategyCommandPending;

    /**
     * 策略命令控制（禁止会话命令）
     */
    @TableField(exist = false)
    private List<String> strategyCommandProhibit;

    /**
     * 登录时间限定
     */
    @TableField(exist = false)
    private  List<StrategyLogintimeWeekday> strategyLoginTime;

    /**
     * 文件类型树
     */
    @TableField(exist = false)
    private  Object fileMonTree;


}
