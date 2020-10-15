package com.goldencis.osa.common.utils;

import java.io.File;

/**
 * Created by limingchao on 2018/12/28.
 */
public class FileUtils {

    /**
     * 备份当前文件或者文件夹，原名_bak_日期
     * @param oldFilePath 文件或者文件夹
     */
    public static void backupFolder(String oldFilePath, String newFilePath) {
        File file = new File(oldFilePath);
        if (file.exists()) {
            File dest = new File(newFilePath);
            int i = 1;
            while (dest.exists()) {
                dest = new File(newFilePath + "(" + i++ + ")");
            }
            file.renameTo(dest);
        }
    }
}
