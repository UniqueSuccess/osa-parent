package com.goldencis.osa.core.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 系统日志类型
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LogSystemType extends Model<LogSystemType> {

    private static final long serialVersionUID = 1L;

    /**
     * key
     */
    private Integer value;

    /**
     * 内容
     */
    private String name;

    public LogSystemType() {
    }

    public LogSystemType(String name, Integer value) {
        this.value = value;
        this.name = name;
    }

    @Override
    protected Serializable pkVal() {
        return this.value;
    }
}
