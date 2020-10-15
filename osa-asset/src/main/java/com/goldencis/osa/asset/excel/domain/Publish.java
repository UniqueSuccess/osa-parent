package com.goldencis.osa.asset.excel.domain;

import java.util.Objects;

/**
 * 是否为应用程序发布器
 */
public enum Publish {

    YES("是", 1),
    NO("否", 0);

    private String content;
    private int intValue;

    Publish(String content, int intValue) {
        this.content = content;
        this.intValue = intValue;
    }

    public String getContent() {
        return content;
    }

    public int getIntValue() {
        return intValue;
    }

    public static Publish matchByValue(Integer intValue) {
        if (Objects.isNull(intValue)) {
            return null;
        }
        if (intValue.equals(YES.intValue)) {
            return YES;
        }
        return NO;
    }

    public static Publish matchByContent(String content) {
        for (Publish value : Publish.values()) {
            if (value.content.equals(content)) {
                return value;
            }
        }
        return NO;
    }
}
