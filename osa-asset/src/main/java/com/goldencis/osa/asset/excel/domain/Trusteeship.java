package com.goldencis.osa.asset.excel.domain;

import java.util.Objects;

/**
 * 是否托管
 */
public enum Trusteeship {

    YES("是", 1),
    NO("否", 0);

    private String content;
    private int intValue;

    Trusteeship(String content, int intValue) {
        this.content = content;
        this.intValue = intValue;
    }

    public String getContent() {
        return content;
    }

    public int getIntValue() {
        return intValue;
    }

    public static Trusteeship matchByValue(Integer intValue) {
        if (Objects.isNull(intValue)) {
            return NO;
        }
        if (intValue.equals(YES.intValue)) {
            return YES;
        }
        return NO;
    }

    public static Trusteeship matchByContent(String content) {
        for (Trusteeship value : Trusteeship.values()) {
            if (value.content.equals(content)) {
                return value;
            }
        }
        return null;
    }
}
