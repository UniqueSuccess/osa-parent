package com.goldencis.osa.task.domain;

/**
 * 周
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-21 17:23
 **/
public enum Week {

    MON(1),
    TUE(2),
    WED(3),
    THU(4),
    FRI(5),
    SAT(6),
    SUN(7);

    private int num;

    Week(int num) {
        this.num = num;
    }

    public int getNum() {
        return this.num;
    }

    public static Week getByNum(int num) {
        for (Week value : Week.values()) {
            if (value.getNum() == num) {
                return value;
            }
        }
        return null;
    }
}
