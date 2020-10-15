/*
Navicat MySQL Data Transfer

Source Server         : 10.10.16.235
Source Server Version : 50713
Source Host           : 10.10.16.235:3306
Source Database       : goldencis_osa

Target Server Type    : MYSQL
Target Server Version : 50713
File Encoding         : 65001

Date: 2019-01-08 16:44:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_approval_definition`
-- ----------------------------
DROP TABLE IF EXISTS `t_approval_definition`;
CREATE TABLE `t_approval_definition` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
  `name` varchar(200) NOT NULL COMMENT '审批流程名称',
  `is_default` int(11) DEFAULT NULL COMMENT '是否默认审批流程',
  `modify_time` timestamp NULL DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_approval_definition
-- ----------------------------
INSERT INTO `t_approval_definition` VALUES ('1', '需审核命令审批流程', '1', '2018-11-14 17:38:57');
INSERT INTO `t_approval_definition` VALUES ('2', '添加授权审批流程', '1', '2018-11-14 17:39:20');
INSERT INTO `t_approval_definition` VALUES ('3', '删除授权审批流程', '1', '2018-11-14 17:39:41');
INSERT INTO `t_approval_definition` VALUES ('4', '删除含授权的设备', '1', '2018-11-23 15:46:54');
INSERT INTO `t_approval_definition` VALUES ('5', '删除含授权的设备账号', '1', '2018-12-24 18:02:54');
INSERT INTO `t_approval_definition` VALUES ('6', '删除含授权的设备组', '1', '2018-12-26 15:22:17');

-- ----------------------------
-- Table structure for `t_approval_detail`
-- ----------------------------
DROP TABLE IF EXISTS `t_approval_detail`;
CREATE TABLE `t_approval_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL COMMENT '审批环节名称',
  `flow_id` varchar(36) NOT NULL COMMENT '所属审批流程id',
  `point_id` int(11) NOT NULL COMMENT '所属审批节点id',
  `senior_id` int(11) NOT NULL COMMENT '上一个环节的id，每个流程的起始节点该字段为0',
  `approver` varchar(36) NOT NULL COMMENT '该节点审批人guid',
  `result` int(10) DEFAULT NULL COMMENT '审批结果，-1为审批被驳回，0为审批进行中，1为审批通过，若null则未开始。',
  `remark` varchar(2000) DEFAULT NULL COMMENT '审批意见',
  `standard` int(10) NOT NULL COMMENT '是否标准环节',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=524 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_approval_detail
-- ----------------------------

-- ----------------------------
-- Table structure for `t_approval_flow`
-- ----------------------------
DROP TABLE IF EXISTS `t_approval_flow`;
CREATE TABLE `t_approval_flow` (
  `id` varchar(36) NOT NULL COMMENT '所属审批流程id',
  `definition_id` int(11) NOT NULL COMMENT '审批流程定义类型',
  `granted_method` int(11) DEFAULT NULL COMMENT '授权方式：1代表(设备->用户)，2代表(设备组->用户)，3代表(设备->用户组)，代表(设备组->用户组)，5代表(删除设备)',
  `name` varchar(200) DEFAULT NULL COMMENT '审批流程名称',
  `status` int(10) DEFAULT NULL COMMENT '审批流程的执行状态，-1为审批被驳回，0为审批进行中，1为审批通过',
  `point_id` int(11) DEFAULT NULL COMMENT '审批流程执行的环节id',
  `applicant_id` varchar(36) DEFAULT NULL COMMENT '申请人guid',
  `applicant_username` varchar(50) DEFAULT NULL COMMENT '申请人用户名',
  `applicant_name` varchar(200) DEFAULT NULL COMMENT '申请人姓名',
  `relation_num` int(11) DEFAULT NULL COMMENT '涉及对象数量',
  `reason` varchar(2000) DEFAULT NULL COMMENT '申请原因',
  `apply_time` timestamp NULL DEFAULT NULL COMMENT '申请提交时间',
  `finish_time` timestamp NULL DEFAULT NULL COMMENT '审批终结时间',
  `approval_expire_time` bigint(20) DEFAULT NULL COMMENT '审批超时时间，单位秒',
  `approval_expire_result` int(11) DEFAULT NULL COMMENT '审批超时处理结果',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_approval_flow
-- ----------------------------

-- ----------------------------
-- Table structure for `t_approval_flow_info_command`
-- ----------------------------
DROP TABLE IF EXISTS `t_approval_flow_info_command`;
CREATE TABLE `t_approval_flow_info_command` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `flow_id` varchar(36) DEFAULT NULL COMMENT '流程表唯一标识',
  `terminal_command_id` varchar(36) DEFAULT NULL COMMENT '需审核命令唯一标识，对应t_terminal_command id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_approval_flow_info_command
-- ----------------------------

-- ----------------------------
-- Table structure for `t_approval_flow_info_granted`
-- ----------------------------
DROP TABLE IF EXISTS `t_approval_flow_info_granted`;
CREATE TABLE `t_approval_flow_info_granted` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `flow_id` varchar(36) NOT NULL COMMENT '所属审批流程id',
  `granted_id` int(11) DEFAULT NULL COMMENT '授权唯一标识，对应t_granted id',
  `assettype_id` int(11) DEFAULT NULL COMMENT '设备类型唯一标识',
  `assettype_name` varchar(50) DEFAULT NULL COMMENT '设备类型名称',
  `asset_id` int(11) DEFAULT NULL COMMENT '设备id',
  `asset_name` varchar(50) DEFAULT NULL COMMENT '设备名称',
  `asset_ip` varchar(50) DEFAULT NULL COMMENT '设备ip地址',
  `asset_account_id` int(11) DEFAULT NULL COMMENT '设备账号id',
  `asset_account_name` varchar(50) DEFAULT NULL COMMENT '设备账号name',
  `assetgroup_id` int(11) DEFAULT NULL COMMENT '设备组id',
  `assetgroup_name` varchar(50) DEFAULT NULL COMMENT '设备组名称',
  `assetgroup_relation_number` int(11) DEFAULT NULL COMMENT '设备数',
  `assetgroup_pid` int(11) DEFAULT NULL COMMENT '设备所属组id',
  `assetgroup_pname` varchar(36) DEFAULT NULL COMMENT '设备所属组名字',
  `user_id` varchar(36) DEFAULT NULL COMMENT '用户guid',
  `user_username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户名称',
  `usergroup_ids` varchar(600) DEFAULT NULL COMMENT '用户组id',
  `usergroup_names` varchar(1000) DEFAULT NULL COMMENT '用户组名称',
  `usergroup_relation_number` int(11) DEFAULT NULL COMMENT '用户数',
  `usergroup_pid` int(11) DEFAULT NULL COMMENT '用户组所属组pid',
  `usergroup_pname` varchar(50) DEFAULT NULL COMMENT '用户组所属组名字',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=921 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_approval_flow_info_granted
-- ----------------------------

-- ----------------------------
-- Table structure for `t_approval_model`
-- ----------------------------
DROP TABLE IF EXISTS `t_approval_model`;
CREATE TABLE `t_approval_model` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(36) NOT NULL COMMENT '审批环节名称',
  `approvers` varchar(2000) NOT NULL COMMENT '审批人guid，多个时以";"隔开',
  `definition_id` int(11) NOT NULL COMMENT '所属审批流程id',
  `senior_id` int(11) NOT NULL COMMENT '上一个环节的id，每个流程的起始节点该字段为0',
  `standard` int(10) NOT NULL COMMENT '是否标准环节,1为标准环节，0为严格环节',
  `modify_time` timestamp NULL DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_approval_model
-- ----------------------------
INSERT INTO `t_approval_model` VALUES ('1', '需审核命令审批流程1', '1;2', '1', '0', '1', '2018-11-14 17:47:13');
INSERT INTO `t_approval_model` VALUES ('2', '需审核命令审批流程2', '3', '1', '1', '1', '2018-11-15 09:25:13');
INSERT INTO `t_approval_model` VALUES ('3', '需审核命令审批流程3', '4', '1', '2', '1', '2018-11-15 09:28:33');
INSERT INTO `t_approval_model` VALUES ('4', '添加授权审核1', '1', '2', '0', '1', '2018-11-15 14:25:14');
INSERT INTO `t_approval_model` VALUES ('5', '添加授权审核2', '2', '2', '1', '1', '2018-11-15 14:25:44');

-- ----------------------------
-- Table structure for `t_asset`
-- ----------------------------
DROP TABLE IF EXISTS `t_asset`;
CREATE TABLE `t_asset` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` int(11) NOT NULL COMMENT '设备类型(关联字典表SBLX)',
  `name` varchar(50) NOT NULL COMMENT '设备名称',
  `ip` varchar(50) DEFAULT NULL COMMENT '设备ip',
  `encode` varchar(50) DEFAULT NULL COMMENT '编码(关联字典表SBBM)',
  `remark` text COMMENT '备注',
  `account` varchar(255) DEFAULT NULL COMMENT '管理账号',
  `password` varchar(255) DEFAULT NULL COMMENT '管理密码',
  `is_publish` tinyint(1) DEFAULT NULL COMMENT '是否应用程序发布器(不是:0;是:1; Windows类型特有)',
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(36) DEFAULT NULL COMMENT '创建人guid',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(36) DEFAULT NULL COMMENT '更新人guid',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_asset
-- ----------------------------

-- ----------------------------
-- Table structure for `t_asset_account`
-- ----------------------------
DROP TABLE IF EXISTS `t_asset_account`;
CREATE TABLE `t_asset_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_id` int(11) NOT NULL COMMENT '资产id',
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `trusteeship` tinyint(1) DEFAULT NULL COMMENT '是否托管(是:1,否:0)',
  `copy_from` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_asset_account
-- ----------------------------


-- ----------------------------
-- Table structure for `t_asset_assetgroup`
-- ----------------------------
DROP TABLE IF EXISTS `t_asset_assetgroup`;
CREATE TABLE `t_asset_assetgroup` (
  `asset_id` int(11) NOT NULL COMMENT '设备id',
  `assetgroup_id` int(11) NOT NULL COMMENT '设备组id',
  PRIMARY KEY (`asset_id`,`assetgroup_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备和设备组关联表';

-- ----------------------------
-- Records of t_asset_assetgroup
-- ----------------------------


-- ----------------------------
-- Table structure for `t_asset_bs`
-- ----------------------------
DROP TABLE IF EXISTS `t_asset_bs`;
CREATE TABLE `t_asset_bs` (
  `asset_id` int(11) NOT NULL COMMENT '设备id',
  `publish` int(11) DEFAULT NULL COMMENT '应用程序发布器',
  `operation_tool` int(11) DEFAULT NULL COMMENT '发布工具',
  `fill_out` int(11) DEFAULT NULL COMMENT '应用密码代填',
  `form_name` varchar(50) DEFAULT NULL COMMENT '表单名称',
  `form_commit_mode` varchar(50) DEFAULT NULL COMMENT '表单提交方式',
  `command` varchar(50) DEFAULT NULL COMMENT '口令属性',
  `login_url` varchar(255) DEFAULT NULL COMMENT '登录url',
  PRIMARY KEY (`asset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备从表(B/S应用)';

-- ----------------------------
-- Records of t_asset_bs
-- ----------------------------

-- ----------------------------
-- Table structure for `t_asset_cs`
-- ----------------------------
DROP TABLE IF EXISTS `t_asset_cs`;
CREATE TABLE `t_asset_cs` (
  `asset_id` int(11) NOT NULL COMMENT '设备id',
  `publish` int(11) DEFAULT NULL COMMENT '应用程序发布器',
  `operation_tool` int(11) DEFAULT NULL COMMENT '运维工具',
  PRIMARY KEY (`asset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备从表(C/S应用)';

-- ----------------------------
-- Records of t_asset_cs
-- ----------------------------

-- ----------------------------
-- Table structure for `t_asset_db`
-- ----------------------------
DROP TABLE IF EXISTS `t_asset_db`;
CREATE TABLE `t_asset_db` (
  `asset_id` int(11) NOT NULL COMMENT '设备id',
  `db_name` varchar(255) DEFAULT NULL COMMENT '数据库名',
  `server_name` varchar(255) DEFAULT NULL COMMENT '服务名(弃用)',
  `port` int(11) NOT NULL COMMENT '端口',
  `system_account_login` enum('off','on') NOT NULL COMMENT '系统账号登录(启用:on;不启用:off)',
  `publish` int(11) DEFAULT NULL COMMENT '应用程序发布器',
  `operation_tool` int(255) DEFAULT NULL COMMENT '运维客户端',
  PRIMARY KEY (`asset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备从表(数据库类型设备)';

-- ----------------------------
-- Records of t_asset_db
-- ----------------------------


-- ----------------------------
-- Table structure for `t_asset_net`
-- ----------------------------
DROP TABLE IF EXISTS `t_asset_net`;
CREATE TABLE `t_asset_net` (
  `asset_id` int(11) NOT NULL COMMENT '设备id',
  `protocol_type` varchar(10) NOT NULL COMMENT '协议类型(FTP,VNC,SSH等)',
  `port` int(11) NOT NULL COMMENT '端口号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备从表(类型为网络设备)';

-- ----------------------------
-- Records of t_asset_net
-- ----------------------------

-- ----------------------------
-- Table structure for `t_asset_type`
-- ----------------------------
DROP TABLE IF EXISTS `t_asset_type`;
CREATE TABLE `t_asset_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `pid` int(11) DEFAULT NULL COMMENT '父级类型id',
  `level` int(11) NOT NULL COMMENT '节点层级,必须从0开始',
  `compositor` int(11) DEFAULT NULL COMMENT '排序用',
  `status` tinyint(1) DEFAULT NULL COMMENT '是否启用(启动:1,不启用:0)',
  `style` enum('windows','db','bs','cs','net') DEFAULT NULL COMMENT '前端界面样式',
  `icon` varchar(255) DEFAULT NULL COMMENT '类型图标',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_asset_type
-- ----------------------------
INSERT INTO `t_asset_type` VALUES ('1', 'UNIX', null, '0', null, null, 'net', 'Linux');
INSERT INTO `t_asset_type` VALUES ('2', 'WINDOWS', null, '0', null, null, 'windows', 'windows');
INSERT INTO `t_asset_type` VALUES ('3', 'Active Dircetory', null, '0', null, '0', null, null);
INSERT INTO `t_asset_type` VALUES ('4', '网络设备', null, '0', null, null, 'net', 'net');
INSERT INTO `t_asset_type` VALUES ('5', '数据库', null, '0', null, null, 'db', 'mysql');
INSERT INTO `t_asset_type` VALUES ('6', 'C/S', null, '0', null, null, 'cs', 'cs');
INSERT INTO `t_asset_type` VALUES ('7', 'B/S', null, '0', null, null, 'bs', 'bs');
INSERT INTO `t_asset_type` VALUES ('8', '中间件', null, '0', null, '0', null, null);
INSERT INTO `t_asset_type` VALUES ('9', '主机', null, '0', null, '0', null, null);
INSERT INTO `t_asset_type` VALUES ('10', 'HA资源', null, '0', null, '0', null, null);
INSERT INTO `t_asset_type` VALUES ('11', 'Redhat', '1', '1', null, null, null, 'Linux');
INSERT INTO `t_asset_type` VALUES ('12', 'SUSE', '1', '1', null, null, null, 'Linux');
INSERT INTO `t_asset_type` VALUES ('13', 'Ubuntu', '1', '1', null, null, null, 'Linux');
INSERT INTO `t_asset_type` VALUES ('14', 'FREE BSD', '1', '1', null, null, null, 'Linux');
INSERT INTO `t_asset_type` VALUES ('15', 'IBM AIX', '1', '1', null, null, null, 'Linux');
INSERT INTO `t_asset_type` VALUES ('16', 'HP-Unix', '1', '1', null, null, null, 'Linux');
INSERT INTO `t_asset_type` VALUES ('17', 'SCO Unix', '1', '1', null, null, null, 'Linux');
INSERT INTO `t_asset_type` VALUES ('18', 'Solaris', '1', '1', null, null, null, 'Linux');
INSERT INTO `t_asset_type` VALUES ('19', 'CentOS', '1', '1', null, null, null, 'Linux');
INSERT INTO `t_asset_type` VALUES ('20', '红旗', '1', '1', null, null, null, 'Linux');
INSERT INTO `t_asset_type` VALUES ('21', 'Fedora', '1', '1', null, null, null, 'Linux');
INSERT INTO `t_asset_type` VALUES ('22', '其他Linux', '1', '1', null, null, null, 'Linux');
INSERT INTO `t_asset_type` VALUES ('23', 'Windows2003', '2', '1', null, null, null, 'windows');
INSERT INTO `t_asset_type` VALUES ('24', 'Windows2008', '2', '1', null, null, null, 'windows');
INSERT INTO `t_asset_type` VALUES ('25', 'Windows2012', '2', '1', null, null, null, 'windows');
INSERT INTO `t_asset_type` VALUES ('26', 'Windows2016', '2', '1', null, null, null, 'windows');
INSERT INTO `t_asset_type` VALUES ('27', '其他Windows系统', '2', '1', null, null, null, 'windows');
INSERT INTO `t_asset_type` VALUES ('28', '思科路由交换', '4', '1', null, null, null, 'net');
INSERT INTO `t_asset_type` VALUES ('29', '华为路由交换', '4', '1', null, null, null, 'net');
INSERT INTO `t_asset_type` VALUES ('30', 'H3C路由交换', '4', '1', null, null, null, 'net');
INSERT INTO `t_asset_type` VALUES ('31', '锐捷路由交换', '4', '1', null, null, null, 'net');
INSERT INTO `t_asset_type` VALUES ('32', '中兴路由交换', '4', '1', null, null, null, 'net');
INSERT INTO `t_asset_type` VALUES ('33', '贝尔路由交换', '4', '1', null, null, null, 'net');
INSERT INTO `t_asset_type` VALUES ('34', '迈普路由交换', '4', '1', null, null, null, 'net');
INSERT INTO `t_asset_type` VALUES ('35', '其他路由交换', '4', '1', null, null, null, 'net');
INSERT INTO `t_asset_type` VALUES ('36', 'Oracle', '5', '1', null, null, null, 'oracle');
INSERT INTO `t_asset_type` VALUES ('37', 'DB2', '5', '1', null, null, null, 'DB2');
INSERT INTO `t_asset_type` VALUES ('38', 'Informix', '5', '1', null, null, null, 'informix');
INSERT INTO `t_asset_type` VALUES ('39', 'SQL Server 2008', '5', '1', null, null, null, 'SQL_srr');
INSERT INTO `t_asset_type` VALUES ('40', 'SQL Server 2005', '5', '1', null, null, null, 'SQL_srr');
INSERT INTO `t_asset_type` VALUES ('41', 'SQL Server 2000', '5', '1', null, null, null, 'SQL_srr');
INSERT INTO `t_asset_type` VALUES ('42', 'Sybase', '5', '1', null, null, null, 'sybase');
INSERT INTO `t_asset_type` VALUES ('43', 'MySQL', '5', '1', null, null, null, 'mysql');
INSERT INTO `t_asset_type` VALUES ('44', '其他数据库系统', '5', '1', null, null, null, 'mysql');
INSERT INTO `t_asset_type` VALUES ('45', 'WebLogic', '8', '1', null, '0', null, null);
INSERT INTO `t_asset_type` VALUES ('46', 'AD Server', '3', '1', null, '0', null, null);

-- ----------------------------
-- Table structure for `t_assetgroup`
-- ----------------------------
DROP TABLE IF EXISTS `t_assetgroup`;
CREATE TABLE `t_assetgroup` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `pid` int(11) DEFAULT NULL COMMENT '上级id',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `tree_path` varchar(50) DEFAULT NULL COMMENT '路径',
  `level` int(11) DEFAULT NULL COMMENT '节点层级,必须从0开始',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者guid',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_assetgroup
-- ----------------------------
INSERT INTO `t_assetgroup` VALUES ('1', '公司资产', null, '顶级备注', ',', '0', '1', '2018-11-24 04:44:22', '1');

-- ----------------------------
-- Table structure for `t_auditor_operator`
-- ----------------------------
DROP TABLE IF EXISTS `t_auditor_operator`;
CREATE TABLE `t_auditor_operator` (
  `auditor_guid` varchar(36) COLLATE utf8_bin NOT NULL COMMENT '审判员guid',
  `operator_guid` varchar(36) COLLATE utf8_bin NOT NULL COMMENT '操作员guid',
  PRIMARY KEY (`auditor_guid`,`operator_guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_auditor_operator
-- ----------------------------
INSERT INTO `t_auditor_operator` VALUES ('1', '1');
INSERT INTO `t_auditor_operator` VALUES ('4', '3');

-- ----------------------------
-- Table structure for `t_back_up`
-- ----------------------------
DROP TABLE IF EXISTS `t_back_up`;
CREATE TABLE `t_back_up` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(128) NOT NULL COMMENT '备份名称',
  `type` varchar(4) NOT NULL DEFAULT '1' COMMENT '备份类型 1 实时 0 定时',
  `mark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `file_path` varchar(2000) DEFAULT NULL COMMENT '备份文件路径',
  `status` varchar(4) DEFAULT '0' COMMENT '状态 0 备份中 1 已完成',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_back_up
-- ----------------------------

-- ----------------------------
-- Table structure for `t_client`
-- ----------------------------
DROP TABLE IF EXISTS `t_client`;
CREATE TABLE `t_client` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `guid` char(64) NOT NULL DEFAULT '' COMMENT '终端设备标识',
  `uuid` char(64) DEFAULT NULL COMMENT '终端用户标识',
  `type` int(255) NOT NULL,
  `last_activity` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `online` int(11) unsigned zerofill NOT NULL,
  `ip` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `guid` (`guid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_client
-- ----------------------------


-- ----------------------------
-- Table structure for `t_department`
-- ----------------------------
DROP TABLE IF EXISTS `t_department`;
CREATE TABLE `t_department` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '部门名称',
  `pid` int(11) DEFAULT NULL COMMENT '父级部门Id',
  `department_remark` varchar(300) COLLATE utf8_bin DEFAULT NULL COMMENT '备注信息',
  `owner` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '部门负责人',
  `department_tel` varchar(15) COLLATE utf8_bin DEFAULT NULL COMMENT '部门电话',
  `tree_path` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '路径',
  `level` int(11) DEFAULT NULL COMMENT '节点层级,必须从0开始',
  `status` int(11) unsigned DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='部门信息表';

-- ----------------------------
-- Records of t_department
-- ----------------------------
INSERT INTO `t_department` VALUES ('1', '顶级部门', null, '顶级部门备注', '顶级部门拥有者', '13066668888', ',', '0', '1');
INSERT INTO `t_department` VALUES ('2', '未分组', '1', null, '', '', ',1,', '1', '1');

-- ----------------------------
-- Table structure for `t_dictionary`
-- ----------------------------
DROP TABLE IF EXISTS `t_dictionary`;
CREATE TABLE `t_dictionary` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(50) DEFAULT NULL COMMENT '源数据类型',
  `name` varchar(50) DEFAULT NULL COMMENT '字典名',
  `value` int(11) DEFAULT NULL COMMENT '排序',
  `nickname` text COMMENT '字典值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_dictionary
-- ----------------------------
INSERT INTO `t_dictionary` VALUES ('1', 'SBBM', 'UTF-8', '1', '');
INSERT INTO `t_dictionary` VALUES ('2', 'HHLX', '会话二次审批', '1', '');
INSERT INTO `t_dictionary` VALUES ('3', 'HHLX', '会话备注', '2', '');
INSERT INTO `t_dictionary` VALUES ('4', 'HHLX', '历史会话审计', '3', '');
INSERT INTO `t_dictionary` VALUES ('5', 'HHLX', '实时会话监控', '4', '');
INSERT INTO `t_dictionary` VALUES ('6', 'RDP', '键盘记录', '1', '');
INSERT INTO `t_dictionary` VALUES ('7', 'RDP', '打印机/驱动器映射', '2', '');
INSERT INTO `t_dictionary` VALUES ('8', 'RDP', '使用剪切板下载', '3', '');
INSERT INTO `t_dictionary` VALUES ('9', 'RDP', '使用剪切板上传', '4', '');
INSERT INTO `t_dictionary` VALUES ('10', 'SSH', 'X11转发', '1', '');
INSERT INTO `t_dictionary` VALUES ('11', 'SSH', '隧道转发', '2', '');
INSERT INTO `t_dictionary` VALUES ('12', 'SSH', '打开SFTP通道', '3', '');
INSERT INTO `t_dictionary` VALUES ('13', 'SSH', '请求exec', '4', '');
INSERT INTO `t_dictionary` VALUES ('14', 'ROLE_TYPE', 'admin', '1', '        \r\n');
INSERT INTO `t_dictionary` VALUES ('15', 'ROLE_TYPE', 'normal', '2', null);
INSERT INTO `t_dictionary` VALUES ('16', 'ROLE_TYPE', 'custom', '3', null);
INSERT INTO `t_dictionary` VALUES ('17', 'SSO_RULE_TYPE', 'Oracle', '1', null);
INSERT INTO `t_dictionary` VALUES ('18', 'SSO_RULE_TYPE', 'DB2', '2', null);
INSERT INTO `t_dictionary` VALUES ('19', 'SSO_RULE_TYPE', 'Informix', '3', null);
INSERT INTO `t_dictionary` VALUES ('20', 'SSO_RULE_TYPE', 'SQL Server 2008', '4', null);
INSERT INTO `t_dictionary` VALUES ('21', 'SSO_RULE_TYPE', 'SQL Server 2005', '5', null);
INSERT INTO `t_dictionary` VALUES ('22', 'SSO_RULE_TYPE', 'SQL Server 2000', '6', null);
INSERT INTO `t_dictionary` VALUES ('23', 'SSO_RULE_TYPE', 'Sybase', '7', null);
INSERT INTO `t_dictionary` VALUES ('24', 'SSO_RULE_TYPE', 'MySQL', '8', null);
INSERT INTO `t_dictionary` VALUES ('25', 'SSO_RULE_TYPE', 'B/S', '9', NULL);
INSERT INTO `t_dictionary` VALUES ('35', 'SSO_CONFIGURATION_TEMPLATE', 'UNIX', '1', '{\"logonToolsType\":\"client\",\"logonTools\":\"XShell\",\"openType\":\"tab\",\"logonMethod\":\"character\",\"protocol\":\"ssh\",\"port\":22}');
INSERT INTO `t_dictionary` VALUES ('36', 'SSO_CONFIGURATION_TEMPLATE', 'WINDOWS', '2', '{\"logonMethod\":\"graphical\",\"protocol\":\"rdp\",\"port\":3389,\"resolution\":\"fullScreen\",\"shareDriver\":\"\",\"shareShearPlate\":true}');
INSERT INTO `t_dictionary` VALUES ('37', 'SSO_CONFIGURATION_TEMPLATE', '网络设备', '4', '{\"logonToolsType\":\"client\",\"logonTools\":\"XShell\",\"openType\":\"tab\",\"logonMethod\":\"character\",\"protocol\":\"ssh\",\"port\":22}');
INSERT INTO `t_dictionary` VALUES ('38', 'SSO_CONFIGURATION_TEMPLATE', '数据库设备', '5', '{\"publishId\":-1,\"protocol\":\"rdp\",\"port\":3389,\"ssoRuleId\":-1}');
INSERT INTO `t_dictionary` VALUES ('39', 'SSO_CONFIGURATION_TEMPLATE', 'C/S设备', '6', '{\"publishId\":-1,\"protocol\":\"rdp\",\"port\":3389,\"ssoRuleId\":-1}');
INSERT INTO `t_dictionary` VALUES ('40', 'SSO_CONFIGURATION_TEMPLATE', 'B/S设备', '7', '{\"publishId\":-1,\"protocol\":\"rdp\",\"port\":3389,\"ssoRuleId\":-1}');
INSERT INTO `t_dictionary` VALUES ('41', 'DEFAULT_ROLE_PERMISSION', '权限查询', '42', null);
INSERT INTO `t_dictionary` VALUES ('42', 'DEFAULT_ROLE_PERMISSION', '菜单查询', '44', null);
INSERT INTO `t_dictionary` VALUES ('43', 'DEFAULT_ROLE_PERMISSION', '登录成功跳转', '45', null);
INSERT INTO `t_dictionary` VALUES ('44', 'DEFAULT_ROLE_PERMISSION', '获取当前登录用户', '47', null);
INSERT INTO `t_dictionary` VALUES ('45', 'DEFAULT_ROLE_PERMISSION', '更新密码', '48', null);
INSERT INTO `t_dictionary` VALUES ('46', 'DEFAULT_ROLE_PERMISSION', '获取密码限制', '49', null);
INSERT INTO `t_dictionary` VALUES ('47', 'backUpType', '定时', '0', null);
INSERT INTO `t_dictionary` VALUES ('48', 'backUpType', '实时', '1', null);
INSERT INTO `t_dictionary` VALUES ('49', 'APPROVAL_EXPIRE_TIME', '30分钟', '1', null);
INSERT INTO `t_dictionary` VALUES ('50', 'APPROVAL_EXPIRE_TIME', '1小时', '2', null);
INSERT INTO `t_dictionary` VALUES ('51', 'APPROVAL_EXPIRE_TIME', '1天', '3', null);
INSERT INTO `t_dictionary` VALUES ('52', 'APPROVAL_EXPIRE_TIME', '7天', '4', null);
INSERT INTO `t_dictionary` VALUES ('53', 'APPROVAL_EXPIRE_TIME', '不限时', '-1', null);
INSERT INTO `t_dictionary` VALUES ('54', 'APPROVAL_EXPIRE_RESULT', '通过', '1', null);
INSERT INTO `t_dictionary` VALUES ('55', 'APPROVAL_EXPIRE_RESULT', '拒绝', '-1', null);
INSERT INTO `t_dictionary` VALUES ('56', 'JS_ACCOUNT', '跳板机默认账号', NULL, 'goldencis');
INSERT INTO `t_dictionary` VALUES ('57', 'JS_PASSWORD', '跳板机默认密码', NULL, 'G0ldencis');
INSERT INTO `t_dictionary` VALUES ('58', 'DEFAULT_ROLE_PERMISSION', '更新session的关联设备标识', '293', null);

-- ----------------------------
-- Table structure for `t_file_type`
-- ----------------------------
DROP TABLE IF EXISTS `t_file_type`;
CREATE TABLE `t_file_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `pid` int(11) DEFAULT NULL COMMENT '父级类型id',
  `level` int(11) DEFAULT NULL COMMENT '节点层级,必须从0开始',
  `compositor` int(11) DEFAULT NULL COMMENT '排序用',
  `status` tinyint(1) DEFAULT NULL COMMENT '是否启用(启动:1,不启用:0)',
  `icon` varchar(255) DEFAULT NULL COMMENT '类型图标',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_file_type
-- ----------------------------
INSERT INTO `t_file_type` VALUES ('1', 'Word', null, '0', null, null, null);
INSERT INTO `t_file_type` VALUES ('2', 'Excel', null, '0', null, null, null);
INSERT INTO `t_file_type` VALUES ('3', 'PowerPoint', null, '0', null, null, null);
INSERT INTO `t_file_type` VALUES ('4', '记事本', null, '0', null, null, null);
INSERT INTO `t_file_type` VALUES ('5', '写字板', null, '0', null, null, null);
INSERT INTO `t_file_type` VALUES ('6', '压缩工具', null, '0', null, null, null);
INSERT INTO `t_file_type` VALUES ('7', 'Adobe Reader', null, '0', null, null, null);
INSERT INTO `t_file_type` VALUES ('8', '图片', null, '0', null, null, null);
INSERT INTO `t_file_type` VALUES ('9', '*.doc', '1', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('10', '*.wps', '1', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('11', '*.docx', '1', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('12', '*.xls', '2', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('13', '*.et', '2', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('14', '*.xlsx', '2', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('15', '*.ppt', '3', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('16', '*.pptx', '3', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('17', '*.txt', '4', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('18', '*.rtf', '5', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('19', '*.rar', '6', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('20', '*.zip', '6', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('21', '*.pdf', '7', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('22', '*.jpg', '8', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('23', '*.bmp', '8', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('24', '*.gif', '8', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('25', '*.PNG', '8', '1', null, null, null);
INSERT INTO `t_file_type` VALUES ('26', '*.PSD', '8', '1', null, null, null);

-- ----------------------------
-- Table structure for `t_granted`
-- ----------------------------
DROP TABLE IF EXISTS `t_granted`;
CREATE TABLE `t_granted` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一主键',
  `type` int(11) NOT NULL COMMENT '授权类型：1代表整个设备组权限，2代表整个设备权限，3代表账户权限',
  `assetgroup_id` int(11) DEFAULT NULL COMMENT '授权设备组id',
  `asset_id` int(11) DEFAULT NULL COMMENT '授权设备id',
  `account_id` int(11) DEFAULT NULL COMMENT '授权账户id',
  `status` int(11) DEFAULT NULL COMMENT '状态：0代表待审批，1代表已授权，-1代表审批拒绝',
  `create_by` varchar(36) DEFAULT NULL COMMENT '创建人guid',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `user_id` varchar(50) DEFAULT NULL COMMENT '用户guid',
  `usergroup_id` int(11) DEFAULT NULL COMMENT '用户组id',
  `approve_id` varchar(36) DEFAULT NULL COMMENT '审批id',
  `isdelete` int(2) DEFAULT '0' COMMENT '是否删除：0默认不删除；1 删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=828 DEFAULT CHARSET=utf8 COMMENT='设备授权表';

-- ----------------------------
-- Records of t_granted
-- ----------------------------
-- ----------------------------
-- Table structure for `t_log_client`
-- ----------------------------
DROP TABLE IF EXISTS `t_log_client`;
CREATE TABLE `t_log_client` (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `data_type` varchar(36) NOT NULL COMMENT '日志类型',
  `dev_unique` varchar(36) DEFAULT NULL COMMENT '设备唯一标识',
  `computer_name` varchar(100) DEFAULT NULL COMMENT '计算机名',
  `time` timestamp NULL DEFAULT NULL COMMENT '时间',
  `user_guid` varchar(36) DEFAULT NULL COMMENT '用户唯一标识',
  `local_ip` varchar(36) DEFAULT NULL COMMENT '客户端ip',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_log_client
-- ----------------------------


-- ----------------------------
-- Table structure for `t_log_client_file`
-- ----------------------------
DROP TABLE IF EXISTS `t_log_client_file`;
CREATE TABLE `t_log_client_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `log_id` varchar(36) NOT NULL COMMENT 't_log_client主键唯一标识',
  `file_name` varchar(100) DEFAULT NULL COMMENT '文件名称',
  `optype` int(11) DEFAULT NULL COMMENT '文件操作类型【 0 打开  1移动  2复制 3删除文件 4重命名 5创建目录 6新建文件 7修改文件 8删除目录  9恢复(从回收站)】',
  `src_path` varchar(600) DEFAULT NULL COMMENT '源路径',
  `dst_path` varchar(600) DEFAULT NULL COMMENT '目标路径',
  `proc` varchar(200) DEFAULT NULL COMMENT '进程名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_log_client_file
-- ----------------------------


-- ----------------------------
-- Table structure for `t_log_system`
-- ----------------------------
DROP TABLE IF EXISTS `t_log_system`;
CREATE TABLE `t_log_system` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '日志id',
  `time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间',
  `ip` varchar(15) DEFAULT NULL COMMENT '管理员IP',
  `user_id` varchar(36) DEFAULT NULL COMMENT '管理员guid',
  `user_username` varchar(50) DEFAULT NULL COMMENT '管理员username',
  `user_name` varchar(50) DEFAULT NULL COMMENT '管理员姓名',
  `log_type` int(11) NOT NULL COMMENT '日志类型',
  `asset_id` int(11) DEFAULT NULL COMMENT '设备id',
  `asset_name` varchar(50) DEFAULT NULL COMMENT '设备名称',
  `asset_ip` varchar(50) DEFAULT NULL COMMENT '设备ip',
  `log_page` varchar(255) DEFAULT '' COMMENT '对应页面',
  `log_operate_param` text COMMENT '请求参数',
  `log_desc` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2933 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_log_system
-- ----------------------------

-- ----------------------------
-- Table structure for `t_navigation`
-- ----------------------------
DROP TABLE IF EXISTS `t_navigation`;
CREATE TABLE `t_navigation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键（自增长）',
  `compositor` int(10) unsigned NOT NULL COMMENT '排序',
  `name` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '页签显示名称',
  `href` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '页签跳转链接',
  `visible` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可见',
  `grant_visible` tinyint(1) NOT NULL DEFAULT '1' COMMENT '角色授权时是否可见',
  `parent_id` int(10) DEFAULT NULL COMMENT '父级页签Id',
  `icon` varchar(45) COLLATE utf8_bin DEFAULT NULL COMMENT '页签图标',
  `level` int(10) unsigned NOT NULL COMMENT '页签级别',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='页签-导航信息表';

-- ----------------------------
-- Records of t_navigation
-- ----------------------------
INSERT INTO `t_navigation` VALUES ('1', '1', '首页', '/homepage/index', '1', '1', null, 'icon-home', '1');
INSERT INTO `t_navigation` VALUES ('2', '2', '用户', '', '1', '1', null, 'icon-user', '1');
INSERT INTO `t_navigation` VALUES ('3', '3', '设备', '', '1', '1', null, 'icon-other-equipment', '1');
INSERT INTO `t_navigation` VALUES ('4', '4', '监控', '', '1', '1', null, 'icon-monitor', '1');
INSERT INTO `t_navigation` VALUES ('5', '5', '审批', '/approval/approvalGrantedList', '1', '1', null, 'icon-approved', '1');
INSERT INTO `t_navigation` VALUES ('6', '6', '策略', '/strategy/strategyList', '1', '1', null, 'icon-strategy', '1');
INSERT INTO `t_navigation` VALUES ('7', '7', '任务', '/task/taskList', '1', '1', NULL, 'icon-lecture', '1');
INSERT INTO `t_navigation` VALUES ('8', '8', '报表', '', '1', '1', null, 'icon-report', '1');
INSERT INTO `t_navigation` VALUES ('9', '9', '系统', '', '1', '1', null, 'icon-system', '1');
INSERT INTO `t_navigation` VALUES ('10', '10', '单点登录', '/granted/grantedList', '1', '0', null, 'icon-monitor', '1');
INSERT INTO `t_navigation` VALUES ('11', '1', '用户管理', '/user/userList', '1', '1', '2', null, '2');
INSERT INTO `t_navigation` VALUES ('12', '2', '用户组', '/user/userGroupList', '1', '1', '2', null, '2');
INSERT INTO `t_navigation` VALUES ('13', '1', '设备管理', '/asset/assetList', '1', '1', '3', null, '2');
INSERT INTO `t_navigation` VALUES ('14', '2', '设备组', '/asset/assetGroupList', '1', '1', '3', null, '2');
INSERT INTO `t_navigation` VALUES ('15', '1', '实时', '/monitor/monitorRelTimeList', '1', '1', '4', null, '2');
INSERT INTO `t_navigation` VALUES ('16', '2', '历史', '/monitor/monitorHistoryList', '1', '1', '4', null, '2');
INSERT INTO `t_navigation` VALUES ('18', '3', '日志', '/system/logList', '1', '1', '9', '', '2');
INSERT INTO `t_navigation` VALUES ('19', '4', '关于', '/system/about', '1', '1', '9', null, '2');
INSERT INTO `t_navigation` VALUES ('20', '1', '授权设备', '/report/resource', '1', '1', '8', null, '2');
INSERT INTO `t_navigation` VALUES ('21', '2', '违规命令', '/report/command', '1', '1', '8', null, '2');
INSERT INTO `t_navigation` VALUES ('22', '1', '账号配置', '/system/accountConfig', '1', '1', '9', null, '2');
INSERT INTO `t_navigation` VALUES ('23', '2', '系统配置', '/system/systemConfig', '1', '1', '9', null, '2');
INSERT INTO `t_navigation` VALUES ('24', '1', '网络配置', '', '0', '1', '23', null, '3');
INSERT INTO `t_navigation` VALUES ('25', '2', '管控平台', '', '0', '1', '23', null, '3');
INSERT INTO `t_navigation` VALUES ('26', '3', '服务器', '', '0', '1', '23', null, '3');
INSERT INTO `t_navigation` VALUES ('27', '4', '级联管理', '', '0', '0', '23', null, '3');
INSERT INTO `t_navigation` VALUES ('28', '5', '第三方服务器', '', '0', '0', '23', null, '3');
INSERT INTO `t_navigation` VALUES ('29', '6', '数据库', '', '0', '1', '23', null, '3');
INSERT INTO `t_navigation` VALUES ('30', '3', '授权用户', '/report/user', '1', '1', '8', null, '2');
INSERT INTO `t_navigation` VALUES ('31', '7', '准入控制', '', '0', '1', '23', NULL, '3');
INSERT INTO `t_navigation` VALUES ('32', '8', 'USBKey配置', '', '0', '1', '23', NULL, '3');
INSERT INTO `t_navigation` VALUES ('33', '4', '行为审计', '/report/action', '1', '1', '8', null, '3');

-- ----------------------------
-- Table structure for `t_operation`
-- ----------------------------
DROP TABLE IF EXISTS `t_operation`;
CREATE TABLE `t_operation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键（自增长）',
  `compositor` int(11) NOT NULL COMMENT '排序',
  `code` varchar(50) DEFAULT NULL COMMENT '功能操作编码',
  `url_partten` varchar(200) NOT NULL COMMENT '功能操作url样式',
  `method` varchar(10) DEFAULT NULL COMMENT '请求方式：GET，POST，PUT，DELETE',
  `description` varchar(200) DEFAULT NULL COMMENT '功能描述',
  `visible` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可见',
  `grant_visible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '角色授权时是否可见',
  `parent_id` int(11) DEFAULT NULL COMMENT '父级功能Id',
  `navigation_id` int(11) DEFAULT NULL COMMENT '功能归属的页签id，没有特定归属可为空，全部页签通用为0。',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=933 DEFAULT CHARSET=utf8 COMMENT='功能操作表';

-- ----------------------------
-- Records of t_operation
-- ----------------------------
INSERT INTO `t_operation` VALUES ('1', '1', 'user::all', '/user/**', '', '用户管理', '0', '0', '0', '11');
INSERT INTO `t_operation` VALUES ('2', '2', 'usergroup::all', '/usergroup/**', null, '用户组管理', '0', '0', '0', '12');
INSERT INTO `t_operation` VALUES ('3', '3', 'granted::all', '/granted/**', null, '授权', '0', '0', '0', '11');
INSERT INTO `t_operation` VALUES ('4', '4', 'asset::all', '/asset/**', null, '设备管理', '0', '0', '0', '13');
INSERT INTO `t_operation` VALUES ('5', '5', 'assetgroup::all', '/assetgroup/**', null, '设备组管理', '0', '0', '0', '14');
INSERT INTO `t_operation` VALUES ('6', '6', 'session::all', '/session/**', null, '会话', '0', '0', '0', '15');
INSERT INTO `t_operation` VALUES ('7', '7', 'replay::all', '/replay/**', null, '回放', '0', '0', '0', '16');
INSERT INTO `t_operation` VALUES ('8', '8', 'approval::all', '/approval/**', null, '审批', '0', '0', '0', '5');
INSERT INTO `t_operation` VALUES ('9', '9', 'strategy::all', '/strategy/**', null, '策略', '0', '0', '0', '6');
INSERT INTO `t_operation` VALUES ('10', '10', 'report::all', '/report/**', null, '报表', '0', '0', '0', '8');
INSERT INTO `t_operation` VALUES ('11', '11', 'logSystem::all', '/logSystem/**', null, '系统日志（操作、授权、审批）', '0', '0', '0', '18');
INSERT INTO `t_operation` VALUES ('12', '12', 'ssoConfiguration::all', '/ssoConfiguration/**', null, '单点登录配置', '0', '0', '0', '20');
INSERT INTO `t_operation` VALUES ('21', '21', 'auditorOperator', '/auditorOperator/**', null, '审批员设置操作员权限', '0', '0', '0', null);
INSERT INTO `t_operation` VALUES ('22', '22', 'userAssetAssetgroup', '/userAssetAssetgroup/**', null, '操作员设置设备/设备组权限', '0', '0', '0', null);
INSERT INTO `t_operation` VALUES ('23', '23', 'backUp:all', '/backUp/**', null, '数据库备份', '0', '0', '0', '29');
INSERT INTO `t_operation` VALUES ('51', '51', 'permission::all', '/permission/**', null, '用户权限管理', '0', '0', '0', null);
INSERT INTO `t_operation` VALUES ('52', '52', 'department::all', '/department/**', null, '部门管理功能', '0', '0', '0', null);
INSERT INTO `t_operation` VALUES ('53', '53', 'navigation::all', '/navigation/**', null, '导航菜单功能', '0', '0', '0', null);
INSERT INTO `t_operation` VALUES ('54', '54', 'loginsuccess', '/loginsuccess', null, '登录成功', '0', '0', '0', null);
INSERT INTO `t_operation` VALUES ('55', '55', 'getCurrentUser', '/user/getCurrentUser', null, '获取当前登录账号', '0', '0', '0', null);
INSERT INTO `t_operation` VALUES ('56', '56', 'updateUserPwd', '/user/updateUserPwd', NULL, '更新密码', '0', '0', '0', NULL);
INSERT INTO `t_operation` VALUES ('57', '57', 'security', '/system/systemSet/platform/security', NULL, '获取密码限制', '0', '0', '0', NULL);
INSERT INTO `t_operation` VALUES ('58', '58', 'onlineSession', '/onlineSession/**', 'POST', '更新session的关联设备标识', '0', '0', '0', NULL);
INSERT INTO `t_operation` VALUES ('99', '99', 'role::all', '/role/**', null, '角色管理', '0', '0', '0', null);
INSERT INTO `t_operation` VALUES ('101', '1', 'user::select', '', null, '查询', '0', '0', '1', '11');
INSERT INTO `t_operation` VALUES ('102', '2', 'user::add', '', null, '新建', '0', '1', '1', '11');
INSERT INTO `t_operation` VALUES ('103', '3', 'user::import', '', null, '导入', '0', '1', '1', '11');
INSERT INTO `t_operation` VALUES ('104', '4', 'user::export', '', null, '导出', '0', '1', '1', '11');
INSERT INTO `t_operation` VALUES ('105', '5', 'user::update', '', null, '编辑', '0', '1', '1', '11');
INSERT INTO `t_operation` VALUES ('106', '6', 'user::delete', '', null, '删除', '0', '1', '1', '11');
INSERT INTO `t_operation` VALUES ('107', '7', 'user::granted', '', null, '设备授权', '0', '1', '1', '11');
INSERT INTO `t_operation` VALUES ('111', '1', 'user::select::page', '/user/getUsersInPage', 'GET', '查看用户列表', '0', '0', '101', '11');
INSERT INTO `t_operation` VALUES ('112', '2', 'user::select::one', '/user/user/*', 'GET', '获取指定用户信息', '0', '0', '101', '11');
INSERT INTO `t_operation` VALUES ('113', '3', 'user::select::self', '/user/getCurrentUser', 'GET', '获取当前登录用户信息', '0', '0', '101', '11');
INSERT INTO `t_operation` VALUES ('114', '4', 'user::add::user', '/user/user', 'POST', '新增用户', '0', '0', '102', '11');
INSERT INTO `t_operation` VALUES ('115', '5', 'user::import::user', '/asset/import/user', 'POST', '导入用户', '0', '0', '103', '11');
INSERT INTO `t_operation` VALUES ('116', '6', 'user::export::user', '/asset/export/user', 'GET', '导出用户', '0', '0', '104', '11');
INSERT INTO `t_operation` VALUES ('117', '7', 'user::update::update', '/user/user', 'PUT', '编辑用户', '0', '0', '105', '11');
INSERT INTO `t_operation` VALUES ('118', '8', 'user::delete::one', '/user/user/*', 'DELETE', '删除用户', '0', '0', '106', '11');
INSERT INTO `t_operation` VALUES ('119', '9', 'user::delete::batch', '/user/deleteUsersByIds', 'DELETE', '批量删除用户', '0', '0', '106', '11');
INSERT INTO `t_operation` VALUES ('120', '10', 'user::granted::asset', '/granted/**', null, '授权设备', '0', '0', '107', '11');
INSERT INTO `t_operation` VALUES ('121', '11', 'user::select::usergroup::list', '/usergroup/list', 'GET', '获取用户组列表', '0', '0', '101', '11');
INSERT INTO `t_operation` VALUES ('122', '12', 'user::select::allstrategy', '/strategy/getStrategyAll', 'GET', '获取所有策略信息列表', '0', '0', '101', '11');
INSERT INTO `t_operation` VALUES ('123', '13', 'user::add::security', '/system/systemSet/platform/security', 'GET', '新建用户-密码限制', '0', '0', '102', '11');
INSERT INTO `t_operation` VALUES ('124', '13', 'user::import::template', '/asset/import/user/template', 'GET', '下载用户导入模板', '0', '0', '103', '11');
INSERT INTO `t_operation` VALUES ('125', '14', 'user::select::granted', '/granted/detailInPage', 'GET', '获取用户授权详情', '0', '0', '101', '11');
INSERT INTO `t_operation` VALUES ('151', '1', 'usergroup::select', '', null, '查询', '0', '0', '2', '12');
INSERT INTO `t_operation` VALUES ('152', '1', 'usergroup::add', '', null, '新建', '0', '1', '2', '12');
INSERT INTO `t_operation` VALUES ('153', '2', 'usergroup::update', '', null, '编辑', '0', '1', '2', '12');
INSERT INTO `t_operation` VALUES ('154', '3', 'usergroup::delete', '', null, '删除', '0', '1', '2', '12');
INSERT INTO `t_operation` VALUES ('155', '4', 'usergroup::granted', '', null, '设备授权', '0', '1', '2', '12');
INSERT INTO `t_operation` VALUES ('161', '1', 'usergroup::select::page', '/usergroup/getUsergroupInPage', 'GET', '获取用户组分页列表', '0', '0', '151', '12');
INSERT INTO `t_operation` VALUES ('162', '2', 'usergroup::select::list', '/usergroup/list', 'GET', '获取用户组列表', '0', '0', '151', '12');
INSERT INTO `t_operation` VALUES ('163', '3', 'usergroup::add::add', '/usergroup/usergroup', 'POST', '保存用户组', '0', '0', '152', '12');
INSERT INTO `t_operation` VALUES ('164', '4', 'usergroup::update::update', '/usergroup/usergroup/*', 'PUT', '更新用户组', '0', '0', '153', '12');
INSERT INTO `t_operation` VALUES ('165', '5', 'usergroup::delete::usergroup', '/usergroup/usergroup/*', 'DELETE', '删除用户组', '0', '0', '154', '12');
INSERT INTO `t_operation` VALUES ('166', '6', 'usergroup::select::listbyguid', '/usergroup/getUsergroupListByUserGroupGuid', 'GET', '获取用户组列表（用户组）', '0', '0', '151', '12');
INSERT INTO `t_operation` VALUES ('167', '7', 'usergroup::granted::asset', '/granted/**', null, '授权设备', '0', '0', '155', '12');
INSERT INTO `t_operation` VALUES ('168', '8', 'usergroup::delete::batch', '/usergroup/usergroup/delete', 'DELETE', '批量删除用户组', '0', '0', '154', '12');
INSERT INTO `t_operation` VALUES ('169', '9', 'usergroup::select::allstrategy', '/strategy/getStrategyAll', 'GET', '获取所有策略信息列表', '0', '0', '151', '12');
INSERT INTO `t_operation` VALUES ('201', '1', 'asset::select', '', null, '查询', '0', '0', null, '13');
INSERT INTO `t_operation` VALUES ('202', '2', 'asset::add', '', null, '新增', '0', '1', null, '13');
INSERT INTO `t_operation` VALUES ('203', '3', 'asset::import', '', null, '导入', '0', '1', null, '13');
INSERT INTO `t_operation` VALUES ('204', '4', 'asset::export', '', null, '导出', '0', '1', null, '13');
INSERT INTO `t_operation` VALUES ('205', '5', 'asset::update', '', null, '编辑', '0', '1', null, '13');
INSERT INTO `t_operation` VALUES ('206', '6', 'asset::delete', '', null, '删除', '0', '1', null, '13');
INSERT INTO `t_operation` VALUES ('211', '1', 'asset::select::page', '/asset/getAssetsInPage', 'GET', '获取设备分页列表', '0', '0', '201', '13');
INSERT INTO `t_operation` VALUES ('212', '2', 'asset::select::list', '/asset/asset/*', 'GET', '获取设备详情', '0', '0', '201', '13');
INSERT INTO `t_operation` VALUES ('213', '3', 'asset::select::type', '/asset/getAssetType', 'GET', '获取设备类型', '0', '0', '201', '13');
INSERT INTO `t_operation` VALUES ('214', '4', 'asset::select::typetree', '/asset/getAssetTypeTree', 'GET', '获取设备类型树', '0', '0', '201', '13');
INSERT INTO `t_operation` VALUES ('215', '5', 'asset::select::publishList', '/asset/publishList', 'GET', '获取应用程序发布器列表', '0', '0', '201', '13');
INSERT INTO `t_operation` VALUES ('216', '6', 'asset::select::ruletype', '/asset/sso/ruleType', 'GET', '获取SSO规则类型', '0', '0', '201', '13');
INSERT INTO `t_operation` VALUES ('217', '7', 'asset::select::ruleattr', '/asset/sso/ruleAttr', 'GET', '获取SSO规则属性', '0', '0', '201', '13');
INSERT INTO `t_operation` VALUES ('218', '8', 'asset::select::toollist', '/asset/getOperationToolList/*', 'GET', '获取运维工具列表', '0', '0', '201', '13');
INSERT INTO `t_operation` VALUES ('219', '9', 'asset::select::accountpage', '/asset/getAssetAccountsInPage', 'GET', '获取账号列表信息', '0', '0', '201', '13');
INSERT INTO `t_operation` VALUES ('220', '10', 'asset::select::accountlist', '/asset/assetAccount/*', 'GET', '获取设备账号信息', '0', '0', '201', '13');
INSERT INTO `t_operation` VALUES ('221', '11', 'asset::add::asset', '/asset/asset', 'POST', '新增设备', '0', '0', '202', '13');
INSERT INTO `t_operation` VALUES ('222', '12', 'asset::add::account', '/asset/assetAccount', 'POST', '新增账号信息', '0', '0', '202', '13');
INSERT INTO `t_operation` VALUES ('223', '13', 'asset::import::rule', '/asset/import/rule', 'POST', '导入应用程序发布规则', '0', '0', '203', '13');
INSERT INTO `t_operation` VALUES ('224', '21', 'asset::import::template', '/asset/import/template', 'GET', '下载设备导入模板', '0', '0', '203', '13');
INSERT INTO `t_operation` VALUES ('225', '14', 'asset::export::asset', '/asset/export', 'GET', '导出设备列表', '0', '0', '204', '13');
INSERT INTO `t_operation` VALUES ('226', '15', 'asset::update::asset', '/asset/asset', 'PUT', '更新设备', '0', '0', '205', '13');
INSERT INTO `t_operation` VALUES ('227', '16', 'asset::update::account', '/asset/assetAccount', 'PUT', '更新账号信息', '0', '0', '205', '13');
INSERT INTO `t_operation` VALUES ('228', '17', 'asset::delete::asset', '/asset/asset/*', 'DELETE', '删除设备', '0', '0', '206', '13');
INSERT INTO `t_operation` VALUES ('229', '18', 'asset::delete::batchasset', '/asset/delete', 'DELETE', '批量删除设备', '0', '0', '206', '13');
INSERT INTO `t_operation` VALUES ('230', '19', 'asset::delete::account', '/asset/assetAccount/*', 'DELETE', '删除设备账号信息', '0', '0', '206', '13');
INSERT INTO `t_operation` VALUES ('231', '20', 'asset::delete::batchaccount', '/asset/assetAccount/delete', 'DELETE', '批量删除设备账号信息', '0', '0', '206', '13');
INSERT INTO `t_operation` VALUES ('232', '1', 'asset::select::assetgroup', '/assetgroup/getAssetgroupListByPid', 'GET', '查找对应设备组以及子类设备组', '0', '0', '201', '13');
INSERT INTO `t_operation` VALUES ('233', '24', 'asset::import::asset', '/asset/import', 'POST', '导入设备', '0', '0', '203', '13');
INSERT INTO `t_operation` VALUES ('234', '25', 'asset::import::rule::template', '/asset/import/rule/template', 'GET', '下载发布规则模板', '0', '0', '203', '13');
INSERT INTO `t_operation` VALUES ('251', '1', 'assetgroup::select', '', null, '查询', '0', '0', '4', '14');
INSERT INTO `t_operation` VALUES ('252', '2', 'assetgroup::add', '', null, '新增', '0', '1', '4', '14');
INSERT INTO `t_operation` VALUES ('253', '3', 'assetgroup::update', '', null, '编辑', '0', '1', '4', '14');
INSERT INTO `t_operation` VALUES ('254', '4', 'assetgroup::delete', '', null, '删除', '0', '1', '4', '14');
INSERT INTO `t_operation` VALUES ('261', '1', 'assetgroup::select::listbypid', '/assetgroup/getAssetgroupListByPid', 'GET', '查找对应设备组以及子类设备组', '0', '0', '251', '14');
INSERT INTO `t_operation` VALUES ('262', '2', 'assetgroup::select::listbyid', '/assetgroup/getAssetgroupListByAssetId', 'GET', '查找对应设备组列表', '0', '0', '251', '14');
INSERT INTO `t_operation` VALUES ('263', '3', 'assetgroup::select::page', '/assetgroup/getAssetgroupInPage', 'GET', '获取设备组分页列表', '0', '0', '251', '14');
INSERT INTO `t_operation` VALUES ('264', '4', 'assetgroup::select::account', '/assetgroup/*/account', 'GET', '获取组内所有设备的账号信息', '0', '0', '251', '14');
INSERT INTO `t_operation` VALUES ('265', '5', 'assetgroup::add::assetgroup', '/assetgroup/assetgroup', 'POST', '新增设备组', '0', '0', '252', '14');
INSERT INTO `t_operation` VALUES ('266', '6', 'assetgroup::update::assetgroup', '/assetgroup/assetgroup', 'PUT', '编辑设备组', '0', '0', '253', '14');
INSERT INTO `t_operation` VALUES ('267', '7', 'assetgroup::delete::assetgroup', '/assetgroup/assetgroup/*', 'DELETE', '删除设备组', '0', '0', '254', '14');
INSERT INTO `t_operation` VALUES ('301', '1', 'realsession::select', '', null, '查询', '0', '0', '5', '15');
INSERT INTO `t_operation` VALUES ('302', '2', 'realsession::monitor', '', null, '播放', '0', '1', '5', '15');
INSERT INTO `t_operation` VALUES ('303', '3', 'realsession::block', '', null, '阻断', '0', '1', '5', '15');
INSERT INTO `t_operation` VALUES ('311', '1', 'realsession::select::page', '/session/getSessionsInPage', 'GET', '获取实时监控的分页列表', '0', '0', '301', '15');
INSERT INTO `t_operation` VALUES ('312', '2', 'realsession::monitor::monitor', '/granted/remote', 'GET', '远程桌面', '0', '0', '302', '15');
INSERT INTO `t_operation` VALUES ('313', '3', 'realsession::block::block', '/session/blockSessionBySessionId', 'POST', '实时监控中的阻断接口', '0', '0', '303', '15');
INSERT INTO `t_operation` VALUES ('351', '1', 'finishsession::select', '', null, '查询', '0', '0', '6', '16');
INSERT INTO `t_operation` VALUES ('352', '4', 'finishsession::replay', '', null, '播放', '0', '1', '6', '16');
INSERT INTO `t_operation` VALUES ('353', '5', 'finishsession::command', '', null, '命令审计', '0', '1', '6', '16');
INSERT INTO `t_operation` VALUES ('361', '1', 'finishsession::select::page', '/session/getSessionsInPage', 'GET', '获取历史监控的分页列表', '0', '0', '351', '16');
INSERT INTO `t_operation` VALUES ('362', '2', 'finishsession::replay::replay', '/replay/getReplayListBySessionId', 'GET', '获取回放录像列表的接口', '0', '0', '352', '16');
INSERT INTO `t_operation` VALUES ('363', '3', 'finishsession::replay::command', '/replay/getCommandReplayBySessionId', 'GET', '获取命令的回放录信息', '0', '0', '352', '16');
INSERT INTO `t_operation` VALUES ('364', '4', 'finishsession::command::play', '/replay/getReplayListBySessionId', 'GET', '获取命令列表的分页', '0', '0', '353', '16');
INSERT INTO `t_operation` VALUES ('365', '5', 'finishsession::replay::player', '/replay/player', 'GET', '视频回放页面', '0', '0', '352', '16');
INSERT INTO `t_operation` VALUES ('366', '6', 'finishsession::command::page', '/command/getCommandListInPage', 'GET', '获取回放录像列表的接口', '0', '0', '352', '16');
INSERT INTO `t_operation` VALUES ('367', '7', 'finishsession::command::player', '/monitor/commandPlay', '', '命令回放页面', '0', '0', '353', '16');
INSERT INTO `t_operation` VALUES ('401', '1', 'approval::select', '', null, '查询', '0', '0', '8', '5');
INSERT INTO `t_operation` VALUES ('402', '1', 'approval::operate', '', null, '操作', '0', '1', '8', '5');
INSERT INTO `t_operation` VALUES ('411', '1', 'approval::select::page', '/approval/getApprovalFlowGrantedsInPage', 'GET', '分页获取授权审批列表', '0', '0', '401', '5');
INSERT INTO `t_operation` VALUES ('412', '2', 'approval::select::detail', '/approval/getApprovalFlowGrantedsDetailInPage', 'GET', '分页获取授权审批列表详情', '0', '0', '401', '5');
INSERT INTO `t_operation` VALUES ('413', '3', 'approval::operate::approval', '/approval/approvalResut', 'GET', '通过或拒绝审批流程', '0', '0', '402', '5');
INSERT INTO `t_operation` VALUES ('451', '1', 'strategy::select', '', null, '查询', '0', '0', '8', '6');
INSERT INTO `t_operation` VALUES ('452', '2', 'strategy::add', '', null, '新建', '0', '1', '8', '6');
INSERT INTO `t_operation` VALUES ('453', '3', 'strategy::update', '', null, '编辑', '0', '1', '8', '6');
INSERT INTO `t_operation` VALUES ('454', '4', 'strategy::delete', '', null, '删除', '0', '1', '8', '6');
INSERT INTO `t_operation` VALUES ('461', '1', 'strategy::select::page', '/strategy/getStrategyInPage', 'GET', '分页获取策略列表', '0', '0', '451', '6');
INSERT INTO `t_operation` VALUES ('462', '2', 'strategy::select::one', '/strategy/strategy/*', 'GET', '获取策略信息', '0', '0', '451', '6');
INSERT INTO `t_operation` VALUES ('463', '3', 'strategy::select::all', '/strategy/getStrategyAll', 'GET', '获取所有策略信息列表', '0', '0', '451', '6');
INSERT INTO `t_operation` VALUES ('464', '4', 'strategy::select::sessiontype', '/strategy/getStrategySessionType', 'GET', '获取协议控制--会话选项', '0', '0', '451', '6');
INSERT INTO `t_operation` VALUES ('465', '5', 'strategy::select::rdp', '/strategy/getStrategyRDP', 'GET', '获取协议控制--RDP选项', '0', '0', '451', '6');
INSERT INTO `t_operation` VALUES ('466', '6', 'strategy::select::ssh', '/strategy/getStrategySSH', 'GET', '获取协议控制--SSH选项', '0', '0', '451', '6');
INSERT INTO `t_operation` VALUES ('467', '7', 'strategy::add::strategy', '/strategy/strategy', 'POST', '新增策略', '0', '0', '452', '6');
INSERT INTO `t_operation` VALUES ('468', '8', 'strategy::update::strategy', '/strategy/strategy', 'PUT', '编辑策略', '0', '0', '453', '6');
INSERT INTO `t_operation` VALUES ('469', '9', 'strategy::delete::strategy', '/strategy/strategy/*', 'DELETE', '删除策略信息', '0', '0', '454', '6');
INSERT INTO `t_operation` VALUES ('470', '10', 'strategy::delete::batchstrategy', '/strategy/deleteStrategiesByGuids', 'DELETE', '批量删除策略信息', '0', '0', '454', '6');
INSERT INTO `t_operation` VALUES ('471', '11', 'strategy::select::fileType', '/strategy/getFileTypeTree', 'GET', '获取文件类型树', '1', '0', NULL, NULL);
INSERT INTO `t_operation` VALUES ('501', '1', 'report::select', '', null, '查询', '0', '0', '9', '8');
INSERT INTO `t_operation` VALUES ('502', '2', 'report::export', '', null, '导出', '0', '1', '9', '8');
INSERT INTO `t_operation` VALUES ('511', '1', 'report::select::report', '/report/**', null, '报表', '0', '0', '501', '8');
INSERT INTO `t_operation` VALUES ('512', '2', 'report::select::assetType', '/asset/getAssetTypeTree', 'GET', '获取设备类型列表', '0', '0', '501', '8');
INSERT INTO `t_operation` VALUES ('551', '1', 'sso::connect', '', null, '单点登录', '0', '0', '6', '10');
INSERT INTO `t_operation` VALUES ('552', '2', 'ssoconfiguration::select', '', null, '查询配置', '0', '0', '6', '10');
INSERT INTO `t_operation` VALUES ('553', '3', 'ssoconfiguration::add', '', null, '保存配置', '0', '0', '6', '10');
INSERT INTO `t_operation` VALUES ('561', '1', 'sso::connect::session', '/session/connectWithSsoConfiguration', 'POST', '生成登录信息，发起连接', '0', '0', '551', '10');
INSERT INTO `t_operation` VALUES ('562', '2', 'sso::connect::openremote', '/session/remoteapp/open', 'POST', '开启一个remoteApp的会话', '0', '0', '551', '10');
INSERT INTO `t_operation` VALUES ('563', '3', 'sso::connect::closeremote', '/session/remoteapp/close', 'POST', '关闭一个remoteApp的会话', '0', '0', '551', '10');
INSERT INTO `t_operation` VALUES ('564', '4', 'ssoconfiguration::select::session', '/ssoConfiguration/findSSOConfigurationByAssetIdAndAccountIdWithCurrentUser', 'GET', '获取当前用户的单点登录配置信息', '0', '0', '552', '10');
INSERT INTO `t_operation` VALUES ('565', '5', 'ssoconfiguration::add::one', '/ssoConfiguration/saveConfigureSSO4CurrentUser', 'POST', '保存配置', '0', '0', '553', '10');
INSERT INTO `t_operation` VALUES ('566', '6', 'ssoconfiguration::add::batch', '/ssoConfiguration/batchSaveConfigureSSO4CurrentUser', 'POST', '批量保存配置', '0', '0', '553', '10');
INSERT INTO `t_operation` VALUES ('567', '7', 'sso::connect::remote', '/sso/remote', 'GET', 'Web端单点登录', '0', '0', '551', '10');
INSERT INTO `t_operation` VALUES ('601', '1', 'granted::select', '/granted/', null, '查询', '0', '0', '3', null);
INSERT INTO `t_operation` VALUES ('611', '1', 'granted::select::ssopage', '/granted/getGrantedsByCurrentUser4SSOInPage', 'GET', '单点登录页查询授权设备列表', '0', '0', '601', '10');
INSERT INTO `t_operation` VALUES ('701', '1', 'accountConfig::all', '/system/accountConfig/**', null, '系统账号配置', '0', '0', '0', '22');
INSERT INTO `t_operation` VALUES ('711', '1', 'accountConfig::save', '/system/accountConfig/saveSystemUser', 'POST', '新增', '0', '1', '701', '22');
INSERT INTO `t_operation` VALUES ('712', '2', 'accountConfig::update', '/system/accountConfig/editSystemUser', 'POST', '修改', '0', '1', '701', '22');
INSERT INTO `t_operation` VALUES ('713', '3', 'accountConfig::delete', '/system/accountConfig/**', 'DELETE', '删除', '0', '1', '701', '22');
INSERT INTO `t_operation` VALUES ('714', '4', 'accountConfig::select::role', '/role/getRoleList', 'GET', '获取角色列表', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('715', '5', 'accountConfig::select::user', '/userAssetAssetgroup/getSystemUsers', 'GET', '获取系统账号列表', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('716', '6', 'accountConfig::select::user::detail', '/userAssetAssetgroup/detail/**', 'GET', '获取账号详情', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('717', '7', 'accountConfig::select::systemRoleList', '/userAssetAssetgroup/getSystemUserRoleTypes', 'GET', '创建系统账号时,获取可选角色列表', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('718', '8', 'accountConfig::save::security', '/system/systemSet/platform/security', 'GET', '新建系统账户-密码限制', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('719', '9', 'accountConfig::select::auditList', '/userAssetAssetgroup/auditList/**', 'GET', '获取审计权限列表', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('720', '10', 'accountConfig::select::assetGroupPermission', '/userAssetAssetgroup/getAssetgroupListTreeByUserGuid', 'GET', '获取设备组权限', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('721', '11', 'accountConfig::select::assetPermission', '/userAssetAssetgroup/getAssetListTreeByUserGuid', 'GET', '获取设备权限', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('722', '12', 'accountConfig::select::addAsset', '/userAssetAssetgroup/save/asset', 'POST', '保存设备权限', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('723', '13', 'accountConfig::select::deleteAsset', '/userAssetAssetgroup/delete/asset', 'DELETE', '删除设备授权', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('724', '14', 'accountConfig::select::addAssetGrouip', '/userAssetAssetgroup/save/assetGroup', 'POST', '添加设备组授权', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('725', '15', 'accountConfig::select::deleteAssetGroup', '/userAssetAssetgroup/delete/assetGroup', 'DELETE', '删除设备组授权', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('726', '16', 'accountConfig::select::deleteAudit', '/userAssetAssetgroup/delete/audit', 'DELETE', '删除审计权限', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('727', '17', 'accountConfig::select::addAudit', '/userAssetAssetgroup/save/audit', 'POST', '添加审计权限', '0', '0', '701', '22');
INSERT INTO `t_operation` VALUES ('751', '1', 'logSystem::select', '', null, '查询', '0', '0', '11', '18');
INSERT INTO `t_operation` VALUES ('761', '1', 'logSystem::select::page', '/logSystem/getLogSystemsInPage', 'GET', '分页查询', '0', '0', '751', '18');
INSERT INTO `t_operation` VALUES ('762', '2', 'logSystem::select::samlltype', '/logSystem/getLogSmallType', 'GET', '获取日志子类型', '0', '0', '751', '18');
INSERT INTO `t_operation` VALUES ('801', '1', 'homepage::select', '', null, '查询', '0', '0', '0', '1');
INSERT INTO `t_operation` VALUES ('811', '1', 'homepage::select::logSystemHome', '/logSystem/getLogSystemHome', 'GET', '首页-活跃账号Top5、资源运维Top5、运维日志Top20', '0', '0', '801', '1');
INSERT INTO `t_operation` VALUES ('812', '2', 'homepage::select::realtimeSession', '/session/getRealtimeSession', 'GET', '首页--实时会话', '0', '0', '801', '1');
INSERT INTO `t_operation` VALUES ('813', '3', 'homepage::select::assetNum', '/asset/infoForHomePage', 'GET', '首页--资源数量', '0', '0', '801', '1');
INSERT INTO `t_operation` VALUES ('814', '4', 'homepage::select::homeCounts', '/logSystem/getHomeCounts', 'GET', '首页--数量统计', '0', '0', '801', '1');
INSERT INTO `t_operation` VALUES ('815', '5', 'homepage::select::homeAssetsWeek', '/asset/getHomeAssetsWeek', 'GET', '首页-本周运维次数', '0', '0', '801', '1');
INSERT INTO `t_operation` VALUES ('901', '1', 'platform::select', '', null, '管控平台', '0', '0', '0', '25');
INSERT INTO `t_operation` VALUES ('902', '2', 'netconfig::select', '/systemSetting/netconfig', null, '网络配置', '0', '0', '0', '24');
INSERT INTO `t_operation` VALUES ('903', '3', 'server::select', '', null, '服务器', '0', '0', '0', '26');
INSERT INTO `t_operation` VALUES ('904', '4', 'backup::select', '', null, '数据库', '0', '0', '0', '29');
INSERT INTO `t_operation` VALUES ('905', '5', 'accessControl::select', '', NULL, '准入控制', '0', '0', NULL, '31');
INSERT INTO `t_operation` VALUES ('906', '6', 'USBKey::select', '', NULL, 'USBKey', '0', '0', '0', '32');
INSERT INTO `t_operation` VALUES ('911', '1', 'platform::select::client', '/system/systemSet/uploadClientPackage', 'POST', '客户端上传', '0', '0', '901', '25');
INSERT INTO `t_operation` VALUES ('912', '2', 'server::select::load', '/system/systemSet/computerInfo', 'GET', '设备负载', '0', '0', '903', '26');
INSERT INTO `t_operation` VALUES ('913', '3', 'server::select::netstat', '/system/systemSet/netStatInfo', 'GET', '网口状态', '0', '0', '903', '26');
INSERT INTO `t_operation` VALUES ('914', '4', 'server::select::logserver', '/system/systemSet/serverInfo', null, '第三方日志服务器', '0', '0', '903', '26');
INSERT INTO `t_operation` VALUES ('915', '5', 'server::select::mail::test', '/system/systemSet/mail/test', 'POST', '邮件服务器测试接口', '0', '0', '903', '26');
INSERT INTO `t_operation` VALUES ('916', '1', 'platform::select::save', '/system/systemSet/platform/save', 'POST', '更新管控平台信息', '0', '0', '901', null);
INSERT INTO `t_operation` VALUES ('917', '2', 'platform::select::get', '/system/systemSet/platform', 'GET', '获取管控平台信息', '0', '0', '901', null);
INSERT INTO `t_operation` VALUES ('918', '3', 'platform::select::background::upload', '/system/systemSet/background/upload', 'POST', '上传登录背景', '0', '0', '901', null);
INSERT INTO `t_operation` VALUES ('919', '4', 'platform::select::background::restore', '/system/systemSet/background/restore', 'GET', '还原登录背景', '0', '0', '901', null);
INSERT INTO `t_operation` VALUES ('922', '3', 'platform::select::getApprovalExpire', '/system/systemSet/platform/getApprovalExpire', 'GET', '获取管控平台--审批配置', '0', '0', '901', null);
INSERT INTO `t_operation` VALUES ('923', '6', 'about::select::fileupload', '/system/about/fileupload', 'POST', '上传授权文件', '0', '0', null, '19');
INSERT INTO `t_operation` VALUES ('924', '7', 'about::select::fileload', '/system/about/fileload', 'GET', '导出授权文件', '0', '0', null, '19');
INSERT INTO `t_operation` VALUES ('925', '8', 'about::select::authInfo', '/system/about/authInfo', 'GET', '获取授权信息', '0', '0', null, '19');
INSERT INTO `t_operation` VALUES ('926', '9', 'about::select::isFileExist', '/system/about/isFileExist', 'GET', '判断是否存在授权文件', '0', '0', null, '19');
INSERT INTO `t_operation` VALUES ('927', '10', 'backup::select::dbClean', '/backUp/dbClean', 'POST', '设置数据库维护', '0', '0', '904', '29');
INSERT INTO `t_operation` VALUES ('928', '11', 'backup::select::exportBackup', '/backUp/exportBackup', 'GET', '导出手动数据库备份', '0', '0', '904', '29');
INSERT INTO `t_operation` VALUES ('929', '12', 'backup::select::deleteBackUp', '/backUp/deleteBackUp', 'POST', '删除手动数据库备份', '0', '0', '904', '29');
INSERT INTO `t_operation` VALUES ('930', '13', 'backup::select::addAutoBackUp', '/backUp/addAutoBackUp', 'POST', '添加自动数据库备份', '0', '0', '904', '29');
INSERT INTO `t_operation` VALUES ('931', '14', 'backup::select::addActualBackUp', '/backUp/addActualBackUp', 'POST', '添加手动数据库备份', '0', '0', '904', '29');
INSERT INTO `t_operation` VALUES ('932', '15', 'backup::select::list', '/backUp/getBackUpList', 'GET', '获取备份列表', '0', '0', '904', '29');
INSERT INTO `t_operation` VALUES ('933', '16', 'accessControl::select::all', '/system/systemSet/accessControl', NULL, '准入控制', '0', '0', '905', '31');
INSERT INTO `t_operation` VALUES ('934', '17', 'USBKey::select::all', '/system/systemSet/ukey/**', NULL, 'USBKey接口', '0', '0', '906', '7');
INSERT INTO `t_operation` VALUES ('1001', '1', 'task::select', '', NULL, '任务模块所有接口', '0', '0', NULL, '7');
INSERT INTO `t_operation` VALUES ('1011', '1', 'task::select::asset', '/task/asset/detail', 'GET', '获取自动改密计划详情', '0', '0', '1001', '7');
INSERT INTO `t_operation` VALUES ('1012', '2', 'task::select::saveAsset', '/task/asset/save', 'POST', '保存自动改密计划', '0', '0', '1001', '7');
INSERT INTO `t_operation` VALUES ('1013', '3', 'task::select::asset::tree', '/task/asset/tree', 'GET', '获取设备树', '0', '0', '1001', '7');

-- ----------------------------
-- Table structure for `t_permission`
-- ----------------------------
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键（自增长）',
  `resource_type` int(11) NOT NULL COMMENT '访问资源类型',
  `resource_id` int(11) NOT NULL COMMENT '访问资源id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=289 DEFAULT CHARSET=utf8 COMMENT='访问资源权限表';

-- ----------------------------
-- Records of t_permission
-- ----------------------------
INSERT INTO `t_permission` VALUES ('1', '1', '1');
INSERT INTO `t_permission` VALUES ('2', '1', '2');
INSERT INTO `t_permission` VALUES ('3', '1', '3');
INSERT INTO `t_permission` VALUES ('4', '1', '4');
INSERT INTO `t_permission` VALUES ('5', '1', '5');
INSERT INTO `t_permission` VALUES ('6', '1', '6');
INSERT INTO `t_permission` VALUES ('7', '1', '7');
INSERT INTO `t_permission` VALUES ('8', '1', '8');
INSERT INTO `t_permission` VALUES ('9', '1', '9');
INSERT INTO `t_permission` VALUES ('10', '1', '10');
INSERT INTO `t_permission` VALUES ('11', '1', '11');
INSERT INTO `t_permission` VALUES ('12', '1', '12');
INSERT INTO `t_permission` VALUES ('13', '1', '13');
INSERT INTO `t_permission` VALUES ('14', '1', '14');
INSERT INTO `t_permission` VALUES ('15', '1', '15');
INSERT INTO `t_permission` VALUES ('16', '1', '16');
INSERT INTO `t_permission` VALUES ('18', '1', '18');
INSERT INTO `t_permission` VALUES ('19', '1', '19');
INSERT INTO `t_permission` VALUES ('20', '1', '20');
INSERT INTO `t_permission` VALUES ('21', '1', '21');
INSERT INTO `t_permission` VALUES ('22', '1', '22');
INSERT INTO `t_permission` VALUES ('23', '1', '23');
INSERT INTO `t_permission` VALUES ('24', '1', '24');
INSERT INTO `t_permission` VALUES ('25', '1', '25');
INSERT INTO `t_permission` VALUES ('26', '1', '26');
INSERT INTO `t_permission` VALUES ('27', '1', '27');
INSERT INTO `t_permission` VALUES ('28', '1', '28');
INSERT INTO `t_permission` VALUES ('29', '1', '29');
INSERT INTO `t_permission` VALUES ('30', '2', '1');
INSERT INTO `t_permission` VALUES ('31', '2', '2');
INSERT INTO `t_permission` VALUES ('32', '2', '3');
INSERT INTO `t_permission` VALUES ('33', '2', '4');
INSERT INTO `t_permission` VALUES ('34', '2', '5');
INSERT INTO `t_permission` VALUES ('35', '2', '6');
INSERT INTO `t_permission` VALUES ('36', '2', '7');
INSERT INTO `t_permission` VALUES ('37', '2', '8');
INSERT INTO `t_permission` VALUES ('38', '2', '9');
INSERT INTO `t_permission` VALUES ('39', '2', '10');
INSERT INTO `t_permission` VALUES ('40', '2', '11');
INSERT INTO `t_permission` VALUES ('41', '2', '12');
INSERT INTO `t_permission` VALUES ('42', '2', '51');
INSERT INTO `t_permission` VALUES ('43', '2', '52');
INSERT INTO `t_permission` VALUES ('44', '2', '53');
INSERT INTO `t_permission` VALUES ('45', '2', '54');
INSERT INTO `t_permission` VALUES ('46', '2', '99');
INSERT INTO `t_permission` VALUES ('47', '2', '55');
INSERT INTO `t_permission` VALUES ('48', '2', '56');
INSERT INTO `t_permission` VALUES ('49', '2', '57');
INSERT INTO `t_permission` VALUES ('100', '2', '21');
INSERT INTO `t_permission` VALUES ('101', '2', '22');
INSERT INTO `t_permission` VALUES ('102', '2', '101');
INSERT INTO `t_permission` VALUES ('103', '2', '102');
INSERT INTO `t_permission` VALUES ('104', '2', '103');
INSERT INTO `t_permission` VALUES ('105', '2', '104');
INSERT INTO `t_permission` VALUES ('106', '2', '105');
INSERT INTO `t_permission` VALUES ('107', '2', '106');
INSERT INTO `t_permission` VALUES ('108', '2', '107');
INSERT INTO `t_permission` VALUES ('109', '2', '111');
INSERT INTO `t_permission` VALUES ('110', '2', '112');
INSERT INTO `t_permission` VALUES ('111', '2', '113');
INSERT INTO `t_permission` VALUES ('112', '2', '114');
INSERT INTO `t_permission` VALUES ('113', '2', '115');
INSERT INTO `t_permission` VALUES ('114', '2', '116');
INSERT INTO `t_permission` VALUES ('115', '2', '117');
INSERT INTO `t_permission` VALUES ('116', '2', '118');
INSERT INTO `t_permission` VALUES ('117', '2', '119');
INSERT INTO `t_permission` VALUES ('118', '2', '120');
INSERT INTO `t_permission` VALUES ('119', '2', '151');
INSERT INTO `t_permission` VALUES ('120', '2', '152');
INSERT INTO `t_permission` VALUES ('121', '2', '153');
INSERT INTO `t_permission` VALUES ('122', '2', '154');
INSERT INTO `t_permission` VALUES ('123', '2', '155');
INSERT INTO `t_permission` VALUES ('124', '2', '161');
INSERT INTO `t_permission` VALUES ('125', '2', '162');
INSERT INTO `t_permission` VALUES ('126', '2', '163');
INSERT INTO `t_permission` VALUES ('127', '2', '164');
INSERT INTO `t_permission` VALUES ('128', '2', '165');
INSERT INTO `t_permission` VALUES ('129', '2', '166');
INSERT INTO `t_permission` VALUES ('130', '2', '167');
INSERT INTO `t_permission` VALUES ('131', '2', '201');
INSERT INTO `t_permission` VALUES ('132', '2', '202');
INSERT INTO `t_permission` VALUES ('133', '2', '203');
INSERT INTO `t_permission` VALUES ('134', '2', '204');
INSERT INTO `t_permission` VALUES ('135', '2', '205');
INSERT INTO `t_permission` VALUES ('136', '2', '206');
INSERT INTO `t_permission` VALUES ('137', '2', '211');
INSERT INTO `t_permission` VALUES ('138', '2', '212');
INSERT INTO `t_permission` VALUES ('139', '2', '213');
INSERT INTO `t_permission` VALUES ('140', '2', '214');
INSERT INTO `t_permission` VALUES ('141', '2', '215');
INSERT INTO `t_permission` VALUES ('142', '2', '216');
INSERT INTO `t_permission` VALUES ('143', '2', '217');
INSERT INTO `t_permission` VALUES ('144', '2', '218');
INSERT INTO `t_permission` VALUES ('145', '2', '219');
INSERT INTO `t_permission` VALUES ('146', '2', '220');
INSERT INTO `t_permission` VALUES ('147', '2', '221');
INSERT INTO `t_permission` VALUES ('148', '2', '222');
INSERT INTO `t_permission` VALUES ('149', '2', '223');
INSERT INTO `t_permission` VALUES ('150', '2', '225');
INSERT INTO `t_permission` VALUES ('151', '2', '226');
INSERT INTO `t_permission` VALUES ('152', '2', '227');
INSERT INTO `t_permission` VALUES ('153', '2', '228');
INSERT INTO `t_permission` VALUES ('154', '2', '229');
INSERT INTO `t_permission` VALUES ('155', '2', '230');
INSERT INTO `t_permission` VALUES ('156', '2', '231');
INSERT INTO `t_permission` VALUES ('157', '2', '251');
INSERT INTO `t_permission` VALUES ('158', '2', '252');
INSERT INTO `t_permission` VALUES ('159', '2', '253');
INSERT INTO `t_permission` VALUES ('160', '2', '254');
INSERT INTO `t_permission` VALUES ('161', '2', '261');
INSERT INTO `t_permission` VALUES ('162', '2', '262');
INSERT INTO `t_permission` VALUES ('163', '2', '263');
INSERT INTO `t_permission` VALUES ('164', '2', '264');
INSERT INTO `t_permission` VALUES ('165', '2', '265');
INSERT INTO `t_permission` VALUES ('166', '2', '266');
INSERT INTO `t_permission` VALUES ('167', '2', '267');
INSERT INTO `t_permission` VALUES ('168', '2', '301');
INSERT INTO `t_permission` VALUES ('169', '2', '302');
INSERT INTO `t_permission` VALUES ('170', '2', '303');
INSERT INTO `t_permission` VALUES ('171', '2', '311');
INSERT INTO `t_permission` VALUES ('172', '2', '312');
INSERT INTO `t_permission` VALUES ('173', '2', '313');
INSERT INTO `t_permission` VALUES ('174', '2', '351');
INSERT INTO `t_permission` VALUES ('175', '2', '352');
INSERT INTO `t_permission` VALUES ('176', '2', '353');
INSERT INTO `t_permission` VALUES ('177', '2', '361');
INSERT INTO `t_permission` VALUES ('178', '2', '362');
INSERT INTO `t_permission` VALUES ('179', '2', '363');
INSERT INTO `t_permission` VALUES ('180', '2', '364');
INSERT INTO `t_permission` VALUES ('181', '2', '401');
INSERT INTO `t_permission` VALUES ('182', '2', '402');
INSERT INTO `t_permission` VALUES ('183', '2', '403');
INSERT INTO `t_permission` VALUES ('184', '2', '411');
INSERT INTO `t_permission` VALUES ('185', '2', '412');
INSERT INTO `t_permission` VALUES ('186', '2', '413');
INSERT INTO `t_permission` VALUES ('187', '2', '414');
INSERT INTO `t_permission` VALUES ('188', '2', '451');
INSERT INTO `t_permission` VALUES ('189', '2', '452');
INSERT INTO `t_permission` VALUES ('190', '2', '453');
INSERT INTO `t_permission` VALUES ('191', '2', '454');
INSERT INTO `t_permission` VALUES ('192', '2', '461');
INSERT INTO `t_permission` VALUES ('193', '2', '462');
INSERT INTO `t_permission` VALUES ('194', '2', '463');
INSERT INTO `t_permission` VALUES ('195', '2', '464');
INSERT INTO `t_permission` VALUES ('196', '2', '465');
INSERT INTO `t_permission` VALUES ('197', '2', '466');
INSERT INTO `t_permission` VALUES ('198', '2', '467');
INSERT INTO `t_permission` VALUES ('199', '2', '468');
INSERT INTO `t_permission` VALUES ('200', '2', '469');
INSERT INTO `t_permission` VALUES ('201', '2', '470');
INSERT INTO `t_permission` VALUES ('202', '2', '501');
INSERT INTO `t_permission` VALUES ('203', '2', '502');
INSERT INTO `t_permission` VALUES ('204', '2', '511');
INSERT INTO `t_permission` VALUES ('205', '2', '551');
INSERT INTO `t_permission` VALUES ('206', '2', '561');
INSERT INTO `t_permission` VALUES ('207', '2', '562');
INSERT INTO `t_permission` VALUES ('208', '2', '563');
INSERT INTO `t_permission` VALUES ('209', '2', '611');
INSERT INTO `t_permission` VALUES ('210', '2', '552');
INSERT INTO `t_permission` VALUES ('211', '2', '553');
INSERT INTO `t_permission` VALUES ('212', '2', '564');
INSERT INTO `t_permission` VALUES ('213', '2', '565');
INSERT INTO `t_permission` VALUES ('214', '2', '566');
INSERT INTO `t_permission` VALUES ('215', '2', '567');
INSERT INTO `t_permission` VALUES ('216', '2', '701');
INSERT INTO `t_permission` VALUES ('217', '2', '711');
INSERT INTO `t_permission` VALUES ('218', '2', '712');
INSERT INTO `t_permission` VALUES ('219', '2', '713');
INSERT INTO `t_permission` VALUES ('220', '2', '751');
INSERT INTO `t_permission` VALUES ('221', '2', '761');
INSERT INTO `t_permission` VALUES ('222', '2', '762');
INSERT INTO `t_permission` VALUES ('223', '2', '801');
INSERT INTO `t_permission` VALUES ('224', '2', '811');
INSERT INTO `t_permission` VALUES ('225', '1', '30');
INSERT INTO `t_permission` VALUES ('226', '2', '168');
INSERT INTO `t_permission` VALUES ('227', '2', '601');
INSERT INTO `t_permission` VALUES ('228', '2', '121');
INSERT INTO `t_permission` VALUES ('229', '2', '232');
INSERT INTO `t_permission` VALUES ('230', '2', '714');
INSERT INTO `t_permission` VALUES ('231', '2', '715');
INSERT INTO `t_permission` VALUES ('232', '2', '122');
INSERT INTO `t_permission` VALUES ('233', '2', '169');
INSERT INTO `t_permission` VALUES ('234', '2', '716');
INSERT INTO `t_permission` VALUES ('235', '2', '365');
INSERT INTO `t_permission` VALUES ('236', '2', '717');
INSERT INTO `t_permission` VALUES ('237', '2', '366');
INSERT INTO `t_permission` VALUES ('238', '2', '901');
INSERT INTO `t_permission` VALUES ('239', '2', '23');
INSERT INTO `t_permission` VALUES ('240', '2', '902');
INSERT INTO `t_permission` VALUES ('241', '2', '903');
INSERT INTO `t_permission` VALUES ('242', '2', '904');
INSERT INTO `t_permission` VALUES ('243', '2', '911');
INSERT INTO `t_permission` VALUES ('244', '2', '912');
INSERT INTO `t_permission` VALUES ('245', '2', '913');
INSERT INTO `t_permission` VALUES ('246', '2', '914');
INSERT INTO `t_permission` VALUES ('247', '2', '915');
INSERT INTO `t_permission` VALUES ('248', '2', '916');
INSERT INTO `t_permission` VALUES ('249', '2', '917');
INSERT INTO `t_permission` VALUES ('250', '2', '367');
INSERT INTO `t_permission` VALUES ('251', '2', '918');
INSERT INTO `t_permission` VALUES ('252', '2', '919');
INSERT INTO `t_permission` VALUES ('253', '2', '920');
INSERT INTO `t_permission` VALUES ('254', '2', '921');
INSERT INTO `t_permission` VALUES ('255', '2', '922');
INSERT INTO `t_permission` VALUES ('256', '2', '233');
INSERT INTO `t_permission` VALUES ('257', '2', '123');
INSERT INTO `t_permission` VALUES ('258', '2', '718');
INSERT INTO `t_permission` VALUES ('259', '2', '812');
INSERT INTO `t_permission` VALUES ('260', '2', '813');
INSERT INTO `t_permission` VALUES ('261', '2', '814');
INSERT INTO `t_permission` VALUES ('262', '2', '923');
INSERT INTO `t_permission` VALUES ('263', '2', '924');
INSERT INTO `t_permission` VALUES ('264', '2', '815');
INSERT INTO `t_permission` VALUES ('265', '2', '124');
INSERT INTO `t_permission` VALUES ('266', '2', '224');
INSERT INTO `t_permission` VALUES ('267', '2', '233');
INSERT INTO `t_permission` VALUES ('268', '2', '925');
INSERT INTO `t_permission` VALUES ('269', '2', '926');
INSERT INTO `t_permission` VALUES ('270', '2', '719');
INSERT INTO `t_permission` VALUES ('271', '2', '720');
INSERT INTO `t_permission` VALUES ('272', '2', '721');
INSERT INTO `t_permission` VALUES ('273', '2', '722');
INSERT INTO `t_permission` VALUES ('274', '2', '723');
INSERT INTO `t_permission` VALUES ('275', '2', '724');
INSERT INTO `t_permission` VALUES ('276', '2', '725');
INSERT INTO `t_permission` VALUES ('277', '2', '726');
INSERT INTO `t_permission` VALUES ('278', '2', '727');
INSERT INTO `t_permission` VALUES ('279', '2', '904');
INSERT INTO `t_permission` VALUES ('280', '2', '927');
INSERT INTO `t_permission` VALUES ('281', '2', '928');
INSERT INTO `t_permission` VALUES ('282', '2', '929');
INSERT INTO `t_permission` VALUES ('283', '2', '930');
INSERT INTO `t_permission` VALUES ('284', '2', '931');
INSERT INTO `t_permission` VALUES ('285', '2', '125');
INSERT INTO `t_permission` VALUES ('286', '2', '932');
INSERT INTO `t_permission` VALUES ('287', '2', '512');
INSERT INTO `t_permission` VALUES ('288', '2', '234');
INSERT INTO `t_permission` VALUES ('289', '2', '1001');
INSERT INTO `t_permission` VALUES ('290', '2', '1011');
INSERT INTO `t_permission` VALUES ('291', '2', '1012');
INSERT INTO `t_permission` VALUES ('292', '2', '1013');
INSERT INTO `t_permission` VALUES ('293', '2', '58');
INSERT INTO `t_permission` VALUES ('294', '2', '915');
INSERT INTO `t_permission` VALUES ('295', '2', '471');
INSERT INTO `t_permission` VALUES ('296', '1', '31');
INSERT INTO `t_permission` VALUES ('297', '2', '905');
INSERT INTO `t_permission` VALUES ('298', '2', '933');
INSERT INTO `t_permission` VALUES ('299', '1', '32');
INSERT INTO `t_permission` VALUES ('300', '2', '906');
INSERT INTO `t_permission` VALUES ('301', '2', '934');
INSERT INTO `t_permission` VALUES ('302', '1', '33');

-- ----------------------------
-- Table structure for `t_quartz_job`
-- ----------------------------
DROP TABLE IF EXISTS `t_quartz_job`;
CREATE TABLE `t_quartz_job` (
  `job_id` varchar(32) NOT NULL COMMENT '任务id',
  `job_name` varchar(32) NOT NULL COMMENT '任务名称',
  `job_group` varchar(32) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务分组',
  `job_status` enum('1','0') NOT NULL DEFAULT '0' COMMENT '任务状态0未开启',
  `concurrent` enum('1','0') DEFAULT '0' COMMENT '是否并发0不是',
  `cron_ex` varchar(32) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `bean_id` varchar(16) DEFAULT NULL COMMENT 'spring中id如果没有根据job_class查找',
  `job_class` varchar(64) DEFAULT NULL COMMENT '包名加类名',
  `method_name` varchar(32) DEFAULT NULL,
  `start_time` varchar(12) DEFAULT NULL COMMENT '开始时间',
  `previous_time` varchar(12) DEFAULT NULL COMMENT '上次执行时间',
  `next_time` varchar(12) DEFAULT NULL COMMENT '下次时间',
  `inversion_method_name` varchar(32) DEFAULT NULL,
  `inversion_class` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时任务信息表';

-- ----------------------------
-- Records of t_quartz_job
-- ----------------------------
INSERT INTO `t_quartz_job` VALUES ('1', 'BackUpJob', 'DEFAULT', '1', '1', '0 40 10 ? * MON', null, null, 'com.goldencis.osa.job.BackUpJob', 'backUp', null, null, null, null, null);
INSERT INTO `t_quartz_job` VALUES ('2', 'DatabaseCleanJob', 'DEFAULT', '1', '1', '0 /1 * * * ? ', null, null, 'com.goldencis.osa.job.DatabaseCleanJob', 'clean', null, null, null, null, null);
INSERT INTO `t_quartz_job` VALUES ('3', 'ChangePasswordJob', 'DEFAULT', '1', '1', '11 11 11 11 11 ? 2099', NULL, NULL, 'com.goldencis.osa.job.ChangePasswordJob', 'changePassword', NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for `t_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `guid` varchar(36) NOT NULL COMMENT '主键.唯一标示',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `type` int(11) NOT NULL COMMENT '角色类型:1为系统角色，2为运维用户角色，3为自定义角色',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `icon` varchar(45) DEFAULT NULL COMMENT '角色图标',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES ('1', '管理员', '1', '系统管理员', 'icon-admin', '2018-10-13 14:37:24');
INSERT INTO `t_role` VALUES ('2', '操作员', '1', '系统操作员', 'icon-user', '2018-10-13 14:37:50');
INSERT INTO `t_role` VALUES ('3', '审计员', '1', '系统审计员', 'icon-comptoller', '2018-10-13 14:38:04');
INSERT INTO `t_role` VALUES ('4', '普通用户', '2', '普通用户', null, '2018-10-13 14:38:04');

-- ----------------------------
-- Table structure for `t_role_permission`
-- ----------------------------
DROP TABLE IF EXISTS `t_role_permission`;
CREATE TABLE `t_role_permission` (
  `role_guid` varchar(36) NOT NULL COMMENT '角色guid',
  `permission_id` int(11) NOT NULL COMMENT '权限id',
  PRIMARY KEY (`role_guid`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色权限表';

-- ----------------------------
-- Records of t_role_permission
-- ----------------------------
INSERT INTO `t_role_permission` VALUES ('1', '1');
INSERT INTO `t_role_permission` VALUES ('1', '2');
INSERT INTO `t_role_permission` VALUES ('1', '7');
INSERT INTO `t_role_permission` VALUES ('1', '9');
INSERT INTO `t_role_permission` VALUES ('1', '11');
INSERT INTO `t_role_permission` VALUES ('1', '12');
INSERT INTO `t_role_permission` VALUES ('1', '18');
INSERT INTO `t_role_permission` VALUES ('1', '19');
INSERT INTO `t_role_permission` VALUES ('1', '22');
INSERT INTO `t_role_permission` VALUES ('1', '23');
INSERT INTO `t_role_permission` VALUES ('1', '24');
INSERT INTO `t_role_permission` VALUES ('1', '25');
INSERT INTO `t_role_permission` VALUES ('1', '26');
INSERT INTO `t_role_permission` VALUES ('1', '29');
INSERT INTO `t_role_permission` VALUES ('1', '42');
INSERT INTO `t_role_permission` VALUES ('1', '44');
INSERT INTO `t_role_permission` VALUES ('1', '45');
INSERT INTO `t_role_permission` VALUES ('1', '46');
INSERT INTO `t_role_permission` VALUES ('1', '47');
INSERT INTO `t_role_permission` VALUES ('1', '48');
INSERT INTO `t_role_permission` VALUES ('1', '49');
INSERT INTO `t_role_permission` VALUES ('1', '102');
INSERT INTO `t_role_permission` VALUES ('1', '103');
INSERT INTO `t_role_permission` VALUES ('1', '104');
INSERT INTO `t_role_permission` VALUES ('1', '105');
INSERT INTO `t_role_permission` VALUES ('1', '106');
INSERT INTO `t_role_permission` VALUES ('1', '107');
INSERT INTO `t_role_permission` VALUES ('1', '109');
INSERT INTO `t_role_permission` VALUES ('1', '110');
INSERT INTO `t_role_permission` VALUES ('1', '111');
INSERT INTO `t_role_permission` VALUES ('1', '112');
INSERT INTO `t_role_permission` VALUES ('1', '113');
INSERT INTO `t_role_permission` VALUES ('1', '114');
INSERT INTO `t_role_permission` VALUES ('1', '115');
INSERT INTO `t_role_permission` VALUES ('1', '116');
INSERT INTO `t_role_permission` VALUES ('1', '117');
INSERT INTO `t_role_permission` VALUES ('1', '119');
INSERT INTO `t_role_permission` VALUES ('1', '120');
INSERT INTO `t_role_permission` VALUES ('1', '121');
INSERT INTO `t_role_permission` VALUES ('1', '122');
INSERT INTO `t_role_permission` VALUES ('1', '124');
INSERT INTO `t_role_permission` VALUES ('1', '125');
INSERT INTO `t_role_permission` VALUES ('1', '126');
INSERT INTO `t_role_permission` VALUES ('1', '127');
INSERT INTO `t_role_permission` VALUES ('1', '128');
INSERT INTO `t_role_permission` VALUES ('1', '129');
INSERT INTO `t_role_permission` VALUES ('1', '217');
INSERT INTO `t_role_permission` VALUES ('1', '218');
INSERT INTO `t_role_permission` VALUES ('1', '219');
INSERT INTO `t_role_permission` VALUES ('1', '220');
INSERT INTO `t_role_permission` VALUES ('1', '221');
INSERT INTO `t_role_permission` VALUES ('1', '222');
INSERT INTO `t_role_permission` VALUES ('1', '223');
INSERT INTO `t_role_permission` VALUES ('1', '224');
INSERT INTO `t_role_permission` VALUES ('1', '226');
INSERT INTO `t_role_permission` VALUES ('1', '228');
INSERT INTO `t_role_permission` VALUES ('1', '230');
INSERT INTO `t_role_permission` VALUES ('1', '231');
INSERT INTO `t_role_permission` VALUES ('1', '232');
INSERT INTO `t_role_permission` VALUES ('1', '233');
INSERT INTO `t_role_permission` VALUES ('1', '234');
INSERT INTO `t_role_permission` VALUES ('1', '236');
INSERT INTO `t_role_permission` VALUES ('1', '238');
INSERT INTO `t_role_permission` VALUES ('1', '240');
INSERT INTO `t_role_permission` VALUES ('1', '241');
INSERT INTO `t_role_permission` VALUES ('1', '242');
INSERT INTO `t_role_permission` VALUES ('1', '243');
INSERT INTO `t_role_permission` VALUES ('1', '244');
INSERT INTO `t_role_permission` VALUES ('1', '245');
INSERT INTO `t_role_permission` VALUES ('1', '246');
INSERT INTO `t_role_permission` VALUES ('1', '248');
INSERT INTO `t_role_permission` VALUES ('1', '249');
INSERT INTO `t_role_permission` VALUES ('1', '251');
INSERT INTO `t_role_permission` VALUES ('1', '252');
INSERT INTO `t_role_permission` VALUES ('1', '255');
INSERT INTO `t_role_permission` VALUES ('1', '257');
INSERT INTO `t_role_permission` VALUES ('1', '258');
INSERT INTO `t_role_permission` VALUES ('1', '259');
INSERT INTO `t_role_permission` VALUES ('1', '260');
INSERT INTO `t_role_permission` VALUES ('1', '261');
INSERT INTO `t_role_permission` VALUES ('1', '262');
INSERT INTO `t_role_permission` VALUES ('1', '263');
INSERT INTO `t_role_permission` VALUES ('1', '264');
INSERT INTO `t_role_permission` VALUES ('1', '265');
INSERT INTO `t_role_permission` VALUES ('1', '268');
INSERT INTO `t_role_permission` VALUES ('1', '269');
INSERT INTO `t_role_permission` VALUES ('1', '270');
INSERT INTO `t_role_permission` VALUES ('1', '271');
INSERT INTO `t_role_permission` VALUES ('1', '272');
INSERT INTO `t_role_permission` VALUES ('1', '273');
INSERT INTO `t_role_permission` VALUES ('1', '274');
INSERT INTO `t_role_permission` VALUES ('1', '275');
INSERT INTO `t_role_permission` VALUES ('1', '276');
INSERT INTO `t_role_permission` VALUES ('1', '277');
INSERT INTO `t_role_permission` VALUES ('1', '278');
INSERT INTO `t_role_permission` VALUES ('1', '279');
INSERT INTO `t_role_permission` VALUES ('1', '280');
INSERT INTO `t_role_permission` VALUES ('1', '281');
INSERT INTO `t_role_permission` VALUES ('1', '282');
INSERT INTO `t_role_permission` VALUES ('1', '283');
INSERT INTO `t_role_permission` VALUES ('1', '284');
INSERT INTO `t_role_permission` VALUES ('1', '285');
INSERT INTO `t_role_permission` VALUES ('1', '286');
INSERT INTO `t_role_permission` VALUES ('1', '289');
INSERT INTO `t_role_permission` VALUES ('1', '290');
INSERT INTO `t_role_permission` VALUES ('1', '291');
INSERT INTO `t_role_permission` VALUES ('1', '292');
INSERT INTO `t_role_permission` VALUES ('1', '293');
INSERT INTO `t_role_permission` VALUES ('1', '294');
INSERT INTO `t_role_permission` VALUES ('1', '295');
INSERT INTO `t_role_permission` VALUES ('1', '296');
INSERT INTO `t_role_permission` VALUES ('1', '297');
INSERT INTO `t_role_permission` VALUES ('1', '298');
INSERT INTO `t_role_permission` VALUES ('1', '299');
INSERT INTO `t_role_permission` VALUES ('1', '300');
INSERT INTO `t_role_permission` VALUES ('1', '301');
INSERT INTO `t_role_permission` VALUES ('1', '302');
INSERT INTO `t_role_permission` VALUES ('2', '1');
INSERT INTO `t_role_permission` VALUES ('2', '2');
INSERT INTO `t_role_permission` VALUES ('2', '3');
INSERT INTO `t_role_permission` VALUES ('2', '4');
INSERT INTO `t_role_permission` VALUES ('2', '6');
INSERT INTO `t_role_permission` VALUES ('2', '11');
INSERT INTO `t_role_permission` VALUES ('2', '12');
INSERT INTO `t_role_permission` VALUES ('2', '13');
INSERT INTO `t_role_permission` VALUES ('2', '14');
INSERT INTO `t_role_permission` VALUES ('2', '15');
INSERT INTO `t_role_permission` VALUES ('2', '16');
INSERT INTO `t_role_permission` VALUES ('2', '42');
INSERT INTO `t_role_permission` VALUES ('2', '44');
INSERT INTO `t_role_permission` VALUES ('2', '45');
INSERT INTO `t_role_permission` VALUES ('2', '47');
INSERT INTO `t_role_permission` VALUES ('2', '48');
INSERT INTO `t_role_permission` VALUES ('2', '49');
INSERT INTO `t_role_permission` VALUES ('2', '102');
INSERT INTO `t_role_permission` VALUES ('2', '103');
INSERT INTO `t_role_permission` VALUES ('2', '104');
INSERT INTO `t_role_permission` VALUES ('2', '105');
INSERT INTO `t_role_permission` VALUES ('2', '106');
INSERT INTO `t_role_permission` VALUES ('2', '107');
INSERT INTO `t_role_permission` VALUES ('2', '108');
INSERT INTO `t_role_permission` VALUES ('2', '109');
INSERT INTO `t_role_permission` VALUES ('2', '110');
INSERT INTO `t_role_permission` VALUES ('2', '111');
INSERT INTO `t_role_permission` VALUES ('2', '112');
INSERT INTO `t_role_permission` VALUES ('2', '113');
INSERT INTO `t_role_permission` VALUES ('2', '114');
INSERT INTO `t_role_permission` VALUES ('2', '115');
INSERT INTO `t_role_permission` VALUES ('2', '116');
INSERT INTO `t_role_permission` VALUES ('2', '117');
INSERT INTO `t_role_permission` VALUES ('2', '118');
INSERT INTO `t_role_permission` VALUES ('2', '119');
INSERT INTO `t_role_permission` VALUES ('2', '120');
INSERT INTO `t_role_permission` VALUES ('2', '121');
INSERT INTO `t_role_permission` VALUES ('2', '122');
INSERT INTO `t_role_permission` VALUES ('2', '123');
INSERT INTO `t_role_permission` VALUES ('2', '124');
INSERT INTO `t_role_permission` VALUES ('2', '125');
INSERT INTO `t_role_permission` VALUES ('2', '126');
INSERT INTO `t_role_permission` VALUES ('2', '127');
INSERT INTO `t_role_permission` VALUES ('2', '128');
INSERT INTO `t_role_permission` VALUES ('2', '129');
INSERT INTO `t_role_permission` VALUES ('2', '130');
INSERT INTO `t_role_permission` VALUES ('2', '131');
INSERT INTO `t_role_permission` VALUES ('2', '132');
INSERT INTO `t_role_permission` VALUES ('2', '133');
INSERT INTO `t_role_permission` VALUES ('2', '134');
INSERT INTO `t_role_permission` VALUES ('2', '135');
INSERT INTO `t_role_permission` VALUES ('2', '136');
INSERT INTO `t_role_permission` VALUES ('2', '137');
INSERT INTO `t_role_permission` VALUES ('2', '138');
INSERT INTO `t_role_permission` VALUES ('2', '139');
INSERT INTO `t_role_permission` VALUES ('2', '140');
INSERT INTO `t_role_permission` VALUES ('2', '141');
INSERT INTO `t_role_permission` VALUES ('2', '142');
INSERT INTO `t_role_permission` VALUES ('2', '143');
INSERT INTO `t_role_permission` VALUES ('2', '144');
INSERT INTO `t_role_permission` VALUES ('2', '145');
INSERT INTO `t_role_permission` VALUES ('2', '146');
INSERT INTO `t_role_permission` VALUES ('2', '147');
INSERT INTO `t_role_permission` VALUES ('2', '148');
INSERT INTO `t_role_permission` VALUES ('2', '149');
INSERT INTO `t_role_permission` VALUES ('2', '150');
INSERT INTO `t_role_permission` VALUES ('2', '151');
INSERT INTO `t_role_permission` VALUES ('2', '152');
INSERT INTO `t_role_permission` VALUES ('2', '153');
INSERT INTO `t_role_permission` VALUES ('2', '154');
INSERT INTO `t_role_permission` VALUES ('2', '155');
INSERT INTO `t_role_permission` VALUES ('2', '156');
INSERT INTO `t_role_permission` VALUES ('2', '157');
INSERT INTO `t_role_permission` VALUES ('2', '158');
INSERT INTO `t_role_permission` VALUES ('2', '159');
INSERT INTO `t_role_permission` VALUES ('2', '160');
INSERT INTO `t_role_permission` VALUES ('2', '161');
INSERT INTO `t_role_permission` VALUES ('2', '162');
INSERT INTO `t_role_permission` VALUES ('2', '163');
INSERT INTO `t_role_permission` VALUES ('2', '164');
INSERT INTO `t_role_permission` VALUES ('2', '165');
INSERT INTO `t_role_permission` VALUES ('2', '166');
INSERT INTO `t_role_permission` VALUES ('2', '167');
INSERT INTO `t_role_permission` VALUES ('2', '168');
INSERT INTO `t_role_permission` VALUES ('2', '169');
INSERT INTO `t_role_permission` VALUES ('2', '170');
INSERT INTO `t_role_permission` VALUES ('2', '171');
INSERT INTO `t_role_permission` VALUES ('2', '172');
INSERT INTO `t_role_permission` VALUES ('2', '173');
INSERT INTO `t_role_permission` VALUES ('2', '174');
INSERT INTO `t_role_permission` VALUES ('2', '175');
INSERT INTO `t_role_permission` VALUES ('2', '176');
INSERT INTO `t_role_permission` VALUES ('2', '177');
INSERT INTO `t_role_permission` VALUES ('2', '178');
INSERT INTO `t_role_permission` VALUES ('2', '179');
INSERT INTO `t_role_permission` VALUES ('2', '180');
INSERT INTO `t_role_permission` VALUES ('2', '188');
INSERT INTO `t_role_permission` VALUES ('2', '189');
INSERT INTO `t_role_permission` VALUES ('2', '190');
INSERT INTO `t_role_permission` VALUES ('2', '191');
INSERT INTO `t_role_permission` VALUES ('2', '192');
INSERT INTO `t_role_permission` VALUES ('2', '193');
INSERT INTO `t_role_permission` VALUES ('2', '194');
INSERT INTO `t_role_permission` VALUES ('2', '195');
INSERT INTO `t_role_permission` VALUES ('2', '196');
INSERT INTO `t_role_permission` VALUES ('2', '197');
INSERT INTO `t_role_permission` VALUES ('2', '198');
INSERT INTO `t_role_permission` VALUES ('2', '199');
INSERT INTO `t_role_permission` VALUES ('2', '200');
INSERT INTO `t_role_permission` VALUES ('2', '201');
INSERT INTO `t_role_permission` VALUES ('2', '223');
INSERT INTO `t_role_permission` VALUES ('2', '224');
INSERT INTO `t_role_permission` VALUES ('2', '226');
INSERT INTO `t_role_permission` VALUES ('2', '228');
INSERT INTO `t_role_permission` VALUES ('2', '229');
INSERT INTO `t_role_permission` VALUES ('2', '232');
INSERT INTO `t_role_permission` VALUES ('2', '233');
INSERT INTO `t_role_permission` VALUES ('2', '235');
INSERT INTO `t_role_permission` VALUES ('2', '237');
INSERT INTO `t_role_permission` VALUES ('2', '239');
INSERT INTO `t_role_permission` VALUES ('2', '250');
INSERT INTO `t_role_permission` VALUES ('2', '256');
INSERT INTO `t_role_permission` VALUES ('2', '257');
INSERT INTO `t_role_permission` VALUES ('2', '259');
INSERT INTO `t_role_permission` VALUES ('2', '260');
INSERT INTO `t_role_permission` VALUES ('2', '261');
INSERT INTO `t_role_permission` VALUES ('2', '264');
INSERT INTO `t_role_permission` VALUES ('2', '265');
INSERT INTO `t_role_permission` VALUES ('2', '266');
INSERT INTO `t_role_permission` VALUES ('2', '267');
INSERT INTO `t_role_permission` VALUES ('2', '288');
INSERT INTO `t_role_permission` VALUES ('2', '293');
INSERT INTO `t_role_permission` VALUES ('2', '295');
INSERT INTO `t_role_permission` VALUES ('3', '1');
INSERT INTO `t_role_permission` VALUES ('3', '5');
INSERT INTO `t_role_permission` VALUES ('3', '8');
INSERT INTO `t_role_permission` VALUES ('3', '20');
INSERT INTO `t_role_permission` VALUES ('3', '21');
INSERT INTO `t_role_permission` VALUES ('3', '42');
INSERT INTO `t_role_permission` VALUES ('3', '44');
INSERT INTO `t_role_permission` VALUES ('3', '45');
INSERT INTO `t_role_permission` VALUES ('3', '47');
INSERT INTO `t_role_permission` VALUES ('3', '48');
INSERT INTO `t_role_permission` VALUES ('3', '49');
INSERT INTO `t_role_permission` VALUES ('3', '181');
INSERT INTO `t_role_permission` VALUES ('3', '182');
INSERT INTO `t_role_permission` VALUES ('3', '184');
INSERT INTO `t_role_permission` VALUES ('3', '185');
INSERT INTO `t_role_permission` VALUES ('3', '186');
INSERT INTO `t_role_permission` VALUES ('3', '202');
INSERT INTO `t_role_permission` VALUES ('3', '204');
INSERT INTO `t_role_permission` VALUES ('3', '223');
INSERT INTO `t_role_permission` VALUES ('3', '224');
INSERT INTO `t_role_permission` VALUES ('3', '225');
INSERT INTO `t_role_permission` VALUES ('3', '259');
INSERT INTO `t_role_permission` VALUES ('3', '260');
INSERT INTO `t_role_permission` VALUES ('3', '261');
INSERT INTO `t_role_permission` VALUES ('3', '264');
INSERT INTO `t_role_permission` VALUES ('3', '287');
INSERT INTO `t_role_permission` VALUES ('3', '293');
INSERT INTO `t_role_permission` VALUES ('4', '10');
INSERT INTO `t_role_permission` VALUES ('4', '42');
INSERT INTO `t_role_permission` VALUES ('4', '43');
INSERT INTO `t_role_permission` VALUES ('4', '44');
INSERT INTO `t_role_permission` VALUES ('4', '45');
INSERT INTO `t_role_permission` VALUES ('4', '48');
INSERT INTO `t_role_permission` VALUES ('4', '49');
INSERT INTO `t_role_permission` VALUES ('4', '111');
INSERT INTO `t_role_permission` VALUES ('4', '140');
INSERT INTO `t_role_permission` VALUES ('4', '205');
INSERT INTO `t_role_permission` VALUES ('4', '206');
INSERT INTO `t_role_permission` VALUES ('4', '207');
INSERT INTO `t_role_permission` VALUES ('4', '208');
INSERT INTO `t_role_permission` VALUES ('4', '209');
INSERT INTO `t_role_permission` VALUES ('4', '210');
INSERT INTO `t_role_permission` VALUES ('4', '211');
INSERT INTO `t_role_permission` VALUES ('4', '212');
INSERT INTO `t_role_permission` VALUES ('4', '213');
INSERT INTO `t_role_permission` VALUES ('4', '214');
INSERT INTO `t_role_permission` VALUES ('4', '215');
INSERT INTO `t_role_permission` VALUES ('4', '289');
INSERT INTO `t_role_permission` VALUES ('4', '293');

-- ----------------------------
-- Table structure for `t_sso_configuration`
-- ----------------------------
DROP TABLE IF EXISTS `t_sso_configuration`;
CREATE TABLE `t_sso_configuration` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一标示',
  `user_guid` varchar(36) NOT NULL COMMENT '配置所属用户的唯一标示',
  `asset_id` int(11) NOT NULL COMMENT '配置所属设备的唯一标示',
  `asset_type` int(11) DEFAULT NULL COMMENT '设备类型',
  `account_id` int(11) NOT NULL COMMENT '配置所属账户的唯一标示',
  `configuration` varchar(2000) DEFAULT NULL COMMENT '单点登录配置项，使用json格式记录，不涉及查询',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='单点登录配置表';

-- ----------------------------
-- Records of t_sso_configuration
-- ----------------------------
INSERT INTO `t_sso_configuration` VALUES ('1', '7912f48c-b2d8-45c7-b17d-e8f49d4144d5', '20', '1', '32', '{\"logonToolsType\":\"client\",\"logonMethod\":\"character\",\"protocol\":\"ssh\",\"port\":22,\"logonTools\":\"XShell\",\"openType\":\"tab\"}');
INSERT INTO `t_sso_configuration` VALUES ('2', 'ab4f47ac-bfff-4e5c-852a-dc079938d868', '36', '1', '54', '{\"logonToolsType\":\"client\",\"logonTools\":\"XShell\",\"openType\":\"tab\",\"logonMethod\":\"character\",\"protocol\":\"ssh\",\"port\":22}');
INSERT INTO `t_sso_configuration` VALUES ('3', '4e2f9538-3de2-4170-98d0-90fc7c10e19a', '37', '2', '55', '{\"logonMethod\":\"graphical\",\"protocol\":\"rdp\",\"port\":3389,\"resolution\":\"fullScreen\",\"shareDriver\":\"\",\"shareShearPlate\":true}');
INSERT INTO `t_sso_configuration` VALUES ('4', '4e2f9538-3de2-4170-98d0-90fc7c10e19a', '16', '2', '59', '{\"logonMethod\":\"graphical\",\"protocol\":\"rdp\",\"port\":3389,\"resolution\":\"fullScreen\",\"shareDriver\":\"C\",\"shareShearPlate\":true}');

-- ----------------------------
-- Table structure for `t_sso_rule`
-- ----------------------------
DROP TABLE IF EXISTS `t_sso_rule`;
CREATE TABLE `t_sso_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `asset_id` int(11) NOT NULL COMMENT '设备id',
  `name` varchar(255) DEFAULT NULL COMMENT '规则名称',
  `tool_type` int(11) DEFAULT NULL COMMENT '工具类型',
  `rule` text COMMENT '规则(规则字符串)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='SSO(单点登录)规则';

-- ----------------------------
-- Records of t_sso_rule
-- ----------------------------
INSERT INTO `t_sso_rule` VALUES ('5', '25', '启动calc', '8', 'exec=C:\\Windows\\System32\\calc.exe');

-- ----------------------------
-- Table structure for `t_sso_rule_attr`
-- ----------------------------
DROP TABLE IF EXISTS `t_sso_rule_attr`;
CREATE TABLE `t_sso_rule_attr` (
  `value` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` int(11) DEFAULT NULL COMMENT '工具类型(目前无用)',
  `name` varchar(255) NOT NULL COMMENT '界面上显示的名称',
  `nickname` varchar(255) NOT NULL COMMENT '规则名',
  `default_content` varchar(255) DEFAULT NULL COMMENT '默认值',
  PRIMARY KEY (`value`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='sso(单点登录)规则属性';

-- ----------------------------
-- Records of t_sso_rule_attr
-- ----------------------------
INSERT INTO `t_sso_rule_attr` VALUES ('1', null, '执行应用', 'exec', null);
INSERT INTO `t_sso_rule_attr` VALUES ('2', null, '定位窗口', 'waitWindow', null);
INSERT INTO `t_sso_rule_attr` VALUES ('3', null, '鼠标左键点击', 'mouseLClick', null);
INSERT INTO `t_sso_rule_attr` VALUES ('4', null, '鼠标右键点击', 'mouseRClick', null);
INSERT INTO `t_sso_rule_attr` VALUES ('5', null, '等待时间(毫秒)', 'sleep', null);
INSERT INTO `t_sso_rule_attr` VALUES ('6', null, '编辑框输入', 'editInput', null);
INSERT INTO `t_sso_rule_attr` VALUES ('7', null, '写入数据', 'keyInput', null);
INSERT INTO `t_sso_rule_attr` VALUES ('8', NULL, '启动浏览器', 'webStartBrowser', NULL);
INSERT INTO `t_sso_rule_attr` VALUES ('9', NULL, '浏览器输入', 'webInputText', NULL);
INSERT INTO `t_sso_rule_attr` VALUES ('10', NULL, '输入验证码', 'webCheckCode', NULL);
INSERT INTO `t_sso_rule_attr` VALUES ('11', NULL, '浏览器点击', 'webButClick', NULL);

-- ----------------------------
-- Table structure for `t_strategy`
-- ----------------------------
DROP TABLE IF EXISTS `t_strategy`;
CREATE TABLE `t_strategy` (
  `guid` varchar(36) COLLATE utf8_bin NOT NULL COMMENT '主键.唯一标示',
  `name` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '策略名称',
  `session_type` varchar(30) COLLATE utf8_bin DEFAULT NULL COMMENT '会话选项',
  `rdp` varchar(30) COLLATE utf8_bin DEFAULT NULL COMMENT 'RDP选项',
  `ssh` varchar(30) COLLATE utf8_bin DEFAULT NULL COMMENT 'SSH选项',
  `screen_watermark` varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '屏幕水印json',
  `file_mon` varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '文件审计json',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '创建者guid',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `updtae_by` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '修改者guid',
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_strategy
-- ----------------------------
INSERT INTO `t_strategy` VALUES ('1', '默认策略', null, null, null,null, null, '2018-12-03 19:05:46', '1', '2018-12-20 10:53:01', '1');

-- ----------------------------
-- Table structure for `t_strategy_command`
-- ----------------------------
DROP TABLE IF EXISTS `t_strategy_command`;
CREATE TABLE `t_strategy_command` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，唯一标识',
  `type` int(11) NOT NULL COMMENT '命令类型，1阻断会话命令，2待审核命令，3禁止执行命令',
  `command_content` text COLLATE utf8_bin COMMENT '命令内容',
  `strategy_id` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '策略guid',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人guid',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_strategy_command
-- ----------------------------

-- ----------------------------
-- Table structure for `t_strategy_logintime`
-- ----------------------------
DROP TABLE IF EXISTS `t_strategy_logintime`;
CREATE TABLE `t_strategy_logintime` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，唯一标识',
  `strategy_id` varchar(36) COLLATE utf8_bin NOT NULL COMMENT '策略guid',
  `day_type` int(11) DEFAULT NULL COMMENT '周期类型(1周一，2 周二，3周三...7周日)',
  `start_hourtime` time DEFAULT NULL COMMENT '开始时间',
  `end_hourtime` time DEFAULT NULL COMMENT '结束时间',
  `time_zone` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '星期、时间对应表',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '创建者guid',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_strategy_logintime
-- ----------------------------
INSERT INTO `t_strategy_logintime` VALUES ('1', '1', '1', '00:00:00', '23:59:00', null, '2019-01-04 14:36:21', '1');
INSERT INTO `t_strategy_logintime` VALUES ('2', '1', '2', '00:00:00', '23:59:00', null, '2019-01-04 14:36:21', '1');
INSERT INTO `t_strategy_logintime` VALUES ('3', '1', '3', '00:00:00', '23:59:00', null, '2019-01-04 14:36:21', '1');
INSERT INTO `t_strategy_logintime` VALUES ('4', '1', '4', '00:00:00', '23:59:00', null, '2019-01-04 14:36:21', '1');
INSERT INTO `t_strategy_logintime` VALUES ('5', '1', '5', '00:00:00', '23:59:00', null, '2019-01-04 14:36:21', '1');
INSERT INTO `t_strategy_logintime` VALUES ('6', '1', '6', '00:00:00', '23:59:00', null, '2019-01-04 14:36:21', '1');
INSERT INTO `t_strategy_logintime` VALUES ('7', '1', '7', '00:00:00', '23:59:00', null, '2019-01-04 14:36:21', '1');

-- ----------------------------
-- Table structure for `t_system_set`
-- ----------------------------
DROP TABLE IF EXISTS `t_system_set`;
CREATE TABLE `t_system_set` (
  `code` varchar(128) NOT NULL COMMENT '系统设置类型',
  `content` varchar(4000) NOT NULL COMMENT '设置内容',
  `type` varchar(20) NOT NULL COMMENT '归类',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_system_set
-- ----------------------------
INSERT INTO `t_system_set` VALUES ('Platform', '{\"security\":{\"number\":true,\"capital\":false,\"lowercase\":true,\"minLength\":7,\"tryCount\":3,\"lockDuration\":10},\"approval\":{\"expireTime\":1,\"expireResult\":-1},\"access\":{\"login\":{\"username\":[\"zd\"],\"ip\":[\"127.0.0.2\"]},\"resource\":{\"ip\":[\"3.3.3.3\"]}},\"custom\":{\"systemName\":\"OSA\",\"background\":\"https://gss3.bdstatic.com/-Po3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=d125a20abede9c82b268f1dd0de8eb6f/3ac79f3df8dcd100f9dcbe127f8b4710b9122f44.jpg\"}}', 'Platform', '2018-09-18 16:51:57');
INSERT INTO `t_system_set` VALUES ('LogServer', '{\"name\":\"第三方日志服务器\",\"url\":\"http://www.baidu.com\"}', 'LogServer', '2018-12-28 19:52:05');
INSERT INTO `t_system_set` VALUES ('DataBaseClean', '{\n	\"day\":\"61\",\n	\"status\":\"1\"\n}', 'DataBaseClean', '2019-01-04 14:47:58');
INSERT INTO `t_system_set` VALUES ('AutoBackUp', '{\n	\"time\":\"10:40\",\n	\"cycle\":\"week\",\n	\"day\":\"1\",\n	\"status\":\"1\"\n}', 'AutoBackUp', '2019-01-07 10:35:37');
INSERT INTO `t_system_set` VALUES ('MailConfig', '{\"serverAddress\":null,\"port\":null,\"from\":null,\"password\":null}', 'MailConfig', '2019-01-23 16:05:46');
INSERT INTO `t_system_set` VALUES ('AccessControlConfig', '{\"status\":false,\"mac\":\"\",\"ips\":[],\"ports\":[]}', 'AccessControlConfig', '2019-01-30 15:42:15');

-- ----------------------------
-- Table structure for `t_terminal`
-- ----------------------------
DROP TABLE IF EXISTS `t_terminal`;
CREATE TABLE `t_terminal` (
  `id` varchar(36) NOT NULL COMMENT '唯一标示',
  `name` varchar(32) NOT NULL COMMENT '终端名称',
  `remote_addr` varchar(128) NOT NULL COMMENT '终端地址',
  `ssh_port` int(11) NOT NULL COMMENT 'ssh端口',
  `http_port` int(11) NOT NULL COMMENT 'HTTP端口',
  `is_accepted` tinyint(1) NOT NULL COMMENT '是否对接',
  `is_deleted` tinyint(1) NOT NULL COMMENT '是否删除(逻辑)',
  `date_created` datetime NOT NULL COMMENT '创建时间',
  `comment` longtext COMMENT '备注',
  `userguid` varchar(36) DEFAULT NULL COMMENT '用户guid',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`userguid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='终端表';

-- ----------------------------
-- Records of t_terminal
-- ----------------------------

-- ----------------------------
-- Table structure for `t_terminal_command`
-- ----------------------------
DROP TABLE IF EXISTS `t_terminal_command`;
CREATE TABLE `t_terminal_command` (
  `id` varchar(36) NOT NULL COMMENT '唯一id',
  `user` varchar(64) NOT NULL COMMENT '用户名',
  `asset` varchar(128) NOT NULL COMMENT '设备名称',
  `system_user` varchar(64) NOT NULL COMMENT '账户名称',
  `input` varchar(128) NOT NULL COMMENT '输入',
  `output` varchar(1024) NOT NULL COMMENT '输出',
  `session` varchar(36) NOT NULL COMMENT '关联session_id',
  `status` int(11) NOT NULL COMMENT '命令状态：0代表审批中，1代表审批通过，-1代表审批拒绝，2代表已正常，3代表禁止，4代表阻断，5代表无效',
  `approve_id` int(11) DEFAULT NULL COMMENT '审批id',
  `timestamp` int(11) NOT NULL COMMENT '提交时间',
  `org_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `terminal_command_input_9acfd946` (`input`) USING BTREE,
  KEY `terminal_command_session_62eaa2c3` (`session`) USING BTREE,
  KEY `terminal_command_timestamp_85bc8045` (`timestamp`) USING BTREE,
  KEY `terminal_command_asset_a8743384` (`asset`) USING BTREE,
  KEY `terminal_command_system_user_224671ed` (`system_user`) USING BTREE,
  KEY `terminal_command_user_62507ff6` (`user`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_terminal_command
-- ----------------------------

-- ----------------------------
-- Table structure for `t_terminal_command_replay`
-- ----------------------------
DROP TABLE IF EXISTS `t_terminal_command_replay`;
CREATE TABLE `t_terminal_command_replay` (
  `id` varchar(36) NOT NULL COMMENT '主键（自增长）',
  `filename` varchar(200) NOT NULL COMMENT '文件名称',
  `filepath` varchar(200) NOT NULL COMMENT '文件存储路径',
  `session_id` varchar(36) NOT NULL COMMENT '会话的唯一标示',
  `create_time` timestamp NOT NULL COMMENT '回放提交时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_terminal_command_replay
-- ----------------------------

-- ----------------------------
-- Table structure for `t_terminal_replay`
-- ----------------------------
DROP TABLE IF EXISTS `t_terminal_replay`;
CREATE TABLE `t_terminal_replay` (
  `id` varchar(36) NOT NULL COMMENT '主键（自增长）',
  `filename` varchar(200) NOT NULL COMMENT '文件名称',
  `filepath` varchar(200) DEFAULT NULL COMMENT '文件存储路径',
  `session_id` varchar(36) NOT NULL COMMENT '会话的唯一标示',
  `session_start` int(11) NOT NULL COMMENT '该会话开始的相对时间，以秒为单位',
  `session_end` int(11) NOT NULL COMMENT '该会话结束的相对时间，以秒为单位',
  `seq` int(11) NOT NULL COMMENT '回访文件顺序',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '视频开始绝对时间14位 04d-02d-02d 02d:02d:02d',
  `end_time` timestamp NOT NULL COMMENT '视频结束绝对时间14位',
  `create_time` timestamp NOT NULL COMMENT '视频提交时间14位',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_terminal_replay
-- ----------------------------

-- ----------------------------
-- Table structure for `t_terminal_session`
-- ----------------------------
DROP TABLE IF EXISTS `t_terminal_session`;
CREATE TABLE `t_terminal_session` (
  `id` varchar(36) NOT NULL COMMENT '唯一标示',
  `user_username` varchar(50) NOT NULL COMMENT '用户名',
  `user_name` varchar(50) NOT NULL COMMENT '用户姓名',
  `remote_addr` varchar(15) NOT NULL COMMENT '用户地址',
  `user_guid` varchar(36) NOT NULL COMMENT '用户guid',
  `asset_name` varchar(50) NOT NULL COMMENT '目标设备名称',
  `asset_addr` varchar(15) NOT NULL COMMENT '目标设备ip',
  `asset_id` int(11) NOT NULL COMMENT '设备id',
  `system_user` varchar(128) NOT NULL COMMENT '目标设备账号',
  `login_from` varchar(2) NOT NULL,
  `is_finished` tinyint(1) NOT NULL COMMENT '是否结束',
  `is_blocking` tinyint(1) NOT NULL COMMENT '是否被阻断',
  `has_replay` tinyint(1) NOT NULL COMMENT '是否有回放',
  `has_command_replay` tinyint(1) NOT NULL COMMENT '是否有命令回放',
  `has_command` tinyint(1) NOT NULL COMMENT '是否有命令',
  `has_file` tinyint(1) NOT NULL COMMENT '是否有文件',
  `date_start` datetime NOT NULL COMMENT '开始时间',
  `date_end` datetime DEFAULT NULL COMMENT '结束时间',
  `date_last_active` datetime NOT NULL COMMENT '最后活动时间',
  `org_id` varchar(36) DEFAULT NULL,
  `protocol` varchar(8) NOT NULL COMMENT '协议',
  `terminal_id` varchar(36) DEFAULT NULL COMMENT '终端id',
  PRIMARY KEY (`id`),
  KEY `terminal_session_date_start_5c59d95b` (`date_start`) USING BTREE,
  KEY `terminal_session_terminal_id_5278f31c_fk_terminal_id` (`terminal_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_terminal_session
-- ----------------------------

-- ----------------------------
-- Table structure for `t_terminal_status`
-- ----------------------------
DROP TABLE IF EXISTS `t_terminal_status`;
CREATE TABLE `t_terminal_status` (
  `id` varchar(36) NOT NULL COMMENT '唯一标示',
  `session_online` int(11) NOT NULL COMMENT '在线的session数量',
  `cpu_used` double NOT NULL COMMENT 'cpu使用率',
  `memory_used` double NOT NULL COMMENT '内存使用率',
  `connections` int(11) NOT NULL COMMENT '连接数',
  `threads` int(11) NOT NULL COMMENT '线程数',
  `boot_time` double NOT NULL COMMENT '启动时间',
  `date_created` datetime NOT NULL COMMENT '数据提交时间',
  `terminal_id` varchar(36) DEFAULT NULL COMMENT '关联终端id',
  PRIMARY KEY (`id`),
  KEY `terminal_status_terminal_id_b57e6176_fk_terminal_id` (`terminal_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_terminal_status
-- ----------------------------

-- ----------------------------
-- Table structure for `t_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `guid` varchar(36) NOT NULL COMMENT '主键.唯一标示',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `department` int(11) DEFAULT NULL COMMENT '所属部门',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `authentication_method` int(11) NOT NULL COMMENT '认证方式：1为密码，2为密码+短信平台，3为密码+第三方USBKEY，4为密码+APP口令，5为密码+动态令牌，6为密码+OTP自写证书认证',
  `strategy` varchar(50) DEFAULT NULL COMMENT '用户策略guid',
  `visible` int(11) DEFAULT '0' COMMENT '是否可见',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(15) DEFAULT NULL COMMENT '电话号码',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `status` int(11) unsigned DEFAULT '1' COMMENT '用户状态可用：11为可用，10为锁定,12为停用',
  `skin` varchar(15) DEFAULT 'blue',
  `last_login_time` timestamp NULL DEFAULT NULL COMMENT '最后一次正常登录时间',
  `error_login_count` int(3) DEFAULT '0' COMMENT '错误登录次数',
  `error_login_last_time` timestamp NULL DEFAULT NULL COMMENT '最近错误登录时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者guid',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '修改者guid',
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表-定义用户基本信息';

-- ----------------------------
-- Records of t_user
-- ----------------------------

INSERT INTO `t_user` VALUES ('1', 'system', '$2a$10$NOybm9GHJdIRukROlEBjsOfgZun//.uPOKnyQ5XWej07H4/hhN9kq', '1', '超级管理员', '1', null, '0', '', '', '', '11', 'blue', '2019-01-08 16:42:39', '0', '2019-01-08 14:32:06', '2018-09-25 14:30:21', null, '2018-11-02 17:19:27', '1');
INSERT INTO `t_user` VALUES ('2', 'admin', '$2a$10$NOybm9GHJdIRukROlEBjsOfgZun//.uPOKnyQ5XWej07H4/hhN9kq', '1', '管理员', '1', null, '0', '', '', '', '11', 'blue', '2019-01-07 10:02:31', '0', '2018-09-25 14:30:21', '2018-09-25 14:30:21', null, null, null);
INSERT INTO `t_user` VALUES ('3', 'operator', '$2a$10$NOybm9GHJdIRukROlEBjsOfgZun//.uPOKnyQ5XWej07H4/hhN9kq', '1', '操作员', '1', null, '0', '', '', '', '11', 'blue', '2019-01-08 11:42:43', '0', '2018-09-25 14:30:22', '2018-09-25 14:30:22', null, null, null);
INSERT INTO `t_user` VALUES ('4', 'auditor', '$2a$10$NOybm9GHJdIRukROlEBjsOfgZun//.uPOKnyQ5XWej07H4/hhN9kq', '1', '审计员', '1', null, '0', '', '', '', '11', 'blue', '2019-01-05 17:30:22', '0', '2018-09-25 14:30:23', '2018-09-25 14:30:23', null, null, null);

-- ----------------------------
-- Table structure for `t_user_asset_assetgroup`
-- ----------------------------
DROP TABLE IF EXISTS `t_user_asset_assetgroup`;
CREATE TABLE `t_user_asset_assetgroup` (
  `user_guid` varchar(36) NOT NULL COMMENT '用户主键',
  `type` int(11) NOT NULL COMMENT '操作员授权类型（1设备，2设备组）',
  `asset_id` int(11) DEFAULT NULL COMMENT '设备id',
  `assetgroup_id` int(11) DEFAULT NULL COMMENT '设备组id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user_asset_assetgroup
-- ----------------------------

INSERT INTO `t_user_asset_assetgroup` VALUES ('3', '2', null, '1');

-- ----------------------------
-- Table structure for `t_user_department`
-- ----------------------------
DROP TABLE IF EXISTS `t_user_department`;
CREATE TABLE `t_user_department` (
  `user_guid` varchar(36) COLLATE utf8_bin NOT NULL COMMENT '用户guid',
  `department_id` int(10) unsigned NOT NULL COMMENT '部门Id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='用户部门关联';

-- ----------------------------
-- Records of t_user_department
-- ----------------------------

-- ----------------------------
-- Table structure for `t_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role` (
  `user_guid` varchar(36) NOT NULL COMMENT '用户guid',
  `role_guid` varchar(36) NOT NULL COMMENT '角色guid',
  PRIMARY KEY (`user_guid`,`role_guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户角色关联表';

-- ----------------------------
-- Records of t_user_role
-- ----------------------------
INSERT INTO `t_user_role` VALUES ('1', '1');
INSERT INTO `t_user_role` VALUES ('1', '2');
INSERT INTO `t_user_role` VALUES ('1', '3');
INSERT INTO `t_user_role` VALUES ('2', '1');
INSERT INTO `t_user_role` VALUES ('3', '2');
INSERT INTO `t_user_role` VALUES ('4', '3');

-- ----------------------------
-- Table structure for `t_user_usergroup`
-- ----------------------------
DROP TABLE IF EXISTS `t_user_usergroup`;
CREATE TABLE `t_user_usergroup` (
  `user_guid` varchar(36) NOT NULL COMMENT '用户guid',
  `usergroup_id` int(11) NOT NULL COMMENT '用户组id',
  PRIMARY KEY (`user_guid`,`usergroup_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户和用户组关联表';

-- ----------------------------
-- Records of t_user_usergroup
-- ----------------------------


-- ----------------------------
-- Table structure for `t_usergroup`
-- ----------------------------
DROP TABLE IF EXISTS `t_usergroup`;
CREATE TABLE `t_usergroup` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键.唯一标示',
  `name` varchar(50) NOT NULL COMMENT '用户组名称',
  `pid` int(11) DEFAULT NULL COMMENT '所属用户组id',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `tree_path` varchar(255) DEFAULT NULL COMMENT '路径',
  `level` int(11) DEFAULT NULL COMMENT '节点层级,必须从0开始',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者guid',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8 COMMENT='用户组表';

-- ----------------------------
-- Records of t_usergroup
-- ----------------------------
INSERT INTO `t_usergroup` VALUES ('1', '产品总线', null, '顶级用户组', ',', '0', '1', '2018-10-23 11:07:46', '');

-- ----------------------------
-- Table structure for t_task_asset
-- ----------------------------
DROP TABLE IF EXISTS `t_task_asset`;
CREATE TABLE `t_task_asset` (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `once` tinyint(1) DEFAULT '0' COMMENT '是否只执行一次',
  `cron` varchar(255) NOT NULL COMMENT 'cron表达式',
  `type` enum('SAME','RANDOM','FIXED') NOT NULL DEFAULT 'RANDOM' COMMENT '密码类型',
  `password` varchar(255) DEFAULT NULL COMMENT '用户选择固定密码时,指定的密码值',
  `rule` int(3) DEFAULT NULL COMMENT '密码规则(int值1:数字,2:大写字母,4:小写字母,8:特殊符号;使用位运算匹配)',
  `length` int(11) DEFAULT NULL COMMENT '密码长度',
  `cycle` enum('week','month','none') DEFAULT 'none',
  `day` varchar(255) DEFAULT NULL COMMENT '如果按周循环,值为1-7;如果按月循环,值为1-28',
  `time` varchar(255) DEFAULT NULL COMMENT '计划执行的之间点,如01:35',
  `ftp` tinyint(1) DEFAULT NULL COMMENT '是否保存ftp服务器',
  `ftp_addr` varchar(255) DEFAULT NULL COMMENT 'FTP服务器地址',
  `ftp_dir` varchar(255) DEFAULT NULL COMMENT 'FTP路径',
  `ftp_account` varchar(255) DEFAULT NULL COMMENT 'FTP账号',
  `ftp_pwd` varchar(255) DEFAULT NULL COMMENT 'FTP密码',
  `email` tinyint(1) DEFAULT NULL COMMENT '是否发送邮件',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时改密计划';

-- ----------------------------
-- Table structure for t_task_asset_asset
-- ----------------------------
DROP TABLE IF EXISTS `t_task_asset_asset`;
CREATE TABLE `t_task_asset_asset` (
  `task_id` varchar(36) NOT NULL,
  `asset_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task_asset_email
-- ----------------------------
DROP TABLE IF EXISTS `t_task_asset_email`;
CREATE TABLE `t_task_asset_email` (
  `task_id` varchar(36) NOT NULL,
  `email` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_ukey
-- ----------------------------
DROP TABLE IF EXISTS `t_ukey`;
CREATE TABLE `t_ukey` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT 'USBKey名称',
  `sign` varchar(255) NOT NULL COMMENT 'USBKey标识',
  `user_guid` varchar(36) DEFAULT NULL COMMENT '绑定用户guid',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='USBKey';