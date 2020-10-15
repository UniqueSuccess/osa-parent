package com.goldencis.osa.report.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangtt
 * @since 2019-01-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_log_client")
public class LogClient extends Model<LogClient> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 日志类型
     */
    private String dataType;

    /**
     * 设备唯一标识
     */
    private String devUnique;

    /**
     * 计算机名
     */
    private String computerName;

    /**
     * 时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    /**
     * 用户唯一标识
     */
    private String userGuid;

    /**
     * 客户端ip
     */
    private String localIp;

    /**
     * 用户组
     */
    @TableField(exist = false)
    private String usergroupIds;

    /**
     * 用户组
     */
    @TableField(exist = false)
    private String usergroupNames;

    /**
     * 用户名
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户姓名
     */
    @TableField(exist = false)
    private String userFullName;

    /**
     * t_log_client主键唯一标识
     */
    @TableField(exist = false)
    private String logId;

    /**
     * 文件名称
     */
    @TableField(exist = false)
    private String fileName;

    /**
     * 文件操作类型【 0 打开  1移动  2复制 3删除文件 4重命名 5创建目录 6新建文件 7修改文件 8删除目录  9恢复(从回收站)】
     */
    @TableField(exist = false)
    private Integer optype;

    /**
     * 文件操作类型【 0 打开  1移动  2复制 3删除文件 4重命名 5创建目录 6新建文件 7修改文件 8删除目录  9恢复(从回收站)】
     */
    @TableField(exist = false)
    private String optypeName;

    /**
     * 源路径
     */
    @TableField(exist = false)
    private String srcPath;

    /**
     * 目标路径
     */
    @TableField(exist = false)
    private String dstPath;

    /**
     * 进程名称
     */
    @TableField(exist = false)
    private String proc;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
