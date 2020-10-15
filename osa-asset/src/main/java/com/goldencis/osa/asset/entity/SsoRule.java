package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * SSO(单点登录)规则
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sso_rule")
public class SsoRule extends Model<SsoRule> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 设备id
     */
    private Integer assetId;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 工具类型
     */
    private Integer toolType;

    /**
     * 规则(规则字符串)
     */
    private String rule;

    /**
     * 设备类型名称
     */
    @TableField(exist = false)
    private String toolTypeName;
    /**
     * 属性集合
     */
    @TableField(exist = false)
    private List<SsoRuleAttr> ruleAttrs;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
