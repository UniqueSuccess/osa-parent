package com.goldencis.osa.asset.excel.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.asset.entity.*;
import com.goldencis.osa.asset.excel.IExport;
import com.goldencis.osa.asset.excel.IImport;
import com.goldencis.osa.asset.excel.ImportMode;
import com.goldencis.osa.asset.excel.ImportRule;
import com.goldencis.osa.asset.excel.annotation.Export;
import com.goldencis.osa.asset.excel.annotation.Import;
import com.goldencis.osa.asset.excel.domain.AssetItem;
import com.goldencis.osa.asset.excel.domain.AssetSheet;
import com.goldencis.osa.asset.excel.domain.Publish;
import com.goldencis.osa.asset.excel.header.IHeader;
import com.goldencis.osa.asset.excel.provide.impl.CellStyleProvider;
import com.goldencis.osa.asset.excel.provide.impl.HeaderProvider;
import com.goldencis.osa.asset.resource.domain.*;
import com.goldencis.osa.asset.service.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
 * 设备导入
 *
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-13 16:43
 **/
@Component
public class AssetExcelHelper implements IImport<HSSFWorkbook>, IExport<IExport.Builder<HSSFWorkbook, Asset>> {

    private final Logger logger = LoggerFactory.getLogger(AssetExcelHelper.class);
    private ImportMode mode = ImportMode.STANDARD;
    private ImportRule rule = ImportRule.PART;
    /**
     * 授权数量
     */
    private int authNumber = 0;
    @Autowired
    private IAssetService assetService;
    @Autowired
    private ISsoRuleService ssoRuleService;
    @Autowired
    private IAssetgroupService assetgroupService;
    @Autowired
    private IAssetTypeService assetTypeService;
    @Autowired
    private HeaderProvider headerProvider;
    @Autowired
    private CellStyleProvider cellStyleProvider;
    @Autowired
    private AccountExcelHelper accountExcelHelper;
    @Autowired
    private RuleExcelHelper ruleExcelHelper;
    @Autowired
    private IAssetAccountService assetAccountService;
    @Autowired
    private IAssetAssetgroupService assetAssetgroupService;
    @Autowired
    private AssetDataHandler assetDataHandler;
    @Autowired
    private AssetGroupExcelHelper assetGroupExcelHelper;

    @Override
    public String sheetName() {
        return AssetSheet.ASSET.tag();
    }

    //region 导入
    @Override
    public void in(HSSFWorkbook workbook) throws Exception {
        // 是否全量导入
        boolean allRule = ImportRule.ALL.equals(rule);
        assetDataHandler.setEnable(allRule);
        assetDataHandler.cache();
        try {
            // 只有全量导入才会导入设备组信息
            // 增量导入,不会导入设备组信息,且不会暂存数据库中现有的数据
            if (allRule) {
                assetGroupExcelHelper.in(workbook.getSheet(assetGroupExcelHelper.sheetName()));
            }
            internalIn(workbook);
            assetDataHandler.cleanup();
        } catch (Exception e) {
            assetDataHandler.restore();
            throw e;
        }
    }

