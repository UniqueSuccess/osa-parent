package com.goldencis.osa;

import cn.neiwang.osajni.IFConfig;
import com.goldencis.osa.common.entity.ResultMsg;
import org.json.JSONObject;
import org.junit.Test;

import java.util.*;

/**
 * Created by limingchao on 2019/1/2.
 */
public class NetworkTest {

    @Test
    public void ifconfigTest() {
        JSONObject obj = new JSONObject("{\"eth3\":{\"addr\":\"\",\"gateway\":\"\",\"mask\":\"\"},\"eth2\":{\"addr\":\"\",\"gateway\":\"\",\"mask\":\"\"},\"eth1\":{\"addr\":\"\",\"gateway\":\"\",\"mask\":\"\"},\"eth0\":{\"addr\":\"\",\"gateway\":\"10.10.16.1\",\"mask\":\"\"}}");
        System.out.println(obj);
        List<Map<String, Object>> datalist = new ArrayList<>();
        if (obj != null) {
            Iterator<String> it = obj.keys();
            while (it.hasNext()) {
                String key = it.next();
                JSONObject ethObj = (JSONObject) obj.get(key);
                if (ethObj.get("addr") != null && !"".equals(ethObj.get("addr").toString().trim())) {
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
        ResultMsg msg = ResultMsg.ok(datalist);

        System.out.println(msg);
    }
}
