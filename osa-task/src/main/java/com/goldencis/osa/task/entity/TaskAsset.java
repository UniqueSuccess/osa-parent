package com.goldencis.osa.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.goldencis.osa.task.domain.AssetItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 定时改密计划
 * </p>
 *
 * @author limingchao
 * @since 2019-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_task_asset")
public class TaskAsset extends Model<TaskAsset> {

    private static final long serialVersionUID = 1L;

    /**
     * 数字
     */
    public static final int FLAG_NUMBER      = 0b0001;
    /**
     * 大写字母
     */
    public static final int FLAG_CAPITAL     = 0b0010;
    /**
     * 小写字母
     */
    public static final int FLAG_LOWERCASE   = 0b0100;
    /**
     * 特殊符号
     */
    public static final int FLAG_SPECIAL     = 0b1000;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 是否只执行一次
     */
    private Boolean once;

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 密码类型
     */
    private String type;

    /**
     * 用户选择固定密码时,指定的密码值
     */
    private String password;

    /**
     * 密码规则(int值1:数字,2:大写字母,4:小写字母,8:特殊符号;做加法,使用位运算匹配)
     */
    private Integer rule;

    /**
     * 密码长度
     */
    private Integer length;

    /**
     * 是否保存ftp服务器
     */
    private Boolean ftp;

    /**
     * FTP服务器地址
     */
    private String ftpAddr;

    /**
     * FTP路径
     */
    private String ftpDir;

    /**
     * FTP账号
     */
    private String ftpAccount;

    /**
     * FTP密码
     */
    private String ftpPwd;

    /**
     * 是否发送邮件
     */
    private Boolean email;
    //自动备份信息
    /**
     * 循环方式
     * @see com.goldencis.osa.task.domain.Cycle
     */
    private String cycle;
    /**
     * 如果按周循环,值为1-7;如果按月循环,值为1-28
     */
    private String day;
    /**
     * 计划执行的之间点,如01:35
     */
    private String time;

    // 资源信息,改密名单,设备id集合
    @TableField(exist = false)
    private List<String> resourceId;
    @TableField(exist = false)
    private List<String> emailAddress;
    /**
     * 资源名单集合
     */
    @TableField(exist = false)
    private List<AssetItem> assetList;
    /**
     * 数字
     */
    @TableField(exist = false)
    private Boolean number;
    /**
     * 大写字母
     */
    @TableField(exist = false)
    private Boolean capital;
    /**
     * 小写字母
     */
    @TableField(exist = false)
    private Boolean lowercase;
    /**
     * 特殊符号
     */
    @TableField(exist = false)
    private Boolean special;

    public static boolean numberFlag(int rule) {
        return (rule & FLAG_NUMBER) == FLAG_NUMBER;
    }

    public static boolean capitalFlag(int rule) {
        return (rule & FLAG_CAPITAL) == FLAG_CAPITAL;
    }

    public static boolean lowercaseFlag(int rule) {
        return (rule & FLAG_LOWERCASE) == FLAG_LOWERCASE;
    }

    public static boolean specialFlag(int rule) {
        return (rule & FLAG_SPECIAL) == FLAG_SPECIAL;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
