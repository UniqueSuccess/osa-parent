package com.goldencis.osa.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.export.annotation.Export;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户信息表-定义用户基本信息
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user")
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键.唯一标示
     */
    @TableId(value = "guid", type = IdType.UUID)
    private String guid;

    /**
     * 用户名
     */
    @Export(order = 1, header = "用户名")
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否采用默认密码
     */
    @TableField(exist = false)
    private String defaultPassword;

    /**
     * 所属部门
     */
    @TableField(exist = false)
    private Department department;

    @TableField(exist = false)
    private String departmentName;

    /**
     * 姓名
     */
    @Export(order = 2, header = "姓名")
    private String name;

    /**
     * @see AuthMethod
     */
    private Integer authenticationMethod;

    /**
     * 用户策略
     */
    private String strategy;

    /**
     * 是否可见
     */
    private Integer visible;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 地址
     */
    private String address;

    /**
     * 用户状态可用：11为可用，10为不可用(锁定)
     */
    private Integer status;

    /**
     * 外观样式
     */
    private String skin;

    /**
     * 最后一次正常登录时间
     */
    @Export(order = 5, header = "最近登录时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    /**
     * 错误登录次数
     */
    private Integer errorLoginCount;

    /**
     * 最近错误登录时间
     */
    @JsonIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime errorLoginLastTime;

    @JsonIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 创建者guid
     */
    private String createBy;

    @JsonIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建者guid
     */
    private String updateBy;

    /**
     * 用户权限集合
     */
    @TableField(exist = false)
    private Set<GrantedAuthority> authorities;

    /**
     * 角色集合
     */
    @TableField(exist = false)
    private List<Role> roles;

    /**
     * 角色集合
     */
    @TableField(exist = false)
    private String roleIds;

    /**
     * 角色类型
     */
    @TableField(exist = false)
    private String roleType;

    /**
     * 角色名称
     */
    @TableField(exist = false)
    private String roleName;

    /**
     * 用户组集合
     */
    @TableField(exist = false)
    private List<Usergroup> usergroups;

    /**
     * 用户组id集合
     */
    @TableField(exist = false)
    private String usergroupIds;

    @Export(order = 3, header = "用户组")
    @TableField(exist = false)
    private String usergroupsName;

    /**
     * 状态名称：11为可用，10为不可用(锁定)
     */
    @Export(order = 0, header = "状态")
    @TableField(exist = false)
    private String statusName;

    /**
     * 认证方式名称：1为密码，2为密码+APP口令，3为密码+动态令牌，4为密码+短信平台，5为密码+第三方USBKEY，6为密码+OTP自写证书认证
     */
    @Export(order = 4, header = "认证方式")
    @TableField(exist = false)
    private String authenticationMethodName;

    /**
     *回显用到，是否被选中（如操作员）
     */
    @TableField(exist = false)
    boolean checked = false;

    /**
     * 用户使用密码+UKey方式登录时,标记绑定ukey的id
     * @see AuthMethod
     */
    @TableField(exist = false)
    private String ukeyId;
    @TableField(exist = false)
    private String ukeyName;

    public String getDepartmentName() {
        if (department != null) {
            return department.getName();
        } else {
            return null;
        }
    }

    public String getDefaultPassword() {
        if (!StringUtils.isEmpty(this.defaultPassword)) {
            return this.defaultPassword;
        }

        if (ConstantsDto.DEFAULT_PWD.equals(password)) {
            return ConstantsDto.SWITCH_ON;
        } else {
            return ConstantsDto.SWITCH_OFF;
        }
    }

    @Override
    protected Serializable pkVal() {
        return this.guid;
    }
}
