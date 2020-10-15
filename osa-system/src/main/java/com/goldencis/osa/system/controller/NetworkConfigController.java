package com.goldencis.osa.system.controller;

import cn.neiwang.osajni.IFConfig;
import com.alibaba.fastjson.JSONArray;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.ListUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 系统-系统配置-网络配置制器
 * </p>
 *
 * @author limingchao
 * @since 2018-12-25
 */
@Api(tags = "系统-系统配置-网络配置")
@RestController
@RequestMapping("/systemSetting")
public class NetworkConfigController {

    private final Logger logger = LoggerFactory.getLogger(NetworkConfigController.class);

    @ApiOperation(value = "获取网络配置信息")
    @GetMapping(value = "/netconfig")
    public ResultMsg netconfig() {
        try {
            JSONObject obj = new JSONObject(IFConfig.getIFConfig());
            List<Map<String, Object>> datalist = new ArrayList<>();
            if (obj != null) {
                Iterator<String> it = obj.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    JSONObject ethObj = (JSONObject) obj.get(key);
                    if (ethObj.get("addr") != null) {
                        Map<String, Object> eth = new HashMap<>();
                        eth.put("name", key);
                        eth.put("addr", ethObj.get("addr"));
                        eth.put("gateway", ethObj.get("gateway"));
                        eth.put("mask", ethObj.get("mask"));
                        datalist.add(eth);
                    }
                }
            }
            Collections.reverse(datalist);
            return ResultMsg.ok(datalist);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "保存网络配置信息")
    @PostMapping(value = "/netconfig", produces = "application/json")
    public ResultMsg savenetconfig(String netConfigs) {
        try {
            JSONArray netConfigJson = JSONArray.parseArray(netConfigs);
            if (!ListUtils.isEmpty(netConfigJson)) {
                Map<String, Object> configMap = new HashMap<>();
                for (int i = 0; netConfigJson.size() > i; i++) {
                    com.alibaba.fastjson.JSONObject netconfig = netConfigJson.getJSONObject(i);
                    Map<String, String> map = new HashMap<>();
                    map.put("addr", netconfig.getString("addr"));
                    map.put("gateway", netconfig.getString("gateway"));
                    map.put("mask", netconfig.getString("mask"));
                    JSONObject json = new JSONObject(map);
                    configMap.put("eth" + i, json);
                }
                JSONObject json = new JSONObject(configMap);

                logger.info("netConfig : " + json.toString());
                /*"{\"eth0\":{\"addr\":\"192.168.3.90\",\"gateway\":\"192.168.3.1\",\"mask\":\"255.255.255.0\"}}"*/
                boolean flag = IFConfig.setIFConfig(json.toString());
                if (flag) {
                    return ResultMsg.ok("success");
                } else {
                    return ResultMsg.False("failed");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.False("failed");
        }
        return ResultMsg.ok("nodata");
    }
}
