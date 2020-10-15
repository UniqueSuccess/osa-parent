package com.goldencis.osa.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * 文件上传公共类
 * Created by limingchao on 2018/1/10.
 */
public class FileUpload {

    /**
     * 上传文件
     */
    public static void uploadFile(MultipartFile packageFile, String dirPath, String fileName) {

        InputStream inStream = null;
        FileOutputStream fos = null;

        try {
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String realPath = dirPath + "/" + fileName;
            File prePackageFile = new File(realPath);
            if (prePackageFile.exists()) {
                if (!prePackageFile.delete()) {
                    throw new RuntimeException("file exists and cannot remove！");
                }
            }

            // 用数据流的方式二次读取和保存数据保证上传的缓存效果和安全性
            if (packageFile != null) {
                inStream = packageFile.getInputStream();
                fos = new FileOutputStream(prePackageFile);
                byte[] buffer = new byte[1024];
                int byteread;
                while ((byteread = inStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteread);
                }
                fos.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("upload file error!");
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean writeStrInFile(File file, String content) {
        boolean flag = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes("UTF-8"));
            fos.flush();
            FileDescriptor fd = fos.getFD();
            fd.sync();
            flag = true;
        } catch (IOException e) {
        } finally {
            //不要忘记关闭
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }

        return flag;
    }
}
