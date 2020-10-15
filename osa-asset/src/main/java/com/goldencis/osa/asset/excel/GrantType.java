package com.goldencis.osa.asset.excel;

import java.util.Objects;

/**
 * 授权类型
 */
public enum GrantType {
    /**
     * 没有授权
     */
    NONE(0),
    /**
     * 授权到设备组
     */
    ASSETGROUP(1),
    /**
     * 授权到设备
     */
    ASSET(2),
    /**
     * 授权到设备账号
     */
    ACCOUNT(3);

    private int value;

    GrantType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GrantType matchValue(int value) {
        if (Objects.isNull(value)) {
            return null;
        }
        for (GrantType type : GrantType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
