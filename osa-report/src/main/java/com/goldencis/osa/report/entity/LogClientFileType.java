package com.goldencis.osa.report.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
public class LogClientFileType extends Model<LogClientFileType> {
    private static final long serialVersionUID = 1L;

    /**
     * 文件操作类型
     */
    private Integer optype;

    /**
     * 设备唯一标识
     */
    private String optName;

    /**
     * 计算机名
     */
    private Integer optNums;

    @Override
    protected Serializable pkVal() {
        return this.optype;
    }

    public LogClientFileType() {
    }

    public LogClientFileType(Integer optype, String optName, Integer optNums) {
        this.optype = optype;
        this.optName = optName;
        this.optNums = optNums;
    }
}
