package com.goldencis.osa.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Toolkit {

    private static final Log LOGGER = LogFactory.getLog(Toolkit.class);

    /** . "零","壹","贰","叁","肆","伍","陆","柒","捌","玖" */
    private static final String[] NUMS = new String[] { "\u96f6", "\u58f9", "\u8d30", "\u53c1", "\u8086", "\u4f0d",
            "\u9646", "\u67d2", "\u634c", "\u7396" };

    /** . "拾","佰","仟","万","亿" */
    private static final String[] UNITS = new String[] { "\u62fe", "\u4f70", "\u4edf", "\u4e07", "\u4ebf" };

    private static final NumberFormat NUMBERFORMAT = new DecimalFormat("#,####.##");

    public static String formatRMB(double number) {
        if (number > Double.MAX_VALUE || number < 0.0) {
            return null;
        }
        // 按个数转换,如"21,1234,4567,5487.4543544"
        String srcNum = NUMBERFORMAT.format(number);
        String prefixNum = srcNum;
        String lastNum = "";
        int index = srcNum.indexOf(".");
        if (index != -1) {
            // 小数点前
            prefixNum = srcNum.substring(0, index);
            lastNum = srcNum.substring(index + 1, srcNum.length());
        }
        // 用于保存结果
        StringBuffer result = new StringBuffer(0);
        // 4个数字一组
        String[] numPices = prefixNum.split(",");
        // 遍历每个组
        for (int i = 0; i < numPices.length; i++) {
            // 遍历组中的每个数字
            for (int j = 0; j < numPices[i].length(); j++) {
                int k = Integer.parseInt(String.valueOf(numPices[i].charAt(j)));
                int len = numPices[i].length();
                // 变成汉字
                result.append(NUMS[k]);
                // 添加仟佰拾
                result.append(len - 2 - j >= 0 && k > 0 ? UNITS[len - 2 - j] : "");
                // 添加亿万
                result.append(j != len - 1 ? "" : ((i + numPices.length) % 2 == 0 ? (i == numPices.length - 1 ? ""
                        : UNITS[3]) : i != numPices.length - 1 ? UNITS[4] : ""));
            }
        }

        String resutlStr = result.toString();
        // "零零" to  "零"
        resutlStr = resutlStr.replaceAll(NUMS[0] + "{2,}", NUMS[0]);
        // "零万" to "万"
        resutlStr = resutlStr.replaceAll(NUMS[0] + UNITS[3] + "{1}", UNITS[3]);
        // "零亿" to"亿"
        resutlStr = resutlStr.replaceAll(NUMS[0] + UNITS[4] + "{1}", UNITS[4]);
        // "亿万" to "亿零"
        resutlStr = resutlStr.replaceAll(UNITS[4] + UNITS[3] + "{1}", UNITS[4]);

        // 去掉最后的"零"
        if (resutlStr.lastIndexOf(NUMS[0]) == resutlStr.length() - 1) {
            resutlStr = resutlStr.substring(0, resutlStr.length() - 1);
        }
        String lastStr = "";
        if (lastNum.length() == 1) {
            int n = Integer.parseInt(lastNum);
            if (n != 0) {
                lastStr = NUMS[n] + "角";
            }
        } else if (lastNum.length() >= 2) {
            int n = Integer.parseInt(lastNum.substring(0, 2));
            if (n > 0) {
                int a = n / 10;
                if (a != 0) {
                    lastStr = NUMS[a] + "角";
                } else {
                    lastStr = "零";
                }
                a = n % 10;
                if (a != 0) {
                    lastStr += NUMS[a] + "分";
                }
            }
        }

        if (lastStr.length() > 0) {
            lastStr = "元" + lastStr;
        } else {
            lastStr = "元整";
        }

        return resutlStr + lastStr;
    }

    /**
     *
     * @param t
     * @return String
     */
    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    /**.
     * debug log
     *
     * @param o Object
     */
    public static void debug(Object o) {
        LOGGER.debug(o);
    }

    public static void debug(Object o, Throwable t) {
        LOGGER.debug(o, t);
    }

    /**.
     * info log 带说明的日志输出
     *
     * @param o Object
     * @param t Throwable
     */
    public static void info(Object o, Throwable t) {
        LOGGER.info(o, t);
    }

    public static void info(Throwable t) {
        LOGGER.info(getStackTrace(t));
    }

    public static void info(Object o) {
        LOGGER.info(o);
    }

    /**.
     * error log
     *
     * @param o object
     * @param t Throwable
     */
    public static void error(Object o, Throwable t) {
        LOGGER.error(o, t);
    }

    public static void error(Throwable t) {
        LOGGER.error(getStackTrace(t));
    }

    /**.
     * 生成流水号
     *
     * @param code
     *            编码 code+date+4位随机正整数
     * @return String
     */
    public static String getFlowNum(String code) {
        // 产生随机数
        Random t = new Random();
        Random rd = new Random(t.nextInt());
        // 取绝对值
        int num = Math.abs(rd.nextInt() % 10000);

        // 格式随机数
        DecimalFormat df = new DecimalFormat();
        String pattern = "0000";
        df.applyPattern(pattern);
        String s = df.format(num).toString();

        String date = DateUtil.getFormatDate("yyyyMMddHHmmssSSS");

        return code + date + s;
    }

    /**.
     * 订单请求交易流水号
     *
     * @return String
     */
    public static String getOrderReqTranSeq() {
        // 产生随机数
        Random t = new Random();
        Random rd = new Random(t.nextInt());
        // 取绝对值
        int num = Math.abs(rd.nextInt() % 100000);

        // 格式随机数
        DecimalFormat df = new DecimalFormat();
        String pattern = "00000";
        df.applyPattern(pattern);
        String s = df.format(num).toString();

        String date = DateUtil.getFormatDate("yyyyMMddHHmmss");
        return "AP" + date + s;
    }

    /**.
     * 生成全球唯一标识符
     *
     * @return String
     */
    public static String getUUID() {
        return java.util.UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    /**.
     * 格式数字
     *
     * @param num number
     * @param newScale
     *            小数点后位数
     * @return String
     */
    public static String formatNumber(double num, int newScale) {
        BigDecimal bd = new BigDecimal(String.valueOf(num));
        return bd.setScale(newScale, 1).toString();
    }

    /**.
     * 格式数字
     *
     * @param num
     *            被除数
     * @param divisor
     *            除数
     * @param newScale
     *            小数点后位数
     * @return String
     */
    public static String formatNumber(String num, int divisor, int newScale) {
        if (num == null || num.length() == 0) {
            return "";
        }
        BigDecimal bd = new BigDecimal(String.valueOf(Double.parseDouble(num) / divisor));
        return bd.setScale(newScale, 1).toString();
    }

    public static String formatAmount(double d) {
        NumberFormat numberFormat = new DecimalFormat("#,####.##");
        String str = numberFormat.format(d);
        return "￥" + str;
    }

    public static Date getDate(String date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = df.parse(date);
        } catch (ParseException e) {
            info(e);
        }
        return d;
    }

    // public static String getAttribute(String name){
    // WebContext ctx = WebContextFactory.get();
    // if(ctx==null)
    // return null;
    // HttpSession session =ctx.getSession();
    // if(session==null)
    // return null;
    // Object obj = session.getAttribute(name);
    // if(obj!=null)
    // return obj.toString();
    // return null;
    // }

    /**.
     * 判断字串是否空字串
     *
     * @param str 字串
     * @return boolean
     */
    public static boolean isEmptyStr(String str) {
        return null == str || "".equals(str.trim());
    }

    /**.
     * 生成随机码
     *
     * @param m 位数
     * @return String
     */
    public static String getRandomNuber(int m) {

        Random rd = new Random();

        String randomnumber = "";

        int number = 1;
        for (int i = 0; i < m - 1; i++) {
            number *= 10;
        }
        randomnumber = String.valueOf(rd.nextInt(number)) + number;

        return randomnumber.substring(0, m);

    }

    /**.
     * 生成服务流水
     *
     * @return String
     */
    public static String getServiceSerialNumber() {

        return "SP" + DateUtil.getFormatDate("yyyyMMddHHmmssSSS") + getRandomNuber(4);

    }

    /**.
     * 生成业务流水
     *
     * @param protalCode 门户编码
     * @return String
     */
    public static String getBusiSerialNumber(String protalCode) {
        return protalCode + DateUtil.getFormatDate("yyyyMMddHHmmssSSS") + getRandomNuber(4);
    }

    /**.
     * 生成操作流水
     *
     * @param protalCode 门户编码
     * @return String
     */
    public static String getOptSerNum(String protalCode) {

        return "OP" + protalCode + DateUtil.getFormatDate("yyyyMMddHHmmssSSS") + getRandomNuber(4);
    }

    /**.
     * 将空字串转化为 "";
     *
     * @param str 空字串
     * @return String
     */
    public static String dellNull(String str) {

        if (null != str) {
            return str;
        } else {
            return "";
        }

    }

    /**.
     * 判断字符串是否全为数字
     *
     * @param validString String
     * @return boolean
     */
    public static boolean isNumber(String validString) {

        byte[] tempbyte = validString.getBytes();
        for (int i = 0; i < validString.length(); i++) {
            if ((tempbyte[i] < 48) || (tempbyte[i] > 57)) {
                return false;
            }
        }
        return true;
    }

    /**.
     * 解析Clob类型数据
     *
     * @param clob Clob类型数据
     * @return String
     */
    public static String parseClob(Clob clob) {
        if (clob == null) {
            return null;
        }
        String temp = null;
        StringBuffer content = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(clob.getCharacterStream());
            while ((temp = reader.readLine()) != null) {
                content.append(temp);
            }
        } catch (SQLException e) {
            info(e);
        } catch (IOException e) {
            info(e);
        }
        return content.toString();
    }

    /**.
     * 获取F5上的IPaddress
     * @return String
     */
    public static String getIpAddress() {
        // String ip =
        // WebContextFactory.get().getHttpServletRequest().getHeader(
        // "X-Forwarded-For");
        // if (ip == null || ip.length() == 0||ip=="") {
        // ip = WebContextFactory.get().getHttpServletRequest().getHeader(
        // "WL-Proxy-Client-IP");
        // }
        // if (ip == null || ip.length() == 0||ip=="") {
        // ip = WebContextFactory.get().getHttpServletRequest()
        // .getRemoteAddr();
        // }
        // return ip;
        return "";
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "".equals(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "".equals(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**.
     * 判断字符串是否为null或者空
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**.
     * 字符串替换
     *
     *
     * @param text
     *            原始字符串
     *
     * @param repl
     *            想被替换的内容
     *
     * @param with
     *            替换后的内容
     * @return String
     */
    public static String replace(String text, String repl, String with) {
        if (text == null || repl == null || with == null || repl.length() == 0) {
            return text;
        }

        StringBuffer buf = new StringBuffer(text.length());
        int searchFrom = 0;
        while (true) {
            int foundAt = text.indexOf(repl, searchFrom);
            if (foundAt == -1) {
                break;
            }

            buf.append(text.substring(searchFrom, foundAt)).append(with);
            searchFrom = foundAt + repl.length();
        }
        buf.append(text.substring(searchFrom));

        return buf.toString();
    }

    //	/**.
    //	 * BASE64字符串解密
    //	 *
    //	 * @param str 字符串
    //	 * @return String
    //	 */
    //	public static String baseDecode(String str) {
    //
    //		String s = null;
    //
    //		if (null != str) {
    //
    //			try {
    //
    //				BASE64Decoder decoder = new BASE64Decoder();
    //
    //				s = new String(decoder.decodeBuffer(str));
    //
    //			} catch (Exception e) {
    //				Toolkit.debug(e);
    //			}
    //		}
    //		return s;
    //
    //	}

    public static String fillString(String src, String c, int len) {
        if (src == null) {
            src = "";
        }
        int size = len - src.length();
        for (int i = 0; i < size; i++) {
            src = c + src;
        }
        return src;
    }

    public static String format(double number) {
        DecimalFormat format = new DecimalFormat("####0.00");

        return format.format(number);
    }

    /**
    * 防止sql注入
    *
    * @param sql
    * @return
    */
    public static String transactSQLInjection(String sql) {
        return sql.replaceAll(".*([';]+|(--)+).*", " ");
    }

    /**
      *  生成短信流水
      * @param destTermid
      * @return
      * @see [类、类#方法、类#成员]
      */
    public static String GenerateMessageSeq(String destTermid) {
        Calendar CD = Calendar.getInstance();
        int MI = CD.get(Calendar.MILLISECOND);
        Date datetime = new Date();
        SimpleDateFormat simpledate = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = simpledate.format(datetime) + Integer.toString(MI);
        return destTermid + "" + time;
    }

    public static boolean checkMoney(String money) {
        int index = money.indexOf(".");
        if (index == -1) {
            return isNumber(money);
        } else if (index == 0 || index == money.length()) {
            return false;
        } else {
            return isNumber(money.substring(0, index)) && isNumber(money.substring(index + 1));
        }
    }

    /**.
     * 检验Email格式是否正确
     * @param email Email
     * @return boolean
     */
    public static boolean isEmailFormat(String email) {
        if ((email == null) || email.trim().equals("")) {
            return false;
        }

        String regex = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";

        return email.matches(regex);
    }

    public static boolean isWeakPassword(String password, String accNbr) {
        //不能是同一数字
        if (password.matches(password.charAt(0) + "{6}")) {
            return true;
        }
        boolean flag = true;
        //不能是递增数字
        for (int i = 0; i < password.length() - 1; i++) {
            if (password.charAt(i + 1) != password.charAt(i) + 1) {
                flag = false;
            }
        }
        if (flag) {
            return true;
        }
        flag = true;
        //不能是递减数字
        for (int i = 0; i < password.length() - 1; i++) {
            if (password.charAt(i + 1) != password.charAt(i) - 1) {
                flag = false;
            }
        }
        if (flag) {
            return true;
        }
        //不能为手机后六位
        if (password.equals(accNbr.substring(accNbr.length() - 6))) {
            return true;
        }
        return false;
    }

    /**
     * 分转元
     * @param amount
     */
    public static String fen2Yuan(String amount) {
        double am = Integer.parseInt(amount);
        double a = 100;
        NumberFormat nbf = NumberFormat.getInstance();
        nbf.setMinimumFractionDigits(2);
        String k_amount = nbf.format(am / a);
        return k_amount;
    }

    /**
     * 元转分
     */
    public static String yuan2Fen(String am) {
        double Amount = Double.parseDouble(am) * 100; //定单金额
        String amount = new DecimalFormat("0").format(Amount);
        return amount;
    }

    /**
     * 将字符串加密成32位16进制MD5字符串
     * @param text
     * @return
     * @throws Exception
     */
    public static String getMD5ofStr(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(text.getBytes("US-ASCII"));
        byte[] digest = md.digest();
        StringBuffer md5 = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            md5.append(Character.forDigit((digest[i] & 0xF0) >> 4, 16));
            md5.append(Character.forDigit((digest[i] & 0xF), 16));
        }
        String md5str = md5.toString();
        md5str = md5str.toUpperCase();
        return md5str;
    }

    /**
    * 操作序列号
    * @param protal_code
    * @return
    */
    public static String getOperationSerialNumber(String protal_code) {
        return (new StringBuilder("OP")).append(protal_code).append(DateUtil.getFormatDate("yyyyMMddHHmmssSSS"))
                .append(getRandomNuber(4)).toString();
    }

}
