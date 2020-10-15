package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * sso(单点登录)规则属性
 * </p>
 *
 * @author limingchao
 * @since 2018-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sso_rule_attr")
public class SsoRuleAttr extends Model<SsoRuleAttr> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "value", type = IdType.AUTO)
    private Integer value;

    /**
     * 工具类型(目前无用)
     */
    private Integer type;

    /**
     * 界面上显示的名称
     */
    private String name;

    /**
     * 规则名
     */
    private String nickname;

    /**
     * 默认的属性值
     */
    private String defaultContent;

    /**
     * 属性值
     */
    @TableField(exist = false)
    private String content;

    /**
     * 排序
     */
    @TableField(exist = false)
    private int compositor;

    @Override
    protected Serializable pkVal() {
        return this.value;
    }

}
