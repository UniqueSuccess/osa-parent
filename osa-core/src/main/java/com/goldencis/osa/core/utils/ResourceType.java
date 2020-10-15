package com.goldencis.osa.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by limingchao on 2018/10/12.
 */
public enum ResourceType {

    NAVIGATION("Navigation", 1), OPERATION("Operation", 2);

    private String name;

    private Integer value;

    ResourceType(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    public static List<Integer> getResourceTypeValues() {
        return Arrays.asList(NAVIGATION.getValue(), OPERATION.getValue());
    }

    public static List<ResourceType> getResourceTypeList() {
        return Arrays.asList(NAVIGATION, OPERATION);
    }
}
