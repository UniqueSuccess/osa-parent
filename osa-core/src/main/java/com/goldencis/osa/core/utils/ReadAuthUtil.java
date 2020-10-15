package com.goldencis.osa.core.utils;

import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.constants.PathConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by limingchao on 2018/10/29.
 */
public class ReadAuthUtil {

    public static String readAuth(String textString) {
        InputStream in = null;
        BufferedReader read = null;
        StringBuilder sb = new StringBuilder();
        try {
            Process pro = Runtime.getRuntime()
                    .exec(PathConfig.READ_AUTH_PATH + File.separator + ConstantsDto.READ_OSA_AUTH_FILE_NAME + " "
                            + textString);
            pro.waitFor();
            in = pro.getInputStream();
            read = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = read.readLine()) != null) {
                sb.append(line);
            }
            System.out.println("授权信息：" + sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
            }

            try {
                if (read != null) {
                    read.close();
                }
            } catch (Exception e2) {
            }
        }

        return sb.toString();
    }
}
