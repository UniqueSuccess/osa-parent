package com.goldencis.osa.asset.sso.param.impl;

import com.goldencis.osa.asset.sso.param.ISsoAttrParam;
import com.goldencis.osa.common.utils.StringUtils;
import lombok.Data;

import java.util.Objects;

/**
 * 检测窗口时,需要的参数
 * 格式为:class:TNavicatMainForm,name:Navicat Premium
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 14:07
 **/
@Data
public class WindowParam implements ISsoAttrParam {

    public static final String PREFIX_CLAZZ = "class:";
    public static final String PREFIX_NAME = "name:";

    /**
     * 窗口类名
     */
    private String clazz;
    /**
     * 窗口名称
     */
    private String name;

    @Override
    public void from(String param) throws Exception {
        if (StringUtils.isEmpty(param)) {
            throw new IllegalArgumentException(String.format("参数格式不正确,不能为null, %s", param));
        }
        String[] split = param.split(",");
        String clazz = null;
        String name = null;
        for (String s : split) {
            if (s.startsWith(WindowParam.PREFIX_CLAZZ)) {
                clazz = s.substring(WindowParam.PREFIX_CLAZZ.length());
            } else if (s.startsWith(WindowParam.PREFIX_NAME)) {
                name = s.substring(WindowParam.PREFIX_NAME.length());
            }
        }
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException(String.format("参数格式不正确, %s", param));
        }
        this.clazz = clazz;
        this.name = name;
    }
}
