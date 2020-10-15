package com.goldencis.osa.asset.excel.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.SsoRule;
import com.goldencis.osa.asset.excel.IExport;
import com.goldencis.osa.asset.excel.IImport;
import com.goldencis.osa.asset.excel.ImportMode;
import com.goldencis.osa.asset.excel.annotation.Export;
import com.goldencis.osa.asset.excel.annotation.Import;
import com.goldencis.osa.asset.excel.domain.AssetSheet;
import com.goldencis.osa.asset.excel.domain.RuleItem;
import com.goldencis.osa.asset.excel.header.IHeader;
import com.goldencis.osa.asset.excel.provide.impl.CellStyleProvider;
import com.goldencis.osa.asset.excel.provide.impl.HeaderProvider;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.ISsoRuleService;
import com.goldencis.osa.core.entity.Dictionary;
import com.goldencis.osa.core.service.IDictionaryService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-17 10:46
 **/
@Component
public class RuleExcelHelper implements IImport<Sheet>, IExport<IExport.Builder<HSSFWorkbook, SsoRule>> {

    private final Logger logger = LoggerFactory.getLogger(RuleExcelHelper.class);
    private ImportMode mode = ImportMode.STANDARD;
    @Autowired
    private IAssetService assetService;
    @Autowired
    private ISsoRuleService ssoRuleService;
    @Autowired
    private IDictionaryService dictionaryService;
    @Autowired
    private HeaderProvider headerProvider;
    @Autowired
    private CellStyleProvider cellStyleProvider;

    @Override
    public String sheetName() {
        return AssetSheet.RULE.tag();
    }

