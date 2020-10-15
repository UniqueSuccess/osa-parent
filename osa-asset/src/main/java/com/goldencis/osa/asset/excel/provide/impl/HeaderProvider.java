package com.goldencis.osa.asset.excel.provide.impl;

import com.goldencis.osa.asset.excel.header.IHeader;
import com.goldencis.osa.asset.excel.provide.IProvider;
import com.goldencis.osa.asset.excel.annotation.Export;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 通过注解来获取标题头
 * @see com.goldencis.osa.asset.excel.annotation.Export
 *
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-13 10:10
 **/
@Component
public class HeaderProvider implements IProvider<Class, List<IHeader>> {

    private final Logger logger = LoggerFactory.getLogger(HeaderProvider.class);

    @Override
    public List<IHeader> provide(Class clazz) throws Exception {
        if (Objects.isNull(clazz)) {
            logger.warn("clazz can not be null");
            return new ArrayList<>(0);
        }
        List<IHeader> list = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            Export export = field.getAnnotation(Export.class);
            if (Objects.isNull(export)) {
                continue;
            }
            list.add(new IHeader() {
                @Override
                public String content() {
                    return export.desc();
                }

                @Override
                public int order() {
                    return export.order();
                }

                @Override
                public int compareTo(IHeader o) {
                    return this.order() - o.order();
                }
            });
        }
        Collections.sort(list);
        return list;
    }
}
