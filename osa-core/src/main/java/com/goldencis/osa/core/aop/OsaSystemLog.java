package com.goldencis.osa.core.aop;

import com.goldencis.osa.common.utils.LogType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 系统日志的注解
 * Created by limingchao on 2018/12/3.
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Documented
public @interface OsaSystemLog {

    /**
     * 日志详情模板
     * 使用%s作为占位符
     * @return
     */
    String template() default "%s";
    /**
     * 取值表达式集合，多个取值可用逗号隔开，格式为{参数下标}.{属性名}
     * 例如在如下方法上
     * void listDeviceByUser(User user)
     * 使用0.name，则会反射调用user.getName()方法获取内容
     * 使用0，则会直接获取user内容
     * @return
     */
    String args() default "0";
    /**
     * 取值表达式集合，多个取值可用逗号隔开，格式为{Result的属性名}.{属性名}
     * 例如在如下方法上
     * User user = userService.deleteUserByGuid(guid);
     * return ResultMsg.ok(user);
     * 使用data.username，则会反射调用user.getName()方法获取内容
     * 使用0，则会直接获取user内容
     * @return
     */
    String ret() default "";
    /**
     * 模块名
     * @return
     */
    String module();
    /**
     * 操作类型
     * @return
     */
    LogType type() default LogType.OTHER;

}
