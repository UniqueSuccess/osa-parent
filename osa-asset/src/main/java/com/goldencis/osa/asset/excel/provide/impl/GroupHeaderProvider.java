package com.goldencis.osa.asset.excel.provide.impl;

import com.goldencis.osa.asset.excel.header.IHeader;
import com.goldencis.osa.asset.excel.header.impl.UserGroupExportHeader;
import com.goldencis.osa.asset.excel.provide.IProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-11 15:04
 **/
@Component
public class GroupHeaderProvider implements IProvider<Integer, List<IHeader>> {

    /**
     * 默认header
     */
    private static final IHeader DEFAULT_HEADER = new UserGroupExportHeader("根部门", 0);
    /**
     * 名称模板
     */
    private static final String TEMPLATE_NAME = "%s%s级子部门";
    private final Logger logger = LoggerFactory.getLogger(GroupHeaderProvider.class);

    private Map<Integer, String> map = new ConcurrentHashMap<>(18);

    @PostConstruct
    private void init() {
        map.put(1, "一");
        map.put(2, "二");
        map.put(3, "三");
        map.put(4, "四");
        map.put(5, "五");
        map.put(6, "六");
        map.put(7, "七");
        map.put(8, "八");
        map.put(9, "九");
        map.put(10, "十");
        map.put(20, "二十");
        map.put(30, "三十");
        map.put(40, "四十");
        map.put(50, "五十");
        map.put(60, "六十");
        map.put(70, "七十");
        map.put(80, "八十");
        map.put(90, "九十");
    }

    /**
     *
     * @param count 从0开始
     * @return
     * @throws Exception
     */
    @Override
    public List<IHeader> provide(Integer count) throws Exception {
        if (Objects.isNull(count)) {
            throw new Exception("count can not be null");
        }
        if (count < 1 || count > 99) {
            throw new Exception("count值不在正常范围内,count: " + count);
        }
        List<IHeader> list = new ArrayList<>(count);
        int max = 9;
        if (count < 10) {
            max = count;
        }
        // 10以下
        list.addAll(lessThanTen(max));
        // 10以及10以上
        list.addAll(moreThanTen(count));
        return list;
    }

    /**
     * 处理10以下的数据
     * @param max
     * @return
     */
    private List<IHeader> lessThanTen(int max) {
        if (max > 9) {
            max = 9;
        }
        List<IHeader> list = new ArrayList<>(max);
        list.add(DEFAULT_HEADER);
        for (int i = 1; i <= max; i++) {
            list.add(new UserGroupExportHeader(String.format(TEMPLATE_NAME, "", map.get(i)), i));
        }
        return list;
    }

    /**
     * 处理10以及10以上的数据
     * @param count
     * @return
     */
    private List<IHeader> moreThanTen(int count) {
        if (count < 10) {
            return new ArrayList<>(0);
        }
        List<IHeader> list = new ArrayList<>();
        for (int i = 10; i <= count; i++) {
            int tenths = i / 10 % 10 * 10;
            int j = i % 10;
            String s = map.get(j) == null ? "" : map.get(j);
            UserGroupExportHeader header = new UserGroupExportHeader(String.format(TEMPLATE_NAME, map.get(tenths), s), i);
            list.add(header);
        }
        return list;
    }
}
