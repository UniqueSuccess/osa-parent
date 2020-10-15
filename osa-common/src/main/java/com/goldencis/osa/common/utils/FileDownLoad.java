package com.goldencis.osa.common.utils;

import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * 文件下载公共类
 * @author Administrator
 *
 */
public class FileDownLoad {
    /**
     * 文件下载
     * @param response
     * @param request
     * @param path
     */
    public void download(HttpServletResponse response, HttpServletRequest request, String path, String newFileName) {
        FileInputStream inputstream = null;
        OutputStream toClient = null;
        try {
            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename;
            if (StringUtils.isEmpty(newFileName)) {
                filename = file.getName();
            } else {
                filename = newFileName;
            }
            // 以流的形式下载文件。
            inputstream = new FileInputStream(path);
            InputStream fis = new BufferedInputStream(inputstream);
            byte[] buffer = new byte[fis.available()];
            int count = fis.read(buffer);
            fis.close();

            // 清空response
            response.reset();

            // 设置response的Header
            if (request.getHeader("User-Agent") != null &&
                    (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0
                    || request.getHeader("User-Agent").toUpperCase().indexOf("TRIDENT") > 0
                    || request.getHeader("User-Agent").toUpperCase().indexOf("EDGE") > 0)) {
                response.setHeader("Content-Disposition",
                        "attachment;" + "filename=" + URLEncoder.encode(filename, "UTF-8"));
            } else {//firefox、chrome、safari、opera
                response.setHeader("Content-Disposition",
                        "attachment;" + "filename=" + new String(filename.getBytes("UTF8"), "ISO8859-1"));
            }

            response.addHeader("Content-Length", String.valueOf(file.length()));
            ServletOutputStream sout = response.getOutputStream();
            toClient = new BufferedOutputStream(sout);

            if (request.getAttribute("fileType") != null
                    && "csv".equalsIgnoreCase(request.getAttribute("fileType").toString())) {
                response.setContentType("multipart/form-data");
                toClient.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
            } else {
                response.setContentType("application/vnd.ms-excel;charset=ISO8859-1");
            }

            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            sout.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
                if (inputstream != null) {
                    inputstream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void download(HttpServletResponse response, HttpServletRequest request, String path) {
        this.download(response, request, path, null);
    }

    public void downloadNoExcel(HttpServletResponse response, HttpServletRequest request, String path, String fileName) {
        File file = new File(path, fileName);
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            try {
                response.addHeader("Content-Disposition", "attachment;fileName="
                        + new String(fileName.getBytes("UTF8"), "ISO8859-1"));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            // 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
