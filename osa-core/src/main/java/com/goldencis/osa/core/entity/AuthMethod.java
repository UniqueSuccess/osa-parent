package com.goldencis.osa.core.entity;

import java.util.Objects;

/**
 * 认证方式
 */
public enum AuthMethod {
    /**
     * 密码
     */
    PWD(1),
    /**
     * 密码+短信平台
     */
    SMS(2),
    /**
     * 密码+第三方USBKEY
     */
    USB(3),
    /**
     * 密码+APP口令
     */
    APP(4),
    /**
     * 密码+动态令牌
     */
    TOKEN(5),
    /**
     * 密码+OTP自写证书认证
     */
    OTP(6);

    private Integer code;

    AuthMethod(Integer code) {
        this.code = code;
    }

    public Integer code() {
        return this.code;
    }

    public static AuthMethod getByCode(Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }
        for (AuthMethod value : AuthMethod.values()) {
            if (code.equals(value.code())) {
                return value;
            }
        }
        return null;
    }
}
