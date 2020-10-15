package com.goldencis.osa.core.utils;

import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.constants.PathConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.*;

/**
 * 启动服务时获取准入服务器数据
 *
 * @author Administrator
 */
@Component
public class InitAuth implements ServletContextAware {

    private final static Logger logger = LoggerFactory.getLogger(InitAuth.class);

    private static boolean hasInited = false;

    @Override
    public void setServletContext(ServletContext sc) {
        if (!hasInited) {
            initUnique(sc);
            hasInited = true;
            logger.info("InitAuth success");
        }
    }

    public static void initUnique(ServletContext sc) {
        String blkResult = execRuntime(new String[]{"sh", "-c", "blkid | grep bin | awk '{print $3}' | awk -F '\"' '{print $2}'"});
        logger.debug("InitAuth success blkid {}", blkResult);
        if (!StringUtils.isEmpty(blkResult)) {
            logger.info("InitAuth success blkid");
            sc.setAttribute("deviceUnique", blkResult);
        }
        String readauthResult = execRuntime(new String[]{"sh", "-c", PathConfig.READ_AUTH_PATH + "/" + ConstantsDto.READ_OSA_AUTH_FILE_NAME + execReadFile(PathConfig.HOMEPATH + "/" + ConstantsDto.AUTH_FILE_NAME)});
        if (!StringUtils.isEmpty(readauthResult)) {
            logger.info("InitAuth success authInfo");
            sc.setAttribute("authInfo", readauthResult);
        }
    }

    /**
     * 执行shell命令
     *
     * @param cmd
     * @return
     */
    private static String execRuntime(String[] cmd) {
        if (cmd == null || cmd.length == 0) {
            return "";
        }
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        if (process != null) {
            try (BufferedReader brStat = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                String fileContent = "";
                while ((line = brStat.readLine()) != null) {
                    fileContent += line;
                }
                return fileContent;
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
            process.destroy();
        }
        return "";
    }

    /**
     * 执行读取文件内容
     *
     * @param file
     * @return
     */
    private static String execReadFile(String file) {
        if (file == null || !new File(file).exists()) {
            return "";
        }
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"))) {
            String line;
            String fileContent = "";
            while ((line = bufferedReader.readLine()) != null) {
                fileContent += line;
            }
            return fileContent;
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return "";
    }
}