    private void internalIn(HSSFWorkbook workbook) throws Exception {
        if (Objects.isNull(workbook)) {
            throw new Exception("workbook can not be null");
        }
        HSSFSheet sheet = workbook.getSheet(sheetName());
        if (Objects.isNull(sheet)) {
            throw new Exception("sheet can not be null");
        }
        // 将表格中条目转换为对象集合
        List<AssetItem> list = new ArrayList<>();
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (Objects.isNull(row)) {
                logger.warn("row is null, rowNum: {}", rowNum);
                continue;
            }
            AssetItem item = new AssetItem();
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
                Method method = AssetItem.class.getMethod(methodName, field.getType());
                method.invoke(item, value);
            }
        }

        // 获取允许导入的点数
        int count = assetService.count(null);
        int allowNumber = this.authNumber - count;
        if (allowNumber <= 0) {
            logger.debug("授权点数为{},数据库中现有数量为{},允许导入数量为{},不能执行导入", authNumber, count, allowNumber);
            return;
        }

        List<AssetItem> publishList = new ArrayList<>();
        List<AssetItem> normalList = new ArrayList<>();
        // 拆分数据集合
        splitList(list, publishList, normalList);

        if (publishList.size() >= allowNumber) {
            List<AssetItem> publishTemp = publishList.subList(0, allowNumber);
            publishList.clear();
            publishList.addAll(publishTemp);
        } else {
            int i = allowNumber - publishList.size();
            if (normalList.size() >= i) {
                List<AssetItem> normalTemp = normalList.subList(0, i);
                normalList.clear();
                normalList.addAll(normalTemp);
            }
        }

        // 将应用程序发布器单独拿出来,先入库,
        savePublish(publishList);
        // 导入单点登录规则
        ruleExcelHelper.in(workbook.getSheet(ruleExcelHelper.sheetName()));
        // 保存普通设备信息
        saveNormal(normalList);
        // 导入账号信息
        accountExcelHelper.in(workbook.getSheet(accountExcelHelper.sheetName()));
    }

    private void splitList(List<AssetItem> list, List<AssetItem> publishList, List<AssetItem> normalList) {
        for (AssetItem item : list) {
            String isPublish = item.getIsPublish();
            if (StringUtils.isEmpty(isPublish) || !Publish.YES.getContent().equals(isPublish)) {
                normalList.add(item);
            } else {
                publishList.add(item);
            }
        }
    }

    /**
     * 保存应用程序发布器
     *
     * @param list
     * @throws Exception
     */
    private void savePublish(List<AssetItem> list) throws Exception {
        List<Asset> assetList = new ArrayList<>();
        for (AssetItem item : list) {
            // 应用程序发布器,管理账号,管理密码都不能为空
            if (StringUtils.isEmpty(item.getAdminAccount()) || StringUtils.isEmpty(item.getAdminPassword())) {
                warnOrError(String.format("应用程序发布器,管理账号,管理密码都不能为空,账号:%s,密码:%s", item.getAdminAccount(), item.getAdminPassword()));
                continue;
            }
            String assetName = item.getAssetName();
            String assetIp = item.getAssetIp();
            if (Objects.nonNull(assetService.getOne(new QueryWrapper<Asset>().eq("name", assetName)))) {
                logger.warn(String.format("已经存在同名的设备信息,名称:%s,ip:%s", assetName, assetIp));
                continue;
            }
            Assetgroup assetgroup = assetgroupService.getOne(new QueryWrapper<Assetgroup>().eq("name", item.getAssetGroupName()));
            if (Objects.isNull(assetgroup)) {
                warnOrError(String.format("设备组不存在: %s", item.getAssetGroupName()));
                continue;
            }
            AssetType assetType = assetTypeService.getOne(new QueryWrapper<AssetType>().eq("name", item.getAssetType()));
            if (Objects.isNull(assetType)) {
                warnOrError("设备类型不存在: " + item.getAssetType());
                continue;
            }
            // 最高级设备类型
            AssetType superType = assetTypeService.getMostSuperiorAssetTypeById(assetType.getId());
            if (Objects.isNull(superType)) {
                warnOrError("设备类型不存在: " + item.getAssetType());
                continue;
            }
            if (!new Windows().typeId().equals(superType.getId())) {
                warnOrError(String.format("这个应用程序发布器不是Windows类型: %s", item.toString()));
                continue;
            }
            Asset asset = new Asset();
            asset.setName(item.getAssetName());
            asset.setAccount(item.getAdminAccount());
            asset.setPassword(item.getAdminPassword());
            asset.setEncode(item.getEncode());
            asset.setGroupId(assetgroup.getId());
            asset.setIp(item.getAssetIp());
            asset.setType(assetType.getId());
            asset.setIsPublish(Publish.YES.getIntValue());
            asset.setRemark(item.getRemark());
            assetList.add(asset);
        }
        if (!CollectionUtils.isEmpty(assetList)) {
            assetList.forEach(assetService::saveOrUpdateAsset);
            // 保存设备设备组中间表一定要在保存设备之后执行,否则设备id会为null
            saveAssetAssetGroup(assetList);
        } else {
            logger.warn("设备集合为空,不执行保存操作");
        }
    }

    /**
     * 保存普通设备
     *
     * @param list
     * @throws Exception
     */
    private void saveNormal(List<AssetItem> list) throws Exception {
        for (AssetItem item : list) {
            String assetName = item.getAssetName();
            String assetIp = item.getAssetIp();
            if (Objects.nonNull(assetService.getOne(new QueryWrapper<Asset>().eq("name", assetName)))) {
                logger.warn(String.format("已经存在同名的设备信息,名称:%s,ip:%s", assetName, assetIp));
                continue;
            }
            Assetgroup assetgroup = assetgroupService.getOne(new QueryWrapper<Assetgroup>().eq("name", item.getAssetGroupName()));
            if (Objects.isNull(assetgroup)) {
                warnOrError(String.format("设备组不存在: %s", item.getAssetGroupName()));
                continue;
            }
            AssetType assetType = assetTypeService.getOne(new QueryWrapper<AssetType>().eq("name", item.getAssetType()));
            if (Objects.isNull(assetType)) {
                warnOrError("设备类型不存在: " + item.getAssetType());
                continue;
            }
            // 最高级设备类型
            AssetType superType = assetTypeService.getMostSuperiorAssetTypeById(assetType.getId());
            if (Objects.isNull(superType)) {
                warnOrError("设备类型不存在: " + item.getAssetType());
                continue;
            }
            if (needAdmin(superType.getId())) {
                if (StringUtils.isEmpty(item.getAdminAccount()) || StringUtils.isEmpty(item.getAdminPassword())) {
                    warnOrError(String.format("管理账号和密码均不能为空,账号:%s,密码:%s", item.getAdminAccount(), item.getAdminPassword()));
                    continue;
                }
            }
            Asset asset = new Asset();
            asset.setName(item.getAssetName());
            asset.setAccount(item.getAdminAccount());
            asset.setPassword(item.getAdminPassword());
            asset.setEncode(item.getEncode());
            asset.setGroupId(assetgroup.getId());
            asset.setIp(item.getAssetIp());
            asset.setType(assetType.getId());
            asset.setIsPublish(Publish.NO.getIntValue());
            asset.setRemark(item.getRemark());

            if (superType.getId().equals(new Bs().typeId())) {
                Asset publish = assetService.getOne(new QueryWrapper<Asset>().eq("name", item.getPublish()));
                if (Objects.isNull(publish)) {
                    warnOrError(String.format("应用程序发布器不存在: %s", item.getPublish()));
                    continue;
                }
                AssetBs bs = new AssetBs();
                bs.setAssetId(asset.getId());
                bs.setPublish(publish.getId());
                bs.setLoginUrl(item.getUrl());
                asset.setExtra(bs);
            } else if (superType.getId().equals(new Cs().typeId())) {
                Asset publish = assetService.getOne(new QueryWrapper<Asset>().eq("name", item.getPublish()));
                if (Objects.isNull(publish)) {
                    warnOrError(String.format("应用程序发布器不存在: %s", item.getPublish()));
                    continue;
                }
                SsoRule ssoRule = ssoRuleService.getOne(new QueryWrapper<SsoRule>().eq("name", item.getOperationTool()).eq("asset_id", publish.getId()));
                if (Objects.isNull(ssoRule)) {
                    warnOrError(String.format("应用程序发布规则不存在: %s", item.getOperationTool()));
                    continue;
                }
                AssetCs cs = new AssetCs();
                cs.setPublish(publish.getId());
                cs.setOperationTool(ssoRule.getId());
                asset.setExtra(cs);
            } else if (superType.getId().equals(new Database().typeId())) {
                Asset publish = assetService.getOne(new QueryWrapper<Asset>().eq("name", item.getPublish()));
                if (Objects.isNull(publish)) {
                    warnOrError(String.format("应用程序发布器不存在: %s", item.getPublish()));
                    continue;
                }
                SsoRule ssoRule = ssoRuleService.getOne(new QueryWrapper<SsoRule>().eq("name", item.getOperationTool()).eq("asset_id", publish.getId()));
                if (Objects.isNull(ssoRule)) {
                    warnOrError(String.format("应用程序发布规则不存在: %s", item.getOperationTool()));
                    continue;
                }
                AssetDb db = new AssetDb();
                db.setDbName(item.getDbName());
                db.setOperationTool(ssoRule.getId());
                db.setPublish(publish.getId());
                db.setPort(Integer.valueOf(item.getPort()));
                asset.setExtra(db);
            }
            assetService.saveOrUpdateAsset(asset);
            // 保存设备设备组中间表一定要在保存设备之后执行,否则设备id会为null
            saveAssetAssetGroup(asset);
        }
    }

    private void saveAssetAssetGroup(List<Asset> list) {
        list.forEach(this::saveAssetAssetGroup);
    }

    private void saveAssetAssetGroup(Asset item) {
        AssetAssetgroup one = assetAssetgroupService.getOne(new QueryWrapper<AssetAssetgroup>().eq("asset_id", item.getId()).eq("assetgroup_id", item.getGroupId()));
        if (Objects.isNull(one)) {
            AssetAssetgroup assetgroup = new AssetAssetgroup();
            assetgroup.setAssetgroupId(item.getGroupId());
            assetgroup.setAssetId(item.getId());
            assetAssetgroupService.save(assetgroup);
        }
    }

    /**
     * 返回所有需要管理账号和管理密码的设备类型
     *
     * @return 如果需要管理账号和密码, 返回true;否则,返回false
     */
    private boolean needAdmin(int superTypeId) {
        return new Windows().typeId().equals(superTypeId)
                || new Linux().typeId().equals(superTypeId)
                || new Network().typeId().equals(superTypeId);
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

    /**
     * 设置导入规则
     * @param rule
     */
    public void setRule(ImportRule rule) {
        this.rule = rule;
    }

    public void setAuthNumber(int number) {
        this.authNumber = number;
    }
    //endregion

    //region 导出
    @Override
    public void export(Builder<HSSFWorkbook, Asset> builder) throws Exception {
        HSSFWorkbook workbook = builder.getT();
        if (Objects.isNull(workbook)) {
            throw new Exception("HSSFWorkbook can not be null");
        }
        HSSFSheet sheet = workbook.createSheet(sheetName());
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(15);

        List<Asset> list = builder.getList();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("设备列表为空");
            return;
        }
        List<IHeader> headerList = headerProvider.provide(AssetItem.class);
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

        List<AssetItem> collect = list.stream()
                .map(this::parse2AssetItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // 从第一行开始画
        int rowNum = 1;
        HSSFCellStyle contentStyle = cellStyleProvider.provide(new CellStyleProvider.Param(workbook, CellStyleProvider.Type.CONTENT));
        for (AssetItem item : collect) {
            HSSFRow row = sheet.createRow(rowNum);
            drawLine(item, row, contentStyle);
            ++rowNum;
        }
        // 导出设备账号
        IExport.Builder<HSSFWorkbook, AssetAccount> accountBuilder = new Builder<>();
        accountBuilder.setT(workbook);
        accountBuilder.setList(parse2AccountList(list));
        accountExcelHelper.export(accountBuilder);
        // 导出发布规则
        IExport.Builder<HSSFWorkbook, SsoRule> ruleBuilder = new Builder<>();
        ruleBuilder.setT(workbook);
        ruleBuilder.setList(parse2RuleList(list));
        ruleExcelHelper.export(ruleBuilder);
    }

    private void drawLine(AssetItem item, HSSFRow row, HSSFCellStyle contentStyle) throws Exception {
        Class<? extends AssetItem> clazz = item.getClass();
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

    private AssetItem parse2AssetItem(Asset asset) {
        if (Objects.isNull(asset) || Objects.isNull(asset.getId())) {
            logger.warn("asset is null or assetId is null: " + asset);
            return null;
        }
        Asset detail = assetService.getAssetDetailById(asset.getId());
        return new AssetItem(detail);
    }

    private List<AssetAccount> parse2AccountList(List<Asset> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        return assetAccountService.list(new QueryWrapper<AssetAccount>().in("asset_id", list.stream().map(Asset::getId).collect(Collectors.toList())));
    }

    private List<SsoRule> parse2RuleList(List<Asset> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        return ssoRuleService.list(new QueryWrapper<SsoRule>().in("asset_id", list.stream().map(Asset::getId).collect(Collectors.toList())));
    }


    //endregion
}
