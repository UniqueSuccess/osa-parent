package com.goldencis.osa.common.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
/**
 * @author shigd
 */
@Component
public class ContextUtil implements ApplicationContextAware{

    private static ApplicationContext context = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static Object getBean(String beanId) {
        Object bean = context.getBean(beanId);
        if (bean == null) {
            return null;
        }
        return bean;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext ctx) {
        context = ctx;
    }
}
