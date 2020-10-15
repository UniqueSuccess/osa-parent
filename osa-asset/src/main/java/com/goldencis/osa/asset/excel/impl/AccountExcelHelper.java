package com.goldencis.osa.asset.excel.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.excel.IExport;
import com.goldencis.osa.asset.excel.IImport;
import com.goldencis.osa.asset.excel.ImportMode;
import com.goldencis.osa.asset.excel.annotation.Export;
import com.goldencis.osa.asset.excel.annotation.Import;
import com.goldencis.osa.asset.excel.domain.*;
import com.goldencis.osa.asset.excel.header.IHeader;
import com.goldencis.osa.asset.excel.provide.impl.CellStyleProvider;
import com.goldencis.osa.asset.excel.provide.impl.HeaderProvider;
import com.goldencis.osa.asset.service.IAssetAccountService;
import com.goldencis.osa.asset.service.IAssetService;
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
 * @create: 2018-12-17 11:33
 **/
@Component
public class AccountExcelHelper implements IImport<Sheet>, IExport<IExport.Builder<HSSFWorkbook, AssetAccount>> {

    private final Logger logger = LoggerFactory.getLogger(AccountExcelHelper.class);
    private ImportMode mode = ImportMode.STANDARD;
    @Autowired
    private IAssetService assetService;
    @Autowired
    private IAssetAccountService assetAccountService;
    @Autowired
    private HeaderProvider headerProvider;
    @Autowired
    private CellStyleProvider cellStyleProvider;

    @Override
    public String sheetName() {
        return AssetSheet.ACCOUNT.tag();
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
        List<AccountItem> list = new ArrayList<>();
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (Objects.isNull(row)) {
                logger.warn("row is null, rowNum: {}", rowNum);
                continue;
            }
            AccountItem item = new AccountItem();
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
                Method method = AccountItem.class.getMethod(methodName, field.getType());
                method.invoke(item, value);
            }
        }
        intoDb(list);
    }

    private void intoDb(List<AccountItem> list) throws Exception {
        List<AssetAccount> assetAccounts = new ArrayList<>();
        for (AccountItem item : list) {
            Trusteeship trusteeship = Trusteeship.matchByContent(item.getTrusteeship());
            if (Objects.isNull(trusteeship)) {
                warnOrError(String.format("账号(%s)托管类型错误: %s", item.getAccount(), item.getTrusteeship()));
                continue;
            }
            Asset asset = assetService.getOne(new QueryWrapper<Asset>().eq("name", item.getAssetName()));
            if (Objects.isNull(asset)) {
                warnOrError(String.format("设备信息不存在: %s", item.getAssetName()));
                continue;
            }
            AssetAccount one = assetAccountService.getOne(new QueryWrapper<AssetAccount>().eq("asset_id", asset.getId()).eq("username", item.getAccount()));
            if (Objects.nonNull(one)) {
                warnOrError(String.format("该设备(%s)下已经存在同名账号: %s", item.getAssetName(), item.getAccount()));
                continue;
            }
            AssetAccount account = new AssetAccount();
            account.setAssetId(asset.getId());
            account.setUsername(item.getAccount());
            account.setPassword(item.getPassword());
            account.setTrusteeship(trusteeship.getIntValue());
            assetAccounts.add(account);
        }
        if (!CollectionUtils.isEmpty(assetAccounts)) {
            assetAccountService.saveOrUpdateBatch(assetAccounts);
        } else {
            logger.warn("设备账号列表为空,不需要入库");
        }
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
    //endregion

    //region 导出
    @Override
    public void export(Builder<HSSFWorkbook, AssetAccount> builder) throws Exception {
        HSSFWorkbook workbook = builder.getT();
        if (Objects.isNull(workbook)) {
            throw new Exception("HSSFWorkbook can not be null");
        }
        List<AssetAccount> list = builder.getList();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("设备账号列表为空");
            return;
        }
        HSSFSheet sheet = workbook.createSheet(sheetName());
        List<IHeader> headerList = headerProvider.provide(AccountItem.class);
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
        List<AccountItem> collect = list.stream()
                .map(this::parse2AccountItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // 从第一行开始画
        int rowNum = 1;
        for (AccountItem item : collect) {
            HSSFRow row = sheet.createRow(rowNum);
            drawLine(item, row, contentStyle);
            ++rowNum;
        }
    }

    private void drawLine(AccountItem item, HSSFRow row, HSSFCellStyle contentStyle) throws Exception {
        Class<? extends AccountItem> clazz = item.getClass();
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

    private AccountItem parse2AccountItem(AssetAccount account) {
        if (Objects.isNull(account)) {
            logger.warn("account is null");
            return null;
        }
        Integer assetId = account.getAssetId();
        Asset asset = assetService.getById(assetId);
        if (Objects.isNull(asset)) {
            logger.warn("asset is null");
            return null;
        }
        AccountItem item = new AccountItem();
        Trusteeship trusteeship = Trusteeship.matchByValue(account.getTrusteeship());
        item.setAssetName(asset.getName());
        item.setAccount(account.getUsername());
        item.setPassword(account.getPassword());
        item.setTrusteeship(trusteeship.getContent());
        return item;
    }
    //endregion
}
