package com.goldencis.osa.common.export.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 导出Excel表格时,被该注解标记的字段会被导出
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Export {

    /**
     * 列排序(0开始)
     * @return
     */
    int order();

    /**
     * 标题头
     * @return
     */
    String header();
}
