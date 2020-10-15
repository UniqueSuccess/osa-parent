package com.goldencis.osa.common.utils;

import java.util.Arrays;
import java.util.List;

/**
 * 日志类型
 */
public enum LogType {
    /**
     * 系统操作：
     * "系统操作--新建",1; "系统操作--删除",2; "系统操作--编辑",3; "系统操作--查看",4 ;"系统操作--登录",5;
     * "系统操作--退出",6; "系统操作--播放",7; "系统操作--阻断",8; "系统操作--回放",9;"系统操作--导入",19;"系统操作--导出",20;
     * 授权：
     * "授权--添加",10； "授权--删除",11; "授权--撤销",18
     * 审批：
     * "审批--通过",12；"审批--拒绝",13
     * 策略：
     * "策略--命中命令",14；"策略--命中登录时间",15
     * 运维：
     * "运维--登录",16； "运维--登出",17； 其他, 99
     */
    SYSTEM_ADD("新建",1),SYSTEM_DELETE("删除",2),SYSTEM_UPDATE("编辑",3),
    SYSTEM_SELECT("查看",4),SYSTEM_LOGININ("登录",5),SYSTEM_LOGINOUT("退出",6),
    SYSTEM_MONITOR_REALTIME_VIEW("播放",7),SYSTEM_MONITOR_REALTIME_BLOCK("阻断",8),
    SYSTEM_MONITOR_HISTORY_VIEW("回放",9),SYSTEM_IMPORT("导入",19),SYSTEM_EXPORT("导出",20),
    GRANTED_ADD("新建",10), GRANTED_DELETE("删除",11), GRANTED_REVOKE("撤销",18),
    APPROVAL_AUTHORIZED("通过",12),APPROVAL_REJECTED("拒绝",13),
    STRATEGY_COMMAND("命令",14),STRATEGY_LOGIN("登录",15),
    OPERATION_LOGININ("登录",16),OPERATION_LOGINOUT("退出",17),OTHER("其他",99) ;

    private String name;
    private Integer value;

    LogType(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    /**
     * 返回系统日志-- 操作类型
     */
    public static List<LogType> getAllSystemOperateLogType(){
        return Arrays.asList(SYSTEM_ADD, SYSTEM_DELETE,SYSTEM_UPDATE,
                SYSTEM_SELECT,SYSTEM_LOGININ,SYSTEM_LOGINOUT,
                SYSTEM_MONITOR_REALTIME_VIEW, SYSTEM_MONITOR_REALTIME_BLOCK, SYSTEM_MONITOR_HISTORY_VIEW,SYSTEM_IMPORT,SYSTEM_EXPORT);
    }

    /**
     * 返回授权类型
     */
    public static List<LogType> getAllGrantedLogType(){
        return Arrays.asList(GRANTED_ADD, GRANTED_DELETE,GRANTED_REVOKE);
    }

    /**
     * 返回审批类型
     */
    public static List<LogType> getAllApprovalLogType(){
        return Arrays.asList(APPROVAL_AUTHORIZED, APPROVAL_REJECTED);
    }

    /**
     * 返回策略类型
     */
    public static List<LogType> getAllStrategyLogType(){
        return Arrays.asList(STRATEGY_COMMAND, STRATEGY_LOGIN);
    }

    /**
     * 返回运维类型
     */
    public static List<LogType> getAllOperationLogType(){
        return Arrays.asList(OPERATION_LOGININ, OPERATION_LOGINOUT);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
