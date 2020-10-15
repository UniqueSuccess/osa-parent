package com.goldencis.osa.strategy.util;

import com.goldencis.osa.common.utils.DateUtil;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

/**
 * 策略工具类
 * 【暂没用】
 */
public class StrategyUtils {
    /**
     * 根据登录命令判断是否登录限制
     * @param loginTimeZone 7*24  01组合字符串（1有权限 0无权限）
     */
    public static boolean isLoginTime(String loginTimeZone, String loginTime){
        if (StringUtils.isEmpty(loginTimeZone)){
            return true;
        }
        if (loginTimeZone.length() == 7*24){
           Integer weekDay = DateUtil.dayForWeek(loginTime);
           if (Objects.isNull(weekDay)){
               throw new IllegalArgumentException("星期参数错误");
           }
           Integer charIndex = charPointIndex(weekDay,loginTime);
           if (Objects.isNull(charIndex)||  charIndex >= 7*24){
               throw new IllegalArgumentException("登录时间节点参数错误");
           }
           if ("1".equals(String.valueOf(loginTimeZone.charAt(charIndex)))){
               return true;
           }
        }
        return false;
    }

    /**
     * 根据星期、时间，获取矩阵节点下标
     * @param weekDay 星期（周一...周日，1...7）
     * @param time 年月日时分秒（只取 小时）
     * @return 7*24矩阵 横向拼接成一个01字符串的下标
     */
    public static Integer charPointIndex(int weekDay, String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(simpleDateFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("时间参数不正确");
        }
        //24小时制 小时
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int charPoint = 0;
        charPoint = ( weekDay - 1) * 24 + hour -1 ;
        System.out.println("hour:"+ hour + "\n" + charPoint);
        return charPoint;
    }
}
