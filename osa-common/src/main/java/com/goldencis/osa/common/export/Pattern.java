package com.goldencis.osa.common.export;

/**
 * 日期转换格式
 */
public enum Pattern {

    PATTERN_1("yyyyMMddHHmmss"),
    PATTERN_2("yyyy-MM-dd HH:mm:ss");

    private String value;

    Pattern(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
