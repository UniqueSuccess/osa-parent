package com.goldencis.osa.core.utils;

import com.alibaba.fastjson.JSONObject;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.constants.PathConfig;
import com.goldencis.osa.common.utils.DateUtil;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletContext;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthUtils {
    public static Map<String, Object> getAuthInfo(ServletContext servletContext) {
        Map<String, Object> authInfo = new HashMap<String, Object>();
        String deviceUnique = servletContext.getAttribute("deviceUnique") != null ? servletContext.getAttribute("deviceUnique").toString() : "";
        authInfo.put("deviceUnique", deviceUnique);
        // 读取授权文件
        String fileContent = "";
        File f = new File(PathConfig.HOMEPATH + "/" + ConstantsDto.AUTH_FILE_NAME);
        String authmsg = "";
        if (!(f.isFile() && f.exists())) {
            InputStreamReader read = null;
            BufferedReader reader = null;
            try {
                read = new InputStreamReader(new FileInputStream(f), "UTF-8");
                reader = new BufferedReader(read);
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent += line;
                }
                JSONObject jasonObject;
                String company = null;
                String beginDate = null;
                String endDate = null;
                String supportDate = null;
                Date nowDate = new Date();
                String authContent = ReadAuthUtil.readAuth(fileContent);
                if (authContent == null || "".equals(authContent)) {
                    authInfo.put("authmsg", "授权文件无效");
                    return authInfo;
                }
                jasonObject = JSONObject.parseObject(authContent);

                company = jasonObject.get("company").toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //时间要么没字段 要么肯定有值
                beginDate = !jasonObject.containsKey("startdate") ? "" : sdf.format(sdf.parse(jasonObject.get("startdate").toString()));
                endDate = !jasonObject.containsKey("enddate") ? ConstantsDto.LONG_TIME_LIMIT : sdf.format(sdf.parse(jasonObject.get("enddate").toString()));
                supportDate = !jasonObject.containsKey("maintaindate") ? ConstantsDto.LONG_TIME_LIMIT : sdf.format(sdf.parse(jasonObject.get("maintaindate").toString()));
                //获取项目标识
                String osaFlag = jasonObject.get("product").toString();

                authInfo.put("company", company);
                authInfo.put("beginEndDate", !ConstantsDto.LONG_TIME_LIMIT.equals(endDate) ? beginDate + " 到 " + endDate : endDate);
                authInfo.put("beginDate", beginDate);
                authInfo.put("endDate", endDate);
                authInfo.put("supportDate", supportDate);
                authInfo.put("deviceUnique", jasonObject.get("hwserial").toString());
                authInfo.put("authDeviceNum", jasonObject.get("endpoints").toString());

                if ("".equals(deviceUnique) || !deviceUnique.equals(jasonObject.get("hwserial").toString())) {
                    authmsg = "设备唯一标识码验证失败";
                } else if (!ConstantsDto.LONG_TIME_LIMIT.equals(endDate) && nowDate.after(sdf.parse(endDate))) {//判断结束时间
                    authmsg = "不在授权期限内";
                } else if (!ConstantsDto.PROCJECT_IDENTIFICATION.equalsIgnoreCase(osaFlag)) {
                    authmsg = "项目标识不正确";
                } else if (!"".equals(beginDate) && nowDate.before(sdf.parse(beginDate))) {//判断开始时间
                    authmsg = "不在授权期限内";
                }

                //提示信息
                if (!ConstantsDto.LONG_TIME_LIMIT.equals(supportDate) && nowDate.after(DateUtil.strToDate(DateUtil.getDateAdd(-1, supportDate, "yyyy-MM-dd"), "yyyy-MM-dd"))) {
                    authInfo.put("promptMsg", "维保期限即将到期，请续保。");
                }
                //判断是否维保期限超期
                if (!ConstantsDto.LONG_TIME_LIMIT.equals(supportDate) && nowDate.after(DateUtil.strToDate(DateUtil.getDateAddDay(1, supportDate, "yyyy-MM-dd"), "yyyy-MM-dd"))) {
                    authInfo.put("promptMsg", "维保期限已到期，请续保。");
                }

            } catch (Exception e) {
                authmsg = "授权文件无效";
            } finally {
                if (read != null) {
                    try {
                        read.close();
                    } catch (Exception e2) {
                    }
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e2) {
                    }
                }
            }
        } else {
            authmsg = "授权文件不存在";
        }
        authInfo.put("authmsg", authmsg);
        return authInfo;
    }

    public static Map<String, Object> checkFileIsValidate(ServletContext servletContext, InputStream inStream) {
        Map<String, Object> authInfo = new HashMap<String, Object>();
        String authmsg = "";

        String deviceUnique = servletContext.getAttribute("deviceUnique") != null ? servletContext.getAttribute("deviceUnique").toString() : "";
        System.out.println("deviceUnique : " + deviceUnique);
        authInfo.put("deviceUnique", deviceUnique);
        try {
            String fileContent = IOUtils.toString(inStream, "UTF-8");
            JSONObject jasonObject;
            String authContent = ReadAuthUtil.readAuth(fileContent);
            if (authContent == null || "".equals(authContent)) {
                authInfo.put("authmsg", "授权文件无效，错误代码：-3");
                return authInfo;
            }

            jasonObject = JSONObject.parseObject(authContent);

            //以下做校验
            String osaFlag = jasonObject.get("product").toString();

            if ("".equals(deviceUnique) || !deviceUnique.equals(jasonObject.get("hwserial").toString())) {
                authmsg = "设备唯一标识码验证失败";
            } else if (!ConstantsDto.PROCJECT_IDENTIFICATION.equalsIgnoreCase(osaFlag)) {
                authmsg = "项目标识验证失败";
            } else if (!jasonObject.containsKey("company")) {
                authmsg = "公司名称不存在";
            }

        } catch (Exception e) {
            e.printStackTrace();
            //这里要求文件比较严格 上面若是有某个字段不正确 则抛出异常
            authmsg = "授权文件无效，错误代码：-4";
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //校验只需要校验格式
        authInfo.put("authmsg", authmsg);
        return authInfo;
    }

    public static Boolean checkFileExsits(String fileUrl, String fileName) {
        File file = new File(fileUrl, fileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