    //region 导入
    @Override
    public void in(Sheet sheet) throws Exception {
        // 发布规则sheet页允许为空,
        // 因为用户可能真的是没有跳板机或者发布规则
        if (Objects.isNull(sheet)) {
            logger.warn("sheet is null");
            return;
        }
        List<RuleItem> list = new ArrayList<>();
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (Objects.isNull(row)) {
                logger.warn("row is null, rowNum: {}", rowNum);
                continue;
            }
            RuleItem item = new RuleItem();
            list.add(item);
            Field[] declaredFields = item.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                Import ann = field.getDeclaredAnnotation(Import.class);
                if (Objects.isNull(ann)) {
                    continue;
                }
                String desc = ann.desc();
                int order = ann.order();
                Cell cell = row.getCell(order);
                if (Objects.isNull(cell)) {
                    warnOrError(String.format("cell is null, desc: %s, columnNum: %d, rowNum: %d", desc, order, rowNum));
                    list.remove(item);
                    break;
                }
                String value = cell.getStringCellValue();
                if (StringUtils.isEmpty(value) && !ann.nullable()) {
                    warnOrError(String.format("value is null, desc: %s, columnNum: %d, rowNum: %d", desc, order, rowNum));
                    list.remove(item);
                    break;
                }
                String fieldName = field.getName();
                String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method method = RuleItem.class.getMethod(methodName, field.getType());
                method.invoke(item, value);
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("不需要导入任何应用程序发布规则");
            return;
        }
        intoDb(list);
    }

    private void intoDb(List<RuleItem> list) throws Exception {
        List<SsoRule> ssoRules = new ArrayList<>();
        for (RuleItem item : list) {
            Asset asset = assetService.getOne(new QueryWrapper<Asset>().eq("name", item.getAssetName()));
            if (Objects.isNull(asset)) {
                warnOrError(String.format("不存在的应用程序发布器: %s", item.getAssetName()));
                continue;
            }
            SsoRule one = ssoRuleService.getOne(new QueryWrapper<SsoRule>().eq("asset_id", asset.getId()).eq("name", item.getName()));
            if (Objects.nonNull(one)) {
                warnOrError(String.format("存在同名的发布规则: %s", item.getName()));
                continue;
            }
            Dictionary dictionary = checkToolType(item);
            if (Objects.isNull(dictionary)) {
                warnOrError(String.format("不存在的工具类型:  %s", item.getToolType()));
                continue;
            }

            SsoRule rule = new SsoRule();
            rule.setAssetId(asset.getId());
            rule.setName(item.getName());
            rule.setRule(item.getRule());
            rule.setToolType(dictionary.getValue());
            ssoRules.add(rule);
        }
        if (!CollectionUtils.isEmpty(ssoRules)) {
            ssoRuleService.saveOrUpdateBatch(ssoRules);
        }
    }

    private Dictionary checkToolType(RuleItem item) {
        List<Dictionary> list = dictionaryService.getAllSSORuleType();
        for (Dictionary dictionary : list) {
            if (dictionary.getName().equals(item.getToolType())) {
                return dictionary;
            }
        }
        return null;
    }

    private void warnOrError(String msg) throws Exception {
        switch (mode) {
            case STRICT:
                throw new Exception(msg);
            case STANDARD:
                logger.warn(msg);
                break;
            default:
                break;
        }
    }

    public void changeImportMode(ImportMode mode) {
        this.mode = mode;
    }
    //endregion

    @Override
    public void export(Builder<HSSFWorkbook, SsoRule> builder) throws Exception {
        HSSFWorkbook workbook = builder.getT();
        if (Objects.isNull(workbook)) {
            throw new Exception("HSSFWorkbook can not be null");
        }
        List<SsoRule> list = builder.getList();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("应用程序发布规则列表为空");
            return;
        }
        HSSFSheet sheet = workbook.createSheet(sheetName());
        List<IHeader> headerList = headerProvider.provide(RuleItem.class);
        if (CollectionUtils.isEmpty(headerList)) {
            throw new Exception("标题头集合不能为空");
        }
        HSSFCellStyle headerStyle = cellStyleProvider.provide(new CellStyleProvider.Param(workbook, CellStyleProvider.Type.HEADER));
        // 绘制标题头
        HSSFRow headerRow = sheet.createRow(0);
        for (IHeader header : headerList) {
            HSSFCell cell = headerRow.createCell(header.order());
            cell.setCellStyle(headerStyle);
            cell.setCellValue(new HSSFRichTextString(header.content()));
        }
        HSSFCellStyle contentStyle = cellStyleProvider.provide(new CellStyleProvider.Param(workbook, CellStyleProvider.Type.CONTENT));
        List<RuleItem> collect = list.stream()
                .map(this::parse2RuleItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // 从第一行开始画
        int rowNum = 1;
        for (RuleItem item : collect) {
            HSSFRow row = sheet.createRow(rowNum);
            drawLine(item, row, contentStyle);
            ++rowNum;
        }
    }

    private RuleItem parse2RuleItem(SsoRule ssoRule) {
        if (Objects.isNull(ssoRule)) {
            logger.warn("ssoRule is null");
            return null;
        }
        Integer assetId = ssoRule.getAssetId();
        Asset asset = assetService.getById(assetId);
        if (Objects.isNull(asset)) {
            logger.warn("asset is null");
            return null;
        }
        Dictionary dictionary = findToolTypeByValue(ssoRule.getToolType());
        if (Objects.isNull(dictionary)) {
            logger.warn("不存在的工具类型: {}", ssoRule);
            return null;
        }
        RuleItem item = new RuleItem();
        item.setAssetName(asset.getName());
        item.setName(ssoRule.getName());
        item.setRule(ssoRule.getRule());
        item.setToolType(dictionary.getName());
        return item;
    }

    private Dictionary findToolTypeByValue(Integer toolType) {
        List<Dictionary> list = dictionaryService.getAllSSORuleType();
        for (Dictionary item : list) {
            if (item.getValue().equals(toolType)) {
                return item;
            }
        }
        return null;
    }

    private void drawLine(RuleItem item, HSSFRow row, HSSFCellStyle contentStyle) throws Exception {
        Class<? extends RuleItem> clazz = item.getClass();
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
