package com.goldencis.osa.common.utils;

import java.util.List;

/**
 * Created by wangyi on 2018/9/26.
 */
public class ListUtils {
    public static boolean isEmpty(List list) {
        if (list != null && list.size() > 0) {
            return false;
        }
        return true;
    }
}
