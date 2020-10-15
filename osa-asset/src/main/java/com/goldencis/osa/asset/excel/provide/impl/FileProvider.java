package com.goldencis.osa.asset.excel.provide.impl;

import com.goldencis.osa.asset.excel.provide.IProvider;
import com.goldencis.osa.common.config.Config;
import com.goldencis.osa.common.utils.StringUtils;
import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-11 17:12
 **/
@Component
public class FileProvider implements IProvider<FileProvider.Builder<HSSFWorkbook>, File> {

    @Autowired
    Config config;

    @Override
    public File provide(Builder<HSSFWorkbook> builder) throws Exception {
        checkInvalid(builder);
        if (!builder.getSuffix().startsWith(".")) {
            builder.setSuffix("." + builder.getSuffix());
        }
        String fileName = builder.getFileName() + builder.getSuffix();
        File file = new File(config.getExportPath(), fileName);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        HSSFWorkbook workbook = builder.getInput();
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private void checkInvalid(Builder builder) throws Exception {
        if (Objects.isNull(builder.getInput())) {
            throw new Exception("input can not be null");
        }
        if (StringUtils.isEmpty(builder.getPath())) {
            throw new Exception("path can not be null");
        }
        if (StringUtils.isEmpty(builder.getFileName())) {
            throw new Exception("fileName can not be null");
        }
        if (StringUtils.isEmpty(builder.getSuffix())) {
            throw new Exception("suffix can not be null");
        }
    }

    @Data
    public static class Builder<IN> {
        /**
         * 文件存储的路径
         */
        private String path;
        /**
         * 文件名称
         */
        private String fileName;
        /**
         * 文件后缀
         */
        private String suffix;
        /**
         * 输入
         */
        private IN input;

    }

}
