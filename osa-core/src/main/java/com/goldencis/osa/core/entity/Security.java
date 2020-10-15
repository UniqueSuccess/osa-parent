package com.goldencis.osa.core.entity;

import lombok.Data;

/**
 * 安全(密码限制)
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-27 17:46
 **/
@Data
public class Security {
    /**
     * 数字
     */
    private Boolean number;
    /**
     * 大写字母
     */
    private Boolean capital;
    /**
     * 小写字母
     */
    private Boolean lowercase;
    /**
     * 最小长度
     */
    private Integer minLength;
    /**
     * 尝试次数
     */
    private Integer tryCount;
    /**
     * 锁定时长(分钟)
     */
    private Integer lockDuration;

}
