package com.goldencis.osa.asset.excel.provide.impl;

import com.goldencis.osa.asset.excel.provide.IProvider;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 单元格样式提供器,根据不同类型提供不同的样式
 *
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-11 16:39
 **/
@Component
public class CellStyleProvider implements IProvider<CellStyleProvider.Param, HSSFCellStyle> {

    private final Logger logger = LoggerFactory.getLogger(CellStyleProvider.class);

    @Override
    public HSSFCellStyle provide(Param param) {
        Type type = param.type;
        switch (type) {
            case HEADER:
                return headerStyle(param.workbook);
            case CONTENT:
                return contentStyle(param.workbook);
            default:
                logger.warn("目前不支持的单元格类型: {}", type);
                return null;
        }
    }

    private HSSFCellStyle headerStyle(HSSFWorkbook workbook) {
        return defaultHeaderStyle(workbook);
    }

    private HSSFCellStyle contentStyle(HSSFWorkbook workbook) {
        return defaultContentStyle(workbook);
    }


    /**
     * 默认的header样式
     *
     * @return
     */
    private HSSFCellStyle defaultHeaderStyle(HSSFWorkbook workbook) {
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(IndexedColors.SKY_BLUE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(IndexedColors.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    /**
     * 默认的正文样式
     *
     * @return
     */
    private HSSFCellStyle defaultContentStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBold(true);
        // 把字体应用到当前的样式
        style.setFont(font2);
        return style;
    }

    /**
     * 单元格类型
     */
    public enum Type {
        /**
         * 标题头
         */
        HEADER,
        /**
         * 具体内容
         */
        CONTENT;
    }

    public static class Param {
        private HSSFWorkbook workbook;
        private Type type;

        public Param(HSSFWorkbook workbook, Type type) {
            this.workbook = workbook;
            this.type = type;
        }
    }

}
