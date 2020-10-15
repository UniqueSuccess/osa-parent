package com.goldencis.osa.common.constants;

import java.io.File;

/**
 * Created by limingchao on 2018/9/25.
 */
public class ConstantsDto {

    public static final Integer CONST_TRUE = 1;
    public static final Integer CONST_FALSE = 0;
    public static final Integer CONST_ERROR = -1;

    public static final Integer RESPONSE_SUCCESS = 0;
    public static final Integer RESPONSE_FALSE = -1;
    public static final Integer RESPONSE_ERROR = 500;

    //返回实体的属性
    public static final String RESULT_MSG = "msg";
    public static final String RESULT_DATA = "data";

    //开关常量
    public static final String SWITCH_ON = "on";
    public static final String SWITCH_OFF = "off";

    // 账户启用
    public static final Integer ACCOUNT_STATUS_ENABLE = 11;
    // 账号锁定
    public static final Integer ACCOUNT_STATUS_LOCK = 10;
    // 账号停用
    public static final Integer ACCOUNT_STATUS_DISABLE = 12;

    //资源开放相关的资源文件配置
    public static final String SECURITY_FILE_PATH = "classpath:static/dataFiles/security.xml";
    public static final String SECURITY_NONE = "/security/http/@pattern";

    //排序规则，升序或降序
    public static final String ORDER_TYPE_ASC = "asc";
    public static final String ORDER_TYPE_DESC = "desc";

    public static final String REAL_PATH = "realPath";
    public static final String CONTEXT_PATH = "contextPath";
    /**
     * 字典表中表示设备编码的type字段
     */
    public static final String TYPE_ASSET_ENCODE = "SBBM";
    /**
     * 字典表中表示角色类型的type字段
     */
    public static final String TYPE_ROLE_TYPE = "ROLE_TYPE";
    /**
     * 字典表中表示SSO规则类型的type字段
     */
    public static final String TYPE_SSO_RULE_TYPE = "SSO_RULE_TYPE";
    /**
     * 字典表中表示SSO规则属性的type字段
     */
    public static final String TYPE_SSO_RULE_ATTR = "SSO_RULE_ATTR";

    //顶级部门
    public static final Integer SUPER_GROUP = 1;

    public static final Integer LEVEL_SUPER_GROUP = 0;
    public static final Integer LEVEL_ONE = 1;
    public static final Integer LEVEL_TWO = 1;
    public static final String SEPARATOR_COMMA = ",";
    public static final String SEPARATOR_SEMICOLON = ";";
    /**
     * 角色类型:系统账号
     */
    public static final String TYPE_ADMIN = "admin";
    /**
     * 角色类型:普通用户
     */
    public static final String TYPE_NORMAL = "normal";
    /**
     * 角色类型：自定义角色
     */
    public static final String TYPE_CUSTOM = "custom";
    /**
     * 开启账号托管
     */
    public static final int ACCOUNT_TRUSTEESHIP_TRUE = 1;
    /**
     * 关闭账号托管
     */
    public static final int ACCOUNT_TRUSTEESHIP_FALSE = 0;

    //默认密码
    public static final String DEFAULT_PWD = "$2a$10$NOybm9GHJdIRukROlEBjsOfgZun//.uPOKnyQ5XWej07H4/hhN9kq";

    //授权相关
    //授权文件名称
    public static final String AUTH_FILE_NAME = "osaauthorized.auth";

    public static final String READ_OSA_AUTH_FILE_NAME = "readosaauth.out";

    public static final String LONG_TIME_LIMIT = "永久";

    public static final String PROCJECT_IDENTIFICATION = "osa";

    //策略--黑白名单，1黑名单，2白名单
    public static final int STRATEGY_MEMBERLIST_BLACK = 1;
    public static final int STRATEGY_MEMBERLIST_WHITE = 2;

    //策略--命令类型，1阻断会话命令，2需审核命令，3禁止执行命令，4 正常指令
    public static final int STRATEGY_COMMAND_BLOCK = 1;
    public static final int STRATEGY_COMMAND_PENDING = 2;
    public static final int STRATEGY_COMMAND_PROHIBIT = 3;
    public static final int STRATEGY_COMMAND_NORMAL = 4;

    //授权设备 1代表整个设备组权限，2代表整个设备权限，3代表账户权限
    public static final int GRANTED_ASSETTYPE_ASSETGROUP = 1;
    public static final int GRANTED_ASSETTYPE_ASSET = 2;
    public static final int GRANTED_ASSETTYPE_ASSETACCOUNT = 3;

    //授权用户类型 1用户，2用户组
    public static final int GRANTED_USERTYPE_USER = 1;
    public static final int GRANTED_USERTYPE_USERGROUP = 2;

    //状态：0代表待审批，1代表已授权，-1代表审批拒绝
    public static final int APPROVAL_PENDING = 0;
    public static final int APPROVAL_AUTHORIZED = 1;
    public static final int APPROVAL_REJECTED = -1;

    //Redis中登录信息对应的分类
    public static final String REDIS_KEY_SESSION = "session:";
    public static final String REDIS_KEY_SSO_LOGON_INFO = ":logoninfo";
    public static final String REDIS_KEY_SSO_USER_INFO = ":userinfo";
    public static final String REDIS_KEY_SSO_ACCOUNT_INFO = ":accountinfo";
    public static final String REDIS_KEY_SSO_SESSION_LOGON_ID = "logonid:";
    public static final String REDIS_KEY_SSO_DEVUNIQUE = "devunique";
    public static final String REDIS_KEY_APPROVAL = ":approval";
    public static final long REDIS_APPROVAL_DEFAULT_EXPIRE = 30*60;//超时时间 秒s --> 30分钟
    /**
     * 存储json格式的发布规则
     */
    public static final String REDIS_KEY_RULE = ":rule";
    /**
     * redis中存储设备标示以及sessionId的后缀
     */
    public static final String REDIS_KEY_SSO_UNIQUE = ":unique";

