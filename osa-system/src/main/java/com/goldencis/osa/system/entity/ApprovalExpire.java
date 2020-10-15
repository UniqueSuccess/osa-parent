package com.goldencis.osa.system.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.goldencis.osa.core.entity.Dictionary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 审批过期配置
 * </p>
 *
 * @author limingchao
 * @since 2018-12-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ApprovalExpire extends Model<ApprovalExpire> {

    private static final long serialVersionUID = 1L;

    /**
     * 审批时限
     */
    private List<Dictionary> approvalExpireTime;

    /**
     * 自动审批结果
     */
    private List<Dictionary> approvalExpireResult;

    public ApprovalExpire() {
    }

    @Override
    protected Serializable pkVal() {
        return 1;
    }

}
