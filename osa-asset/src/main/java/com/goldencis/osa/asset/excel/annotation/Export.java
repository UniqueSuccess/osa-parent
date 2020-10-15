package com.goldencis.osa.asset.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 导出时,需要导出的字段注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Export {

    /**
     * 描述,对应表格的标题头
     * @return
     */
    String desc() default "";

    /**
     * 排序,从0开始,对应表格中的列
     * @return
     */
    int order() default -1;

}
