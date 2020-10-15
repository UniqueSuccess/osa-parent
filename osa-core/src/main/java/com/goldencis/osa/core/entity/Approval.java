package com.goldencis.osa.core.entity;

import lombok.Data;

/**
 * 审批配置
 * @program: osa-parent
 * @author: wang tt
 * @create: 2019-1-2 14:35:13
 **/
@Data
public class Approval {
    /**
     * 审批时间限制
     */
    private Integer expireTime;
    /**
     * 审批结果
     */
    private Integer expireResult;
}
