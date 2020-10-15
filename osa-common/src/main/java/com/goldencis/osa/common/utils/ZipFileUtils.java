package com.goldencis.osa.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileUtils {

    public static void scZip(List<Map<String, Object>> backupList, String zipFile){
        ZipOutputStream out = null;
        FileInputStream fis = null;
        try {
            byte[] buffer = new byte[1024];
            out = new ZipOutputStream(new FileOutputStream(zipFile));
            File file = null;
            for (int i = 0; i < backupList.size(); i++) {
                Map<String, Object> bacuk = backupList.get(i);
                if (bacuk.containsKey("filePath")) {
                    file = new File(bacuk.get("filePath").toString());
                    fis = new FileInputStream(file);
                    out.putNextEntry(new ZipEntry(bacuk.get("name").toString() + ".sql"));
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    out.closeEntry();
                    fis.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(out != null){
                    out.close();
                }
                if(fis != null){
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
