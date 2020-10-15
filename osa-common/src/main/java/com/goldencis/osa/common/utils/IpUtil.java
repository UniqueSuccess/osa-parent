package com.goldencis.osa.common.utils;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ip校验工具类
 *
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-28 14:29
 **/
public class IpUtil {

    public static final String LOCALHOST_STR = "localhost";
    public static final String LOCALHOST_NUM = "127.0.0.1";

    // IP的正则
    private static Pattern pattern = Pattern
            .compile("(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\."
                    + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\."
                    + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\."
                    + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})");

    /**
     * 在添加至白名单时进行格式校验
     *
     * @param ip
     * @return
     */
    public static boolean validate(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }
        for (String s : ip.split("-"))
            if (!pattern.matcher(s).matches()) {
                return false;
            }
        return true;
    }

    /**
     * 判断
     *
     * @param ip
     * @param list
     * @return
     * @throws IllegalArgumentException
     */
    public static boolean isIpInAddressRange(String ip, List<String> list) throws IllegalArgumentException {
        if (CollectionUtils.isNotEmpty(list)) {
            long theIp = addressToLong(ip);
            for (String tmp : list) {
                String[] iprange = tmp.split("-");
                long start = addressToLong(iprange[0]);
                long end = addressToLong(iprange.length > 1 ? iprange[1] : iprange[0]);
                if (theIp >= start && theIp <= end) {
                    return true;
                }
            }
        }
        return false;
    }

    public static long addressToLong(String ipAddr) throws IllegalArgumentException {
        if (!validate(ipAddr)) {
            throw new IllegalArgumentException("Illegal ip address: " + ipAddr);
        }
        long result = 0;
        String[] tmp = ipAddr.split("\\.");
        for (String part : tmp)
            result = result << 8 | Integer.parseInt(part);
        return result;
    }

    /**
     * 从url中分析出hostIP<br/>
     * @param url
     * @return
     */
    public static String getIpFromUrl(String url) {
        // 1.判断是否为空
        if (url == null || url.trim().equals("")) {
            return "";
        }

        // 2. 如果是以localhost,那么替换成127.0.0.1
        if(url.startsWith("http://" + LOCALHOST_STR) ){
            url = url.replace("http://" + LOCALHOST_STR, "http://" + LOCALHOST_NUM) ;
        }

        String host = "";
        Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            host = matcher.group();
        }
        return host;
    }
}
