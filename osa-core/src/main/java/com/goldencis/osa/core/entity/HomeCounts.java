package com.goldencis.osa.core.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 系统日志
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HomeCounts extends Model<HomeCounts> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户总数
     */
    private Integer userNums;

    /**
     * 设备总数
     */
    private Integer assetNums;

    /**
     * 用户在线数
     */
    private Integer userOnlineNums;

    /**
     * 在线会话
     */
    private Integer sessionOnlineNums;

    /**
     * 已审批
     */
    private Integer approvedNums;

    /**
     * 待审批
     */
    private Integer unApprovedNums;

    @Override
    protected Serializable pkVal() {
        return 1;
    }

}
