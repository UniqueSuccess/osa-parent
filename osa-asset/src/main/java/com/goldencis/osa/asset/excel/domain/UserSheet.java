package com.goldencis.osa.asset.excel.domain;

/**
 * 用户模块,导入导出的sheet页
 */
public enum UserSheet {
    USER("员工"),
    USERGROUP("部门");

    private String tag;

    UserSheet(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }
}
