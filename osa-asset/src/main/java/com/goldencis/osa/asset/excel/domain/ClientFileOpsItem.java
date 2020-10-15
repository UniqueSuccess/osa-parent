package com.goldencis.osa.asset.excel.domain;

import com.goldencis.osa.asset.excel.annotation.Export;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报表 行为审计（文件操作）
 * @author wangtt
 * @since 2019-01-28
 */
@Data
public class ClientFileOpsItem  {

    /**
     * 主键
     */
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
    @Export(desc = "时间", order = 8)
    private String time;
    /**
     * 用户唯一标识
     */
    private String userGuid;

    @Export(desc = "客户端IP", order = 3)
    private String localIp;

    private String usergroupIds;

    @Export(desc = "用户组", order = 0)
    private String usergroupNames;

    @Export(desc = "用户", order = 1)
    private String userName;

    @Export(desc = "姓名", order = 2)
    private String userFullName;

    /**
     * t_log_client主键唯一标识
     */
    private String logId;

    @Export(desc = "文件名", order = 4)
    private String fileName;

    /**
     * 文件类型
     */
    private Integer optype;

    /**
     * 文件操作类型【 0 打开  1移动  2复制 3删除文件 4重命名 5创建目录 6新建文件 7修改文件 8删除目录  9恢复(从回收站)】
     */
    @Export(desc = "操作类型", order = 5)
    private String optypeName;

    @Export(desc = "源路径", order = 6)
    private String srcPath;

    @Export(desc = "目标路径", order = 7)
    private String dstPath;

    /**
     * 进程名称
     */
    private String proc;

}
