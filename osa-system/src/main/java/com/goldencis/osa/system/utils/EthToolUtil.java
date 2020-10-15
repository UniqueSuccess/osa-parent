package com.goldencis.osa.system.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 判断端口
 * */
public class EthToolUtil {

    public static boolean checkEthx(String ethx) {
        String commond = " ifconfig " + ethx;
        String reslut;
        try {
            reslut = runShell(commond);
            if (reslut.indexOf("RUNNING") > -1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String runShell(String commond) throws Exception {
        Process process;
        process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", commond });
        process.waitFor();
        BufferedReader read = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        String result = "";
        while ((line = read.readLine()) != null) {
            result += line;
        }
        return result;
    }
}
