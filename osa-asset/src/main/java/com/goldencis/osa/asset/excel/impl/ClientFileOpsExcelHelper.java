package com.goldencis.osa.asset.excel.impl;

import com.goldencis.osa.asset.excel.IExport;
import com.goldencis.osa.asset.excel.IImport;
import com.goldencis.osa.asset.excel.annotation.Export;
import com.goldencis.osa.asset.excel.domain.AssetSheet;
import com.goldencis.osa.asset.excel.domain.ClientFileOpsItem;
import com.goldencis.osa.asset.excel.header.IHeader;
import com.goldencis.osa.asset.excel.provide.impl.CellStyleProvider;
import com.goldencis.osa.asset.excel.provide.impl.HeaderProvider;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * 报表 -- 行为审计 （文件操作）导出
 **/
@Component
public class ClientFileOpsExcelHelper implements IImport<HSSFWorkbook>, IExport<IExport.Builder<HSSFWorkbook, ClientFileOpsItem>> {

    private final Logger logger = LoggerFactory.getLogger(ClientFileOpsExcelHelper.class);

    @Autowired
    private HeaderProvider headerProvider;
    @Autowired
    private CellStyleProvider cellStyleProvider;

    @Override
    public String sheetName() {
        return AssetSheet.USERREPORT.tag();
    }

    @Override
    public void in(HSSFWorkbook sheets) throws Exception {

    }

    //导出
    @Override
    public void export(Builder<HSSFWorkbook, ClientFileOpsItem> builder) throws Exception {
        HSSFWorkbook workbook = builder.getT();
        if (Objects.isNull(workbook)) {
            throw new Exception("HSSFWorkbook can not be null");
        }
        HSSFSheet sheet = workbook.createSheet(sheetName());
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(15);

        List<ClientFileOpsItem> list = builder.getList();
        if (CollectionUtils.isEmpty(list)) {
            throw new Exception("列表为空");
        }
        List<IHeader> headerList = headerProvider.provide(ClientFileOpsItem.class);
        if (CollectionUtils.isEmpty(headerList)) {
            throw new Exception("标题头集合不能为空");
        }
        HSSFCellStyle headerStyle = cellStyleProvider.provide(new CellStyleProvider.Param(workbook, CellStyleProvider.Type.HEADER));
        HSSFRow headerRow = sheet.createRow(0);
        for (IHeader header : headerList) {
            HSSFCell cell = headerRow.createCell(header.order());
            cell.setCellStyle(headerStyle);
            cell.setCellValue(new HSSFRichTextString(header.content()));
        }

        // 从第一行开始画
        int rowNum = 1;
        HSSFCellStyle contentStyle = cellStyleProvider.provide(new CellStyleProvider.Param(workbook, CellStyleProvider.Type.CONTENT));
        for (ClientFileOpsItem item : list) {
            HSSFRow row = sheet.createRow(rowNum);
            drawLine(item, row, contentStyle);
            ++rowNum;
        }
    }

    private void drawLine(ClientFileOpsItem item, HSSFRow row, HSSFCellStyle contentStyle) throws Exception {
        Class<? extends ClientFileOpsItem> clazz = item.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            Export export = field.getAnnotation(Export.class);
            if (Objects.isNull(export)) {
                continue;
            }
            HSSFCell cell = row.createCell(export.order());
            cell.setCellStyle(contentStyle);
            String fieldName = field.getName();
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = clazz.getMethod(methodName, new Class[]{});
            Object object = method.invoke(item);
            if (object instanceof String) {
                cell.setCellStyle(contentStyle);
                cell.setCellValue(new HSSFRichTextString((String) object));
            } else {
                logger.warn("{}.{} do not instanceof String", clazz.getName(), fieldName);
            }
        }
    }
}
