package com.goldencis.osa.system.access;

/**
 * 准入控制开关
 */
public enum Switch {

    /**
     * 开
     */
    ON(1),
    /**
     * 关
     */
    OFF(0);

    private int code;

    Switch(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }

}
