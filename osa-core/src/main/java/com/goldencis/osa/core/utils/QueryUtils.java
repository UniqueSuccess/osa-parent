package com.goldencis.osa.core.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goldencis.osa.common.entity.Pagination;
import com.goldencis.osa.common.utils.ListUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by limingchao on 2018/10/9.
 */
public class QueryUtils {

    /**
     * 设置分页参数
     * @param params 请求中的参数Map
     * @param <T> 包装类的泛型
     * @return 分页对象
     */
    public static <T> IPage<T> paresParams2Page(Map<String, String> params) {
        //获取分页参数的起始位置
        Integer start = StringUtils.isEmpty(params.get("start")) ? 0 : Integer.valueOf(params.get("start"));
        //获取分页参数的每页条数
        Integer length = StringUtils.isEmpty(params.get("length")) ? 10 : Integer.valueOf(params.get("length"));
        //获取分页页码
        Integer pageNo = (start / length) + 1;

        return new Page(pageNo, length);
    }

    /**
     * 设置分页参数
     * @param params 请求中的参数Map
     * @param <T> 包装类的泛型
     * @return 分页对象
     */
    public static <T> IPage<T> paresParams2Page(Pagination params) {
        //获取分页参数的起始位置
        Integer start = params.getStart() == null ? 0 : params.getStart();
        //获取分页参数的每页条数
        int length = params.getLength() == null ? 10 : params.getLength();
        //获取分页页码
        int pageNo = (start / length) + 1;

        if (StringUtils.isEmpty(params.getSearchStr())) {
            params.setSearchStr(null);
        }
        return new Page(pageNo, length);
    }

    /**
     * 设置模糊查询条件(仅适合查询条件都为模糊查询的情况，不能再添加其他查询条件)
     * @param wrapper 查询的包装类
     * @param params 请求中的参数Map
     * @param columns 需要匹配查询条件的字段
     * @param <T> 包装类的泛型
     * @return 查询的包装类
     */
    public static <T> QueryWrapper<T> setQeryConditionByParamsMap(QueryWrapper<T> wrapper, Map<String, String> params, String... columns) {
        if (params.containsKey("searchStr") && !StringUtils.isEmpty(params.get("searchStr"))) {
            String searchStr = params.get("searchStr");
            //遍历字段，为其设置查询条件，模糊查询
            for (String column : columns) {
                wrapper.or().like(column, searchStr);
            }
        }

        return wrapper;
    }

    /**
     * 设置查询时间
     * @param wrapper 查询的包装类
     * @param params 请求中的参数Map
     * @param column 需要匹配时间条件的字段
     * @param <T> 包装类的泛型
     * @return 查询的包装类
     */
    public static <T> QueryWrapper<T> setQeryTimeByParamsMap(QueryWrapper<T> wrapper, Map<String, String> params, String column) {
        String startTime;
        String endTime;
        //分为三种情况，开始时间和结束时间都有，或者只有其中一个。
        if (params.containsKey("startTime") && params.containsKey("endTime")) {
            startTime = params.get("startTime");
            endTime = params.get("startTime");
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                wrapper.between(column, startTime, endTime);
            }
        } else if (params.containsKey("startTime")) {
            startTime = params.get("startTime");
            if (!StringUtils.isEmpty(startTime)) {
                wrapper.ge(column, startTime);
            }
        } else if (params.containsKey("endTime")) {
            startTime = params.get("startTime");
            endTime = params.get("startTime");
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                wrapper.le(column, endTime);
            }
        }

        return wrapper;
    }

    /**
     * 设置排序条件
     * @param wrapper 查询的包装类
     * @param params 请求中的参数Map
     * @param defaultOrderType 默认的排序类型
     * @param defaultOrderColumn 默认的排序字段
     * @param <T> 包装类的泛型
     * @return 查询的包装类
     */
    public static <T> QueryWrapper<T> setQeryOrderByParamsMap(QueryWrapper<T> wrapper, Map<String, String> params, String defaultOrderType, String defaultOrderColumn) {
        //增加默认排序
        if (!params.containsKey("orderType") && !StringUtils.isEmpty(defaultOrderType)) {
            params.put("orderType", defaultOrderType);
        }
        if (!params.containsKey("orderColumn") && !StringUtils.isEmpty(defaultOrderColumn)) {
            params.put("orderColumn", defaultOrderColumn);
        }

        if (params.containsKey("orderType") && params.containsKey("orderColumn")) {
            String orderType = params.get("orderType");
            String orderColumn = params.get("orderColumn");
            if (!StringUtils.isEmpty(orderType) && !StringUtils.isEmpty(orderColumn)) {
                boolean isAsc = "desc".equals(orderType) ? false : true;
                wrapper.orderBy(true, isAsc, orderColumn);
            }
        }

        return wrapper;
    }

    /**
     * 设置排序条件
     * @param wrapper 查询的包装类
     * @param params 请求中的参数Map
     * @param <T> 包装类的泛型
     * @return 查询的包装类
     */
    public static <T> QueryWrapper<T> setQeryOrderByParamsMap(QueryWrapper<T> wrapper, Map<String, String> params) {
        return setQeryOrderByParamsMap(wrapper, params, null, null);
    }

    /**
     * 为模糊查询的添加增加%%符号
     * @param params 查询map
     */
    public static void addFuzzyQuerySymbols(Map<String, String> params) {
        addFuzzyQuerySymbols(params, "searchStr");
    }

    /**
     * 为模糊查询的添加增加%%符号
     * @param params 查询map
     */
    public static void addFuzzyQuerySymbols(Pagination params) {
        String searchStr = params.getSearchStr();
        if (StringUtils.isEmpty(searchStr)) {
            return;
        }
        params.setSearchStr("%" + searchStr + "%");
    }

    /**
     * 为模糊查询的添加增加%%符号
     * @param params 查询map
     * @param paramName 参数名称
     */
    public static void addFuzzyQuerySymbols(Map<String, String> params, String paramName) {
        if (params.get(paramName) == null || "".equals(params.get(paramName))) {
            params.remove(paramName);
            return;
        }
        params.put(paramName, "%" + params.get(paramName) + "%");
    }

    /**
     * 擦除泛型，同时将分页参数转化为Integer类型
     * @param params 参数map
     * @return 擦除泛型后的参数map
     */
    public static Map<String, Object> formatPageParams(Map params) {
        Map map = params;
        //获取分页参数的起始位置
        Integer start = StringUtils.isEmpty(params.get("start")) ? 0 : Integer.valueOf((String)params.get("start"));
        map.put("start", start);

        //获取分页参数的每页条数
        Integer length = StringUtils.isEmpty(params.get("length")) ? 10 : Integer.valueOf((String)params.get("length"));
        map.put("length", length);

        return map;
    }

    /**
     * 将指定参数名的参数从map中取出，split后，转化成String的List，添加到map中，key为参数集合名
     * @param paramMap 参数map
     * @param paramName 指定参数名
     * @param paramListName 参数集合名
     */
    public static void addQueryConditionBySplitString4Str(Map<String, Object> paramMap, String paramName, String paramListName, String separator) {
        if (!StringUtils.isEmpty(paramMap.get(paramName))) {
            String conditionStr = (String) paramMap.get(paramName);
            String[] split = conditionStr.split(StringUtils.isEmpty(separator) ? ";" : separator);
            List<String> paramList = new ArrayList<>();
            for (String condition : split) {
                paramList.add(condition);
            }

            if (!ListUtils.isEmpty(paramList)) {
                paramMap.put(paramListName, paramList);
            }
        }
    }
}
