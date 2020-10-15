package com.goldencis.osa.system.access;

import cn.neiwang.osajni.IFConfig;
import com.goldencis.osa.common.config.Config;
import com.goldencis.osa.common.utils.IpUtil;
import com.goldencis.osa.system.domain.AccessControl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 准入控制管理
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-25 15:40
 **/
@Service
public class AccessControlManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Config config;

    public void turnOn() {
        exec(Path.Switch, "1");
    }

    public void turnOff() {
        exec(Path.Switch, "0");
    }

    /**
     * 设置业务服务器ip
     */
    public void local() {
        String ip = null;
        String ifConfig = IFConfig.getIFConfig();
        logger.debug("config: {}", ifConfig);
        JSONObject obj = new JSONObject(ifConfig);
        Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (!StringUtils.isEmpty(key) && key.equalsIgnoreCase(config.getEth())) {
                JSONObject ethObj = (JSONObject) obj.get(key);
                ip = (String) ethObj.get("addr");
            }
        }
        exec(Path.local, StringUtils.isEmpty(ip) ? "" : String.valueOf(IpUtil.addressToLong(ip)));
    }

    /**
     * 设置受控服务器IP列表
     * @param list
     */
    public void servers(List<AccessControl.IpBlack> list) {
        String ips;
        if (!CollectionUtils.isEmpty(list)) {
            ips = list.stream()
                    .filter(Objects::nonNull)
                    .map(AccessControl.IpBlack::getIp)
                    .filter(Objects::nonNull)
                    .map(ip -> {
                        if (ip.contains("-")) {
                            String[] split = ip.split("-");
                            long start = IpUtil.addressToLong(split[0]);
                            long end = IpUtil.addressToLong(split[1]);
                            return String.format("%d-%d", start, end);
                        }
                        return String.valueOf(IpUtil.addressToLong(ip));
                    })
                    .collect(Collectors.joining(","));
        } else {
            ips = "";
        }
        logger.debug("受控服务器IP列表: {}", ips);
        exec(Path.servers, ips);
    }

    /**
     * 设置受控端口列表
     * @param list
     */
    public void ports(List<AccessControl.PortBlack> list) {
        String ports;
        if (!CollectionUtils.isEmpty(list)) {
            ports = list.stream()
                    .filter(item -> {
                        if (Objects.isNull(item)) {
                            return false;
                        }
                        return !Objects.isNull(item.getStart()) && !Objects.isNull(item.getEnd());
                    }).map(item -> {
                        if (item.getStart().equals(item.getEnd())) {
                            return Integer.toString(item.getStart());
                        }
                        return String.format("%d-%d", item.getStart(), item.getEnd());
                    }).collect(Collectors.joining(","));
        } else {
            ports = "";
        }
        exec(Path.ports, ports);
    }


    private void exec(Path path, String args) {
        String command = String.format("echo %s > %s", args, path.path());
        ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", command);
        try {
            Process process = builder.start();
            String s = parse(process);
            logger.debug("执行完成: {}", s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取脚本返回值
     */
    private String parse(Process process) {
        if (process != null) {
            try (BufferedReader brStat = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                StringBuilder fileContent = new StringBuilder();
                while ((line = brStat.readLine()) != null) {
                    logger.debug("line: {}", line);
                    fileContent.append(line);
                }
                return fileContent.toString();
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
            process.destroy();
        } else {
            logger.debug("process == null");
        }
        return "";
    }

}
