package com.goldencis.osa.system.controller;

import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.constants.PathConfig;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.JsonUtils;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.core.utils.AuthUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * @program: voc-portal-parent
 * @description: 关于模块控制器
 * @author: Wang Mingchao
 * @create: 2018-07-31 08:53
 **/
@Api(tags = "关于模块")
@RestController
@RequestMapping(value = "/system/about")
public class AboutController implements ServletContextAware {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @ApiOperation(value = "上传授权文件")
    @PostMapping(value = "/fileupload", produces = "application/json")
    public ResultMsg fileupload(@RequestParam(value = "file", required = false) MultipartFile file,
                                HttpServletRequest request, HttpServletResponse response) {
        String fileUploadPath = PathConfig.HOMEPATH;
        String fileName = file.getOriginalFilename();

        if (StringUtils.isEmpty(fileName) || !fileName.endsWith(ConstantsDto.SUFFIX_AUTH_FILE)) {
            return ResultMsg.error("授权文件无效，错误代码：-1");
        }
        //校验授权文件是否有效
        try {
            Map<String, Object> result = AuthUtils.checkFileIsValidate(servletContext, file.getInputStream());
            if (result.get("authmsg") != null && !"".equals(result.get("authmsg").toString())) {
                return ResultMsg.error(result.get("authmsg").toString());
            }
        } catch (IOException e1) {
            //校验抛出异常也不允许提交
            return ResultMsg.error("授权文件无效，错误代码：-2");
        }
        fileUploadPath = fileUploadPath.replace('\\', '/');
        if (!fileUploadPath.endsWith("/")) {
            fileUploadPath += "/";
        }
        File tmp = new File(fileUploadPath);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        /**
         * 文件存在则删除文件
         */
        File authFile = new File(fileUploadPath + ConstantsDto.AUTH_FILE_NAME);
        if (authFile.exists()) {
            authFile.delete();
        }

        InputStream inStream = null;
        FileOutputStream fos = null;
        try {
            // 用数据流的方式二次读取和保存数据保证上传的缓存效果和安全性
            if (file != null) {
                inStream = file.getInputStream();
                fos = new FileOutputStream(fileUploadPath + ConstantsDto.AUTH_FILE_NAME);
                byte[] buffer = new byte[1024];
                int byteread = 0;
                while ((byteread = inStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteread);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error("上传文件失败");
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
        return ResultMsg.ok();
    }

    /**
     * 下载模板
     * @param request
     * @param response
     */
    @ApiOperation(value = "导出授权文件")
    @GetMapping(value = "/fileload", produces = "application/json")
    public void fileload(HttpServletRequest request, HttpServletResponse response) {
        File file = new File(PathConfig.HOMEPATH, ConstantsDto.AUTH_FILE_NAME);
        if (file.exists()) {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition", "attachment;fileName=" + ConstantsDto.EXPORT_AUTH_FILE_NAME);// 设置文件名
                byte[] buffer = new byte[1024];

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

    @ApiOperation(value = "获取授权信息")
    @GetMapping(value = "/authInfo")
    public ResultMsg authInfo() {
        try {
            Map<String, Object> authInfo = AuthUtils.getAuthInfo(servletContext);
            String json = JsonUtils.objectToJson(authInfo);
            return ResultMsg.ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    /**
     * 下载模板
     * @param request
     * @param response
     */
    @ApiOperation(value = "判断授权文件是否存在")
    @GetMapping(value = "/isFileExist", produces = "application/json")
    public ResultMsg isFileExist(HttpServletRequest request, HttpServletResponse response) {
        String fileUrl = PathConfig.HOMEPATH;
        File file = new File(fileUrl, ConstantsDto.AUTH_FILE_NAME);
        String result = "";
        if (file.exists()) {
            result = "success";
        } else {
            result = "failure";
        }
        return ResultMsg.ok(result);
    }

}
