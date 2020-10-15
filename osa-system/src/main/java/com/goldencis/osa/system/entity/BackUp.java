package com.goldencis.osa.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 
 * </p>
 *
 * @author shigd
 * @since 2018-12-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_back_up")
public class BackUp extends Model<BackUp> {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 备份名称
     */
    private String name;

    /**
     * 备份类型 1 实时 0 定时
     */
    private String type;

    /**
     * 备注
     */
    private String mark;

    /**
     * 备份文件路径
     */
    private String filePath;

    /**
     * 状态 0 备份中 1 已完成
     */
    private String status;

    /**
     * 创建时间
     */
    private String createTime;

    private String typeName;

    //自动备份信息
    private String cycle;
    private String day;
    private String time;

    private Integer length;
    private Integer start;
    private String searchConditon;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
