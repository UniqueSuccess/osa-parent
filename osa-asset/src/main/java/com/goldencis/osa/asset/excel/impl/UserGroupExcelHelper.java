package com.goldencis.osa.asset.excel.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.asset.excel.IExport;
import com.goldencis.osa.asset.excel.IImport;
import com.goldencis.osa.asset.excel.domain.UserSheet;
import com.goldencis.osa.asset.excel.header.IHeader;
import com.goldencis.osa.asset.excel.provide.impl.CellStyleProvider;
import com.goldencis.osa.asset.excel.provide.impl.GroupHeaderProvider;
import com.goldencis.osa.core.entity.Usergroup;
import com.goldencis.osa.core.service.IUsergroupService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 用户组导入
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-10 11:47
 **/
@Component
public class UserGroupExcelHelper implements IImport<Sheet>, IExport<HSSFWorkbook> {

    private final Logger logger = LoggerFactory.getLogger(UserGroupExcelHelper.class);
    @Autowired
    private IUsergroupService usergroupService;
    @Autowired
    private CellStyleProvider cellStyleProvider;
    @Autowired
    private GroupHeaderProvider groupHeaderProvider;
    @Autowired
    private UserGroupImportCallback callback;

    @Override
    public String sheetName() {
        return UserSheet.USERGROUP.tag();
    }

    //region 导入
    @Override
    public void in(Sheet sheet) throws Exception {
        if (Objects.isNull(sheet)) {
            throw new Exception("sheet can not be null");
        }
        // 首先清理数据库
        callback.cleanup();
        int columnCount = sheet.getRow(0).getLastCellNum() - 1;
        int rowCount = sheet.getLastRowNum();
        for (int rowNum = 1; rowNum <= rowCount; rowNum++) {
            checkRowInvalid(sheet, rowNum);
            for (int columnNum = 0; columnNum <= columnCount; columnNum++) {
                Cell cell = sheet.getRow(rowNum).getCell(columnNum);
                if (Objects.isNull(cell)) {
                    logger.debug("cell is null -1,rowNum:{};columnNum:{}", rowNum, columnNum);
                    continue;
                }
                String value = cell.getStringCellValue();
                if (StringUtils.isEmpty(value)) {
                    logger.debug("value is empty -1,rowNum:{};columnNum:{}", rowNum, columnNum);
                    continue;
                }
                // 查询上级部门
                Usergroup parent = findParent(sheet, rowNum, columnNum);
                // 保存到数据库
                callback.save(value.trim(), parent);
            }
        }
    }

    private Usergroup findParent(Sheet sheet, int rowNum, int columnNum) {
        // 第一列或者第一行,不会有上级部门
        if (columnNum <= 0 || rowNum <= 1) {
            return null;
        }
        --columnNum;
        --rowNum;
        for (int num = rowNum; num > 0; num--) {
            Cell cell = sheet.getRow(num).getCell(columnNum);
            if (Objects.isNull(cell)) {
                logger.debug("cell is null -2,rowNum:{};columnNum:{}", rowNum, columnNum);
                continue;
            }
            String value = cell.getStringCellValue();
            if (StringUtils.isEmpty(value)) {
                logger.debug("value is empty -2,rowNum:{};columnNum:{}", rowNum, columnNum);
                continue;
            }
            return callback.findByName(value);
        }
        return null;
    }

    /**
     * 检查一行信息是否存在错误(表头除外)
     * @param sheet 表格
     * @param rowNum 行号
     */
    private void checkRowInvalid(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (Objects.isNull(row)) {
            return;
        }
        int count = 0;
        StringBuilder sb = new StringBuilder();
        for (short i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (Objects.isNull(cell)) {
                continue;
            }
            String value = cell.getStringCellValue();
            if (!StringUtils.isEmpty(value)) {
                ++count;
                sb.append(value).append(",");
            }
        }
        if (count != 1) {
            throw new IllegalArgumentException(String.format("第 %d 行的数据不正确,多个单元格存在信息: %s", rowNum, sb.toString()));
        }
    }
    //endregion

    //region 导出
    @Override
    public void export(HSSFWorkbook workbook) throws Exception {
        HSSFSheet sheet = workbook.createSheet(sheetName());
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(15);
        List<Usergroup> list = usergroupService.list(null);
        int level = getMaxLevel(list);
        List<IHeader> headerList = groupHeaderProvider.provide(level);

        HSSFRow headerRow = sheet.createRow(0);
        HSSFCellStyle headerStyle = cellStyleProvider.provide(new CellStyleProvider.Param(workbook, CellStyleProvider.Type.HEADER));
        for (IHeader item : headerList) {
            HSSFCell cell = headerRow.createCell(item.order(), CellType.STRING);
            cell.setCellStyle(headerStyle);
            HSSFRichTextString text = new HSSFRichTextString(item.content());
            cell.setCellValue(text);
        }
        // 样式
        HSSFCellStyle contentStyle = cellStyleProvider.provide(new CellStyleProvider.Param(workbook, CellStyleProvider.Type.CONTENT));
        recursiveGroup(null, 0, sheet, contentStyle);

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

    private int recursiveGroup(Integer pid, int rowNum, HSSFSheet sheet, HSSFCellStyle contentStyle) {
        QueryWrapper<Usergroup> queryWrapper = new QueryWrapper<>();
        if (Objects.isNull(pid)) {
            queryWrapper.isNull("pid");
        } else {
            queryWrapper.eq("pid", pid);
        }
        List<Usergroup> list = usergroupService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return rowNum;
        }
        for (Usergroup item : list) {
            // 移动到下一行
            ++rowNum;
            // 写到表格中
            HSSFRow row = sheet.createRow(rowNum);
            HSSFCell cell = row.createCell(item.getLevel());
            cell.setCellStyle(contentStyle);
            cell.setCellValue(new HSSFRichTextString(item.getName()));
            rowNum = recursiveGroup(item.getId(), rowNum, sheet, contentStyle);
        }
        return rowNum;
    }

    /**
     * 获取层级最高的level
     * @param list
     * @return
     */
    private int getMaxLevel(List<Usergroup> list) {
        int level = 0;
        for (Usergroup item : list) {
            if (item.getLevel() > level) {
                level = item.getLevel();
            }
        }
        return level;
    }
    //endregion
}
