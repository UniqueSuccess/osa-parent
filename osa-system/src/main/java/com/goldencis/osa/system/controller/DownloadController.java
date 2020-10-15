package com.goldencis.osa.system.controller;

import com.goldencis.osa.common.config.ClientConfig;
import com.goldencis.osa.common.config.Config;
import com.goldencis.osa.common.utils.FileDownLoad;
import com.goldencis.osa.system.utils.SystemCons;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Created by limingchao on 2018/11/28.
 */
@Api(tags = "下载管理")
@RestController
public class DownloadController {

    @Autowired
    private Config config;

    @Autowired
    private ClientConfig clientConfig;

    /**
     * 下载OSA客户端接口
     * Created by limingchao on 2018/1/29.
     */
    @ApiOperation(value = "下载OSA客户端")
    @ApiImplicitParam(name = "packageType", value = "下载客户端类型:", dataType = "Integer", paramType = "query")
    @GetMapping(value = "/client/download")
    public void downloadVDPPackage(Integer packageType, HttpServletRequest request, HttpServletResponse response) {
        if (packageType == null) {
            this.responseMsg(response, "下载客户端类型不能为空");
            return;
        }

        String dirPath = clientConfig.getPackageuploadPath();
        String fileName;
        if (SystemCons.CLIENT_PACKAGE == packageType) {
            fileName = clientConfig.getPackageuploadFileName();
        } else if (SystemCons.CLIENT_PACKAGE_XP == packageType) {
            fileName = clientConfig.getXpPackageuploadFileName();
        } else if (SystemCons.BRIDGE_PACKAGE == packageType) {
            fileName = clientConfig.getBridgeuploadFileName();
        } else {
            this.responseMsg(response, "下载客户端类型不正确");
            return;
        }

        String path = dirPath + File.separator + fileName;
        File file = new File(path);
        if (!file.exists()) {
            this.responseMsg(response, "请先上传客户端安装包");
            return;
        }

        int index = fileName.lastIndexOf(".");
        fileName = fileName.substring(0, index) + "_" + config.getHost().replace(".", "_") + "_" + fileName.substring(index);
        FileDownLoad filedownload = new FileDownLoad();
        filedownload.download(response, request, path, fileName);
    }

    private void responseMsg(HttpServletResponse response, String msg) {
        try {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
