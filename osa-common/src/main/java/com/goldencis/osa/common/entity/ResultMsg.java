package com.goldencis.osa.common.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Created by limingchao on 2018/9/26.
 */
public class ResultMsg {

    public static final Integer RESPONSE_TRUE = 0;

    public static final Integer RESPONSE_FALSE = 1;

    public static final Integer RESPONSE_ERROR = 500;

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 响应业务状态
    private Integer resultCode;

    // 响应消息
    private String resultMsg;

    // 响应中的数据
    private Object data;

    // 分页查询时的总记录数
    private Integer total;

    // 分页查询时的记录
    private Object rows;

    public ResultMsg() {

    }

    public ResultMsg(Integer resultCode, String resultMsg, Object data) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.data = data;
    }

    public ResultMsg(Object data) {
        this.resultCode = RESPONSE_TRUE;
        this.resultMsg = "success";
        this.data = data;
    }

    public ResultMsg(IPage page) {
        this.resultCode = RESPONSE_TRUE;
        this.total = Math.toIntExact(page.getTotal());
        this.rows = page.getRecords();
    }

    public ResultMsg(IPage page, Object data) {
        this.resultCode = RESPONSE_TRUE;
        this.total = Math.toIntExact(page.getTotal());
        this.rows = page.getRecords();
        this.data = data;
    }

    public static ResultMsg ok(Object data) {
        return new ResultMsg(data);
    }

    public static ResultMsg ok() {
        return new ResultMsg((Object) null);
    }

    public static ResultMsg False(String resultMsg) {
        return build(RESPONSE_FALSE, resultMsg);
    }

    public static ResultMsg error(String resultMsg) {
        return build(RESPONSE_ERROR, resultMsg);
    }

    public static ResultMsg build(Integer resultCode, String resultMsg, Object data) {
        return new ResultMsg(resultCode, resultMsg, data);
    }

    public static ResultMsg build(Integer resultCode, String resultMsg) {
        return new ResultMsg(resultCode, resultMsg, null);
    }

    public static ResultMsg page(IPage page) {
        return new ResultMsg(page);
    }

    public static ResultMsg page(IPage page,Object data) {
        return new ResultMsg(page,data);
    }

//    public Boolean isOK() {
//        return this.resultCode == 200;
//    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getRows() {
        return rows;
    }

    public void setRows(Object rows) {
        this.rows = rows;
    }


    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * 将json结果集转化为TaotaoResult对象
     * jsonNode 是解析树形结构的  类似于解析DOM
     *  {"age" : 29,
     *   "messages" : [ "msg 1", "msg 2", "msg 3" ],
     *	 "name" : "mkyong"
     *	 }
     * @param jsonData json数据
     * @param clazz TaotaoResult中的object类型
     * @return
     */
    public static ResultMsg formatToPojo(String jsonData, Class<?> clazz) {
        try {
            if (clazz == null) {
                return MAPPER.readValue(jsonData, ResultMsg.class);
            }
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (clazz != null) {
                if (data.isObject()) {
                    obj = MAPPER.readValue(data.traverse(), clazz);
                } else if (data.isTextual()) {
                    obj = MAPPER.readValue(data.asText(), clazz);
                }
            }
            return build(jsonNode.get("resultCode").intValue(), jsonNode.get("resultMsg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 没有object对象的转化
     *
     * @param json
     * @return
     */
    public static ResultMsg format(String json) {
        try {
            return MAPPER.readValue(json, ResultMsg.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Object是集合转化
     *
     * @param jsonData json数据
     * @param clazz 集合中的类型
     * @return
     */
    public static ResultMsg formatToList(String jsonData, Class<?> clazz) {
        try {
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (data.isArray() && data.size() > 0) {
                obj = MAPPER.readValue(data.traverse(),
                        MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            return build(jsonNode.get("resultCode").intValue(), jsonNode.get("resultMsg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T convertDataToPojo(Class<T> clazz) {
        try {
            T pojo = JSONObject.parseObject((String) this.getData(), clazz);
            return pojo;
        } catch (Exception e) {
            throw new RuntimeException("convert session json failed!");
        }
    }

    public static ObjectMapper getMAPPER() {
        return MAPPER;
    }
}
