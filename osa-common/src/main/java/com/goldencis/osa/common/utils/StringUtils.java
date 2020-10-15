package com.goldencis.osa.common.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 
 *  
 * String工具类. <br> 
 *  

 */
public class StringUtils {

    /** 
     * 功能：检查这个字符串是不是空字符串。<br/> 
     * 如果这个字符串为null或者trim后为空字符串则返回true，否则返回false。 
     *  
     * @param chkStr 
     *            被检查的字符串 
     * @return boolean 
     */
    public static boolean isEmpty(String chkStr) {
        if (chkStr == null) {
            return true;
        } else {
            return "".equals(chkStr.trim()) ? true : false;
        }
    }

    /** 
     * 如果字符串没有超过最长显示长度返回原字符串，否则从开头截取指定长度并加...返回。 
     *  
     * @param str 
     *            原字符串 
     * @param length 
     *            字符串最长显示的长度 
     * @return 转换后的字符串 
     */
    public static String trimString(String str, int length) {
        if (str == null) {
            return "";
        } else if (str.length() > length) {
            return str.substring(0, length - 3) + "...";
        } else {
            return str;
        }
    }

    /**
     * 根据绝对路径返回最后文件名称
     * @author mll
     * @param path
     * @return String
     */
    public static String subLastPath(String path) {
        int index = path.lastIndexOf("\\");
        char[] ch = path.toCharArray();
        String lastString = String.copyValueOf(ch, index + 1, ch.length - index - 1);
        return lastString;
    }

    /**
     * 将数组转化成字符
     * @param list
     * @param separator
     * @return
     */
    public static String listToString(List<String> list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public static boolean compareStringList(String nstr, String ostr, String regex) {
        if (nstr != null && ostr != null) {
            if (nstr.length() != ostr.length()) {
                return false;
            }
            String[] nmap = nstr.split(regex);
            String[] omap = nstr.split(regex);
            if (nmap.length != omap.length) {
                return false;
            }
            Set<String> set = new HashSet<String>();
            set.addAll(Arrays.asList(nmap));
            set.addAll(Arrays.asList(omap));
            if (nmap.length != set.size()) {
                return false;
            }
            return true;
        } else if (nstr == null && ostr == null) {
            return true;
        }
        nstr = (nstr == null ? "" : nstr);
        ostr = (ostr == null ? "" : ostr);
        if ("".equals(nstr) && "".equals(ostr)) {
            return true;
        }

        return false;
    }

    public static String dealEmptyStr (Object object) {
        return object == null ? "" : object.toString();
    }
    public static String dealEmptyNull (Object object) {
        return object == null ? null : object.toString();
    }

    /**
     * 判断是否是在长度限制内
     * @param str 字符串
     * @param length 字符长度限制 (包含 限制長度)
     * @return true 在长度限制内； false 不在限制内
     */
    public static boolean isInLength(String str, int length){
        if (StringUtils.isEmpty(str)){
            return true;
        }
        if (str.length()> length){
            return false;
        }
        return true;
    }

}