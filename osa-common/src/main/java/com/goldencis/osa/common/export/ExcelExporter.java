package com.goldencis.osa.common.export;

import com.goldencis.osa.common.export.annotation.Export;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;

/**
 * 这个导出工具类,不能满足需求
 *
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-10-30 16:44
 **/
public class ExcelExporter<T> {

    /**
     * excel文件后缀
     */
    private static final String SUFFIX_EXCEL = ".xls";

    private Builder<T> builder;

    private void setBuilder(Builder<T> builder) {
        this.builder = builder;
    }

    private ExcelExporter() {
    }

    public File export() {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 计算sheet页
        int sheetCount = calculateSheetCount();
        // header样式
        HSSFCellStyle headerStyle = builder.headerStyle;
        if (headerStyle == null) {
            headerStyle = defaultHeaderStyle(workbook);
        }
        // 正文样式
        HSSFCellStyle bodyStyle = builder.bodyStyle;
        if (bodyStyle == null) {
            bodyStyle = defaultBodyStyle(workbook);
        }
        // 获取header
        List<IHeader> headerList = getHeader();
        List<T> data = builder.data;
        int sheetSize = builder.sheetSize;
        String title = builder.title;
        HSSFFont richStringFont = workbook.createFont();
        richStringFont.setColor(IndexedColors.BLUE.index);
        for (int sheetNum = 1; sheetNum <= sheetCount; sheetNum++) {
            // 生成一个表格
            HSSFSheet sheet = workbook.createSheet(title + sheetNum);
            // 设置表格默认列宽度为15个字节
            sheet.setDefaultColumnWidth(15);
            //产生表格标题行
            HSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < headerList.size(); i++) {
                HSSFCell cell = headerRow.createCell(i);
                cell.setCellStyle(headerStyle);
                HSSFRichTextString text = new HSSFRichTextString(headerList.get(i).content());
                cell.setCellValue(text);
            }
            // 声明一个画图的顶级管理器
            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
            List<T> subList = cuteData(sheetNum, sheetCount, sheetSize);
            Iterator<T> iterator = subList.iterator();
            int index = 0;
            while (iterator.hasNext()) {
                index++;
                T t = iterator.next();
                // 绘制一行信息
                drawRow(sheet, index, t, bodyStyle, patriarch, workbook, richStringFont);
            }
            int maxColumn = headerList.size();
            //获取当前列的宽度，然后对比本列的长度，取最大值
            for (int columnNum = 0; columnNum <= maxColumn; columnNum++) {
                int columnWidth = sheet.getColumnWidth(columnNum) / 256;
                for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    if (sheet.getRow(rowNum) != null && sheet.getRow(rowNum).getCell(columnNum) != null) {
                        int length = sheet.getRow(rowNum).getCell(columnNum).toString().getBytes().length;
                        if (columnWidth < length + 1) {
                            columnWidth = (length + 2) < 255 ? (length + 2) : 255;
                        }
                    }
                }
                sheet.setColumnWidth(columnNum, columnWidth * 256);
            }
        }
        // 写出到文件
        File dir = new File(builder.outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = Objects.isNull(builder.outputName) ? "" : builder.outputName;
        SimpleDateFormat sdf = new SimpleDateFormat(Pattern.PATTERN_1.getValue());
        fileName = fileName + sdf.format(new Date()) + SUFFIX_EXCEL;
        File target = new File(dir, fileName);
        try (FileOutputStream out = new FileOutputStream(target)) {
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
        return target;
    }

    /**
     * 绘制一行
     *
     * @param sheet
     * @param index
     * @param t
     * @param bodyStyle
     * @param patriarch
     * @param workbook
     * @param richStringFont
     */
    private void drawRow(HSSFSheet sheet, int index, T t, HSSFCellStyle bodyStyle, HSSFPatriarch patriarch, HSSFWorkbook workbook, HSSFFont richStringFont) {
        // 创建一行
        HSSFRow row = sheet.createRow(index);
        Field[] fields = t.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Export export = field.getAnnotation(Export.class);
            // 没有注解或者指定被禁用的字段,跳过
            if (Objects.isNull(export)) {
                continue;
            }
            HSSFCell cell = row.createCell(export.order());
            cell.setCellStyle(bodyStyle);
            String fieldName = field.getName();
            String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                Class<? extends Object> tCls = t.getClass();
                Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                Object value = getMethod.invoke(t, new Object[]{});
                //判断值的类型后进行强制类型转换
                String textValue = null;

                if (value instanceof Date) {
                    Date date = (Date) value;
                    SimpleDateFormat sdf = new SimpleDateFormat(builder.pattern.getValue());
                    textValue = sdf.format(date);
                } else if (value instanceof byte[]) {
                    // 有图片时，设置行高为60px;
                    row.setHeightInPoints(60);
                    // 设置图片所在列宽度为80px,注意这里单位的一个换算
                    sheet.setColumnWidth(i, (int) (35.7 * 80));
                    // sheet.autoSizeColumn(i);
                    byte[] bsValue = (byte[]) value;
                    HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 6, index, (short) 6,
                            index);
                    anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
                    patriarch.createPicture(anchor,
                            workbook.addPicture(bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
                } else {
                    //其它数据类型都当作字符串简单处理
                    textValue = value == null ? "" : value.toString();
                }
                //如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                if (textValue != null) {
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile("^//d+(//.//d+)?$");
                    Matcher matcher = p.matcher(textValue);
                    if (matcher.matches()) {
                        //是数字当作double处理
                        cell.setCellValue(Double.parseDouble(textValue));
                    } else {
                        HSSFRichTextString richString = new HSSFRichTextString(textValue);
                        richString.applyFont(richStringFont);
                        cell.setCellValue(richString);
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private List<IHeader> getHeader() {
        List<IHeader> list = new ArrayList<>();
        Field[] declaredFields = getTClass().getDeclaredFields();
        for (Field field : declaredFields) {
            Export annotation = field.getAnnotation(Export.class);
            if (annotation == null) {
                continue;
            }
            HeaderImpl header = new HeaderImpl(annotation.header(), annotation.order());
            list.add(header);
        }
        Collections.sort(list);
        return list;
    }

    private Class<T> getTClass() {
        List<T> data = builder.data;
        if (data.isEmpty()) {
            return builder.clazz;
        }
        T t = data.get(0);
        return (Class<T>) t.getClass();
    }

    /**
     * 根据sheet页截取数据源
     *
     * @param sheetNum   当前的sheet页码,从1开始
     * @param sheetCount
     * @param sheetSize  每个sheet允许的最大数量
     * @return 截取后的数据集合
     */
    private List<T> cuteData(int sheetNum, int sheetCount, int sheetSize) {
        List<T> data = builder.data;
        int fromIndex = (sheetNum - 1) * sheetSize;
        if (sheetNum == sheetCount) {
            return data.subList(fromIndex, data.size());
        } else {
            return data.subList(fromIndex, sheetNum * sheetSize);
        }
    }

    /**
     * 根据数据源数量以及每个sheet页允许的最大数量计算sheet页数量
     *
     * @return sheet页数量
     */
    private int calculateSheetCount() {
        int size = builder.data.size();
        int num = builder.sheetSize;
        // sheet页的数量
        int sheetCount = size / num;
        if (size % num > 0) {
            sheetCount++;
        }
        return sheetCount < 1 ? 1 : sheetCount;
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
        style.setAlignment(builder.alignType);
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
    private HSSFCellStyle defaultBodyStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(builder.alignType);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBold(true);
        // 把字体应用到当前的样式
        style.setFont(font2);
        return style;
    }

    public static class Builder<T> {

        /**
         * 每个sheet页的最大容量
         */
        private static final int MAX_SIZE = 50000;

        /**
         * 每个sheet页的标题
         */
        private String title = "Sheet";
        /**
         * 每个sheet页的条目数量
         */
        private int sheetSize;
        /**
         * 输出路径
         */
        private String outputPath;
        /**
         * 输出文件名
         */
        private String outputName;
        /**
         * header样式
         */
        private HSSFCellStyle headerStyle;
        /**
         * 正文样式
         */
        private HSSFCellStyle bodyStyle;
        /**
         * 数据源
         */
        private List<T> data;
        /**
         * 日期转换格式
         */
        private Pattern pattern = Pattern.PATTERN_2;
        /**
         * 对齐方式??
         */
        private HorizontalAlignment alignType = HorizontalAlignment.CENTER;
        /**
         * 泛型
         */
        private Class<T> clazz;

        public Builder<T> setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder<T> setSheetSize(int sheetSize) {
            this.sheetSize = sheetSize;
            return this;
        }

        public Builder<T> setOutputPath(String outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public Builder<T> setOutputName(String outputName) {
            this.outputName = outputName;
            return this;
        }

        public Builder<T> setHeaderStyle(HSSFCellStyle headerStyle) {
            this.headerStyle = headerStyle;
            return this;
        }

        public Builder<T> setBodyStyle(HSSFCellStyle bodyStyle) {
            this.bodyStyle = bodyStyle;
            return this;
        }

        public Builder<T> setData(List<T> data) {
            this.data = data;
            return this;
        }

        public Builder<T> setPattern(Pattern pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder<T> setAlignType(HorizontalAlignment alignType) {
            this.alignType = alignType;
            return this;
        }

        /**
         * 指定数据源的泛型
         *
         * @param clazz
         * @return
         */
        public Builder<T> setClazz(Class<T> clazz) {
            this.clazz = clazz;
            return this;
        }

        public ExcelExporter<T> build() {
            if (sheetSize <= 0 || sheetSize > MAX_SIZE) {
                sheetSize = MAX_SIZE;
            }
            if (Objects.isNull(data)) {
                throw new IllegalArgumentException("先设置数据源");
            }
            if (Objects.isNull(outputPath) || outputPath.isEmpty()) {
                throw new IllegalArgumentException("先设置输出路径");
            }
            if (Objects.isNull(alignType)) {
                throw new IllegalArgumentException("alignType can not be null");
            }
            if (Objects.isNull(pattern)) {
                throw new IllegalArgumentException("时间格式不能为空");
            }
            if (Objects.isNull(clazz)) {
                throw new IllegalArgumentException("指定数据泛型");
            }
            ExcelExporter<T> exporter = new ExcelExporter<>();
            exporter.setBuilder(this);
            return exporter;
        }
    }

}
