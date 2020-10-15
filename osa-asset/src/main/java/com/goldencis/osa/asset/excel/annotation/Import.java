package com.goldencis.osa.asset.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 导入时,需要导入的字段注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Import {

    /**
     * 描述,对应表格的标题头
     * @return
     */
    String desc();

    /**
     * 排序,从0开始,对应表格中的列
     * @return
     */
    int order();

    /**
     * 是否允许值为null或空
     * @return
     */
    boolean nullable() default false;
}