    //监控回放文件路径及后缀
    public static final String MONITOR_FILE_PATH = "C:\\Users\\shigd\\Desktop\\osa";
    public static final String MONITOR_FILE_SUFFIX = ".replay.gz";

    //审批类型(1 命令添加； 2 授权添加； 3 删除授权； 4 删除授权设备, 5 删除设备账号, 6 删除设备组)
    public static final int APPROVAL_COMMAND_ADD= 1;
    public static final int APPROVAL_GRANTED_ADD = 2;
    public static final int APPROVAL_GRANTED_DELETE = 3;
    public static final int APPROVAL_GRANTED_DELETE_ASSET = 4;
    public static final int APPROVAL_GRANTED_DELETE_ACCOUNT = 5;
    public static final int APPROVAL_GRANTED_DELETE_ASSET_GROUP = 6;

    //授权类型 类型：1代表(设备->用户)，2代表(设备组->用户)，3代表(设备->用户组)，4代表(设备组->用户组)，5代表(删除设备),6代表(删除设备账号),7代表 （删除设备组）
    public static final int GRANTEDMETHOD_USER_ASSET = 1;
    public static final int GRANTEDMETHOD_USER_ASSETGROUP = 2;
    public static final int GRANTEDMETHOD_USERGROUP_ASSET = 3;
    public static final int GRANTEDMETHOD_USERGROUP_ASSETGROUP = 4;
    public static final int GRANTEDMETHOD_DELETE_ASSET = 5;
    public static final int GRANTEDMETHOD_DELETE_ACCOUNT = 6;
    public static final int GRANTEDMETHOD_DELETE_ASSETGROUP = 7;

    //获取审批列表 数据类型（0命令，1授权）
    public static final int APPROVAL_DATATYPE_COMMAND = 0;
    public static final int APPROVAL_DATATYPE_GRANTED = 1;

    //获取审批列表 审批类型（0待审批，1已审批）
    public static final int APPROVAL_APPROVALTYPE_PENDING = 0;
    public static final int APPROVAL_APPROVALTYPE_APPROVED = 1;

    /**
     * 数据库t_asset_type表中,winServer2003类型的主键id
     */
    public static final int ID_WINDOW_SERVER_2003 = 23;

    //是否已结束
    public static final String IS_FINISHED = "1";

    //连接方式
    public static final String CONNECT_TYPE_COCO = "CO";
    public static final String CONNECT_TYPE_GUACAMOLE = "GA";
    public static final String CONNECT_TYPE_REMOTEAPP = "RA";

    //MQClient订阅频道格式
    public static final String MQCLIENT_SUBSCRIBE_CHANNEL = "__keyevent@0__:expired";

    /**
     * 录屏开始
     */
    public static final String PS_START = "start";
    /**
     * 录屏结束
     */
    public static final String PS_STOP = "stop";

    //日志类型（1 系统操作； 2 系统授权； 3 系统审批; 4 策略； 5运维）
    public static final int LOG_SYSTEM_OPERATE = 1;
    public static final int LOG_SYSTEM_GRANTED = 2;
    public static final int LOG_SYSTEM_APPROVAL = 3;
    public static final int LOG_OPERATION_STRATEGY = 4;
    public static final int LOG_OPERATION_OPERATE = 5;

    //用户角色pid (1 管理员； 2 操作员， 3 审计员， 4 用户)
    public static final String ROLE_SYSTEM_PID = "1";
    public static final String ROLE_OPERATOR_PID = "2";
    public static final String ROLE_AUDITOR_PID = "3";
    public static final String ROLE_USER_PID = "4";

    //操作员 授权 设备、设备组
    public static final int OPERATOR_TYPE_ASSET = 1;
    public static final int OPERATOR_TYPE_ASSETGROUP = 2;

    //角色的中类型分类：系统内置用户，运维用户，自定义用户
    public static final int ROLE_TYPE_SYSTEM = 1;
    public static final int ROLE_TYPE_OPERATOR = 2;
    public static final int ROLE_TYPE_CUSTOM = 3;

    //系统管理员--默认账号id
    public static final String USER_SYSTEM_ID = "1";
    public static final String USER_ADMIN_ID= "2";
    public static final String USER_OPERATOR_ID = "3";
    public static final String USER_AUDITOR_ID = "4";

    //权限类型
    public static final int PERMISSION_TYPE_NAVIGATION = 1;
    public static final int PERMISSION_TYPE_OPERATION = 2;

    public static final String UPLOAD_PATH = File.separator + "upload" + File.separator + "attachment" + File.separator;

    /**
     * 虚无缥缈的,为实现需求,在树结构中添加的临时节点,id的统一后缀
     */
    public static final String NIHILITY = "-nihility";
    public static final String NAME_NIHILITY = "未分组";

    //黑白名单类型
    public static final int VISITRESTRICTION_TYPE_BLACK = 1;
    public static final int VISITRESTRICTION_TYPE_WHITE = 2;

    public static final String NEVER_CRONF = "11 11 11 11 11 ? 2099";

    /**
     * 是否校验授权
     */
    public static final int ISCHECKAUTH = 0;
    /**
     * 授权文件名后缀
     */
    public static final String SUFFIX_AUTH_FILE = ".osa";
    /**
     * 授权文件名称
     */
    public static final String EXPORT_AUTH_FILE_NAME = "goldencis.osa";
    public static final String AUTH_NUM_ERROR = "设备总数超过授权数量";
}
