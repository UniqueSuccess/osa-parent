package com.goldencis.osa.core.entity;

import com.goldencis.osa.common.constants.ConstantsDto;

/**
 * 用户状态
 */
public enum UserStatus {
    /**
     * 启用
     */
    ENABLE(ConstantsDto.ACCOUNT_STATUS_ENABLE),
    /**
     * 锁定
     */
    LOCK(ConstantsDto.ACCOUNT_STATUS_LOCK),
    /**
     * 停用
     */
    DISABLE(ConstantsDto.ACCOUNT_STATUS_DISABLE);

    private Integer code;

    UserStatus(Integer code) {
        this.code = code;
    }

    public Integer code() {
        return this.code;
    }
}
