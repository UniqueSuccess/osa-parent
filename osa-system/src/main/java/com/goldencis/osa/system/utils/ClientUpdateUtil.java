package com.goldencis.osa.system.utils;

import java.io.File;
import java.io.IOException;

/**
 * 在控制台上传升级包后，调用脚本解压升级包的工具类
 * Created by limingchao on 2018/2/2.
 */
public class ClientUpdateUtil {

    public static void executeClientUpdate(String dirPath, String gzFilename) {
        try {
            Runtime.getRuntime().exec("tar -zxpf " + dirPath + File.separator + gzFilename + " -C " + dirPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
