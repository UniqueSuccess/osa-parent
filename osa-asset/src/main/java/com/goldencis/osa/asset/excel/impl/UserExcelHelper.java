package com.goldencis.osa.asset.excel.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.entity.Assetgroup;
import com.goldencis.osa.asset.entity.Granted;
import com.goldencis.osa.asset.excel.*;
import com.goldencis.osa.asset.excel.IImport;
import com.goldencis.osa.asset.excel.annotation.Export;
import com.goldencis.osa.asset.excel.annotation.Import;
import com.goldencis.osa.asset.excel.domain.UserItem;
import com.goldencis.osa.asset.excel.domain.UserSheet;
import com.goldencis.osa.asset.excel.header.IHeader;
import com.goldencis.osa.asset.excel.provide.impl.CellStyleProvider;
import com.goldencis.osa.asset.excel.provide.impl.HeaderProvider;
import com.goldencis.osa.asset.service.IAssetAccountService;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.IAssetgroupService;
import com.goldencis.osa.asset.service.IGrantedService;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.core.entity.*;
import com.goldencis.osa.core.service.IUserRoleService;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.core.service.IUserUsergroupService;
import com.goldencis.osa.core.service.IUsergroupService;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用户模块导入导出工具类
 *
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-10 15:57
 **/
@Component
public class UserExcelHelper implements IImport<HSSFWorkbook>, IExport<IExport.Builder<HSSFWorkbook, User>> {

    private final Logger logger = LoggerFactory.getLogger(UserExcelHelper.class);
    // 导入模式
    private ImportMode mode = ImportMode.STANDARD;
    // 导入规则(全量或增量),默认增量
    private ImportRule rule = ImportRule.PART;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUsergroupService usergroupService;
    @Autowired
    private IUserRoleService userRoleService;
    @Autowired
    private IUserUsergroupService userUsergroupService;
    @Autowired
    private IAssetService assetService;
    @Autowired
    private IAssetgroupService assetgroupService;
    @Autowired
    private IAssetAccountService assetAccountService;
    @Autowired
    private IGrantedService grantedService;
    @Autowired
    private HeaderProvider headerProvider;
    @Autowired
    private CellStyleProvider cellStyleProvider;
    @Autowired
    private UserDataHandler userDataHandler;
    @Autowired
    private UserGroupExcelHelper userGroupExcelHelper;

    @Override
    public String sheetName() {
        return UserSheet.USER.tag();
    }

    //region 导入
    @Override
    public void in(HSSFWorkbook workbook) throws Exception {
        boolean allRule = ImportRule.ALL.equals(rule);
        userDataHandler.setEnable(allRule);
        userDataHandler.cache();
        try {
            if (allRule) {
                userGroupExcelHelper.in(workbook.getSheet(userGroupExcelHelper.sheetName()));
            }
            HSSFSheet sheet = workbook.getSheet(sheetName());
            internalIn(sheet);
            userDataHandler.cleanup();
        } catch (Exception e) {
            userDataHandler.restore();
            throw e;
        }
    }

    private void internalIn(Sheet sheet) throws Exception {
        if (Objects.isNull(sheet)) {
            throw new Exception("sheet can not be null");
        }
        // 将表格中条目转换为对象集合
        List<UserItem> list = new ArrayList<>();
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (Objects.isNull(row)) {
                logger.warn("row is null, rowNum: {}", rowNum);
                continue;
            }
            UserItem item = new UserItem();
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
                Method method = UserItem.class.getMethod(methodName, field.getType());
                method.invoke(item, value);
            }
        }
        // 获取授权类型
        for (UserItem item : list) {
            item.setGrantType(calcGrantType(item));
        }
        // 将集合中的对象存储到数据库
        intoDb(list.stream().filter(item -> item.getGrantType() != null).collect(Collectors.toList()));
    }

    /**
     * 计算授权类型
     *
     * @param item
     * @return
     */
    private GrantType calcGrantType(UserItem item) throws Exception {
        if (Objects.isNull(item)) {
            throw new IllegalArgumentException("UserItem can not be null");
        }
        String assetGroupName = item.getAssetGroupName();
        String assetName = item.getAssetName();
        String assetIp = item.getAssetIp();
        String account = item.getAccount();
        if (!StringUtils.isEmpty(account)
                && !StringUtils.isEmpty(assetName)
                && !StringUtils.isEmpty(assetIp)) {
            return GrantType.ACCOUNT;
        }
        if (!StringUtils.isEmpty(assetName)
                && !StringUtils.isEmpty(assetIp)) {
            return GrantType.ASSET;
        }
        if (!StringUtils.isEmpty(assetGroupName)) {
            return GrantType.ASSETGROUP;
        }
        if (StringUtils.isEmpty(assetGroupName)
                && StringUtils.isEmpty(assetName)
                && StringUtils.isEmpty(assetIp)
                && StringUtils.isEmpty(account)) {
            return GrantType.NONE;
        }
        warnOrError(String.format("找不到合适的授权类型: %s", item.toString()));
        return null;
    }

    /**
     * 存储到数据库中
     *
     * @param list 数据集合
     * @throws Exception
     */
    private void intoDb(List<UserItem> list) throws Exception {
        for (UserItem item : list) {
            // 查询用户信息是否存在
            String username = item.getUsername();
            List<User> userList = userService.list(new QueryWrapper<User>().eq("username", username));
            if (!CollectionUtils.isEmpty(userList)) {
                warnOrError(String.format("用户信息已经存在,用户名: %s", username));
                continue;
            }
            Granted granted = null;
            switch (item.getGrantType()) {
                case NONE:
                    intoDbForNoneType(item);
                    break;
                case ASSETGROUP:
                    granted = intoDbForAssetgroupType(item);
                    break;
                case ASSET:
                    granted = intoDbForAssetType(item);
                    break;
                case ACCOUNT:
                    granted = intoDbForAccountType(item);
                    break;
                default:
                    break;
            }
            // 保存授权信息
            if (Objects.nonNull(granted)) {
                grantedService.save(granted);
            }
        }
    }

    private void intoDbForNoneType(UserItem item) throws Exception {
        saveUserAndGetId(item);
    }

    /**
     * 授权到设备组类型的用户,入库
     *
     * @param item
     */
    private Granted intoDbForAssetgroupType(UserItem item) throws Exception {
        // 查询出设备组信息
        String assetGroupName = item.getAssetGroupName();
        if (StringUtils.isEmpty(assetGroupName)) {
            warnOrError(String.format("设备组名称不能为空,assetGroupName: %s", assetGroupName));
            return null;
        }
        List<Assetgroup> list = assetgroupService.list(new QueryWrapper<Assetgroup>().eq("name", assetGroupName));
        if (CollectionUtils.isEmpty(list)) {
            warnOrError(String.format("设备组不存在, assetGroupName: %s", assetGroupName));
            return null;
        }
        if (list.size() != 1) {
            logger.warn("存在多个同名的设备组,assetGroupName: {}", assetGroupName);
        }
        Assetgroup assetgroup = list.get(0);
        // 将用户信息入库,并获取用户guid
        String guid = saveUserAndGetId(item);
        if (StringUtils.isEmpty(guid)) {
            return null;
        }
        Granted granted = new Granted();
        granted.setUserId(guid);
        granted.setAssetgroupId(assetgroup.getId());
        granted.setType(GrantType.ASSETGROUP.getValue());
        return granted;
    }

    /**
     * 授权到设备类型的用户,入库
     *
     * @param item
     */
    private Granted intoDbForAssetType(UserItem item) throws Exception {
        // 查询出设备信息
        String assetName = item.getAssetName();
        String assetIp = item.getAssetIp();
        if (StringUtils.isEmpty(assetName) || StringUtils.isEmpty(assetIp)) {
            warnOrError(String.format("设备名称和设备ip均不能为空,assetName: %s, assetIp: %s", assetName, assetIp));
            return null;
        }
        List<Asset> assetList = assetService.list(new QueryWrapper<Asset>().eq("ip", assetIp).eq("name", assetName));
        if (CollectionUtils.isEmpty(assetList)) {
            warnOrError(String.format("设备信息不存在: %s", item.toString()));
            return null;
        }
        if (assetList.size() != 1) {
            logger.warn("有多条ip相同且名称相同的设备,ip: {},name: {}", assetIp, assetName);
        }
        Asset asset = assetList.get(0);
        // 将用户信息入库,并获取用户guid
        String guid = saveUserAndGetId(item);
        if (StringUtils.isEmpty(guid)) {
            return null;
        }
        Granted granted = new Granted();
        granted.setUserId(guid);
        granted.setAssetId(asset.getId());
        granted.setType(GrantType.ASSET.getValue());
        return granted;
    }

    /**
     * 授权到账号类型的用户,入库
     *
     * @param item
     */
    private Granted intoDbForAccountType(UserItem item) throws Exception {
        // 查询出设备信息
        String assetName = item.getAssetName();
        String assetIp = item.getAssetIp();
        if (StringUtils.isEmpty(assetName) || StringUtils.isEmpty(assetIp)) {
            warnOrError(String.format("设备名称和设备ip均不能为空,assetName: %s, assetIp: %s", assetName, assetIp));
            return null;
        }
        List<Asset> assetList = assetService.list(new QueryWrapper<Asset>().eq("ip", assetIp).eq("name", assetName));
        if (CollectionUtils.isEmpty(assetList)) {
            warnOrError(String.format("设备信息不存在: %s", item.toString()));
            return null;
        }
        if (assetList.size() != 1) {
            logger.warn("有多条ip相同且名称相同的设备,ip: {},name: {}", assetIp, assetName);
        }
        Asset asset = assetList.get(0);
        // 查询出设备账号信息
        String account = item.getAccount();
        List<AssetAccount> accountList = assetAccountService.list(new QueryWrapper<AssetAccount>().eq("username", account).eq("asset_id", asset.getId()));
        if (CollectionUtils.isEmpty(accountList)) {
            warnOrError(String.format("账号信息不存在,设备id: %d, 导入信息: %s", asset.getId(), item.toString()));
            return null;
        }
        if (accountList.size() != 1) {
            logger.warn("同一设备下存在多个同名账号,设备id: {}, 账号名称: {}", asset.getId(), account);
        }
        AssetAccount assetAccount = accountList.get(0);
        // 将用户信息入库,并获取用户guid
        String guid = saveUserAndGetId(item);
        if (StringUtils.isEmpty(guid)) {
            return null;
        }
        Granted granted = new Granted();
        granted.setUserId(guid);
        granted.setAssetId(asset.getId());
        granted.setAccountId(assetAccount.getId());
        granted.setType(GrantType.ACCOUNT.getValue());
        return granted;
    }

    /**
     * 将用户信息入库,并获取用户guid
     *
     * @param item
     * @return
     */
    private String saveUserAndGetId(UserItem item) throws Exception {
        // 检查用户组是否存在,
        List<Integer> list = checkUserGroup(item.getUserGroupName());
        if (CollectionUtils.isEmpty(list)) {
            warnOrError(String.format("用户组不存在: %s", item.getUserGroupName()));
            return null;
        }
        String guid = UUID.randomUUID().toString();
        User user = new User();
        user.setGuid(guid);
        user.setUsername(item.getUsername());
        user.setName(item.getName());
        user.setPassword(ConstantsDto.DEFAULT_PWD);
        user.setAuthenticationMethod(AuthMethod.PWD.code());
        user.setStatus(UserStatus.ENABLE.code());
        userService.save(user);
        // 保存用户用户组中间表信息
        for (Integer id : list) {
            List<UserUsergroup> userUsergroupList = userUsergroupService.list(new QueryWrapper<UserUsergroup>().eq("user_guid", guid).eq("usergroup_id", id));
            if (CollectionUtils.isEmpty(userUsergroupList)) {
                userUsergroupService.save(new UserUsergroup(guid, id));
            }
        }
        // 保存用户角色中间表
        UserRole userRole = new UserRole(guid, ConstantsDto.ROLE_USER_PID);
        userRoleService.save(userRole);
        return guid;
    }

    public void setRule(ImportRule rule) {
        this.rule = rule;
    }

    /**
     * 检查用户组是否正确
     *
     * @param userGroupName 用户组名称,如果多个用户组名称,用英文逗号(,)隔开
     */
    private List<Integer> checkUserGroup(String userGroupName) throws Exception {
        if (StringUtils.isEmpty(userGroupName)) {
            throw new Exception("userGroupName can not be null");
        }
        if (!userGroupName.contains(",")) {
            List<Usergroup> list = usergroupService.list(new QueryWrapper<Usergroup>().eq("name", userGroupName));
            if (CollectionUtils.isEmpty(list)) {
                warnOrError(String.format("用户组不存在: %s", userGroupName));
                return new ArrayList<>(0);
            }
            if (list.size() != 1) {
                logger.warn("存在多个同名的用户组: {}", userGroupName);
            }
            return list.stream().map(Usergroup::getId).collect(Collectors.toList());
        }
        String[] split = userGroupName.split(",");
        List<Usergroup> list = usergroupService.list(new QueryWrapper<Usergroup>().in("name", Arrays.asList(split)));
        if (CollectionUtils.isEmpty(list)) {
            warnOrError(String.format("用户组不存在: %s", userGroupName));
            return new ArrayList<>(0);
        }
        return list.stream().map(Usergroup::getId).collect(Collectors.toList());
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

    //region 导出
    @Override
    public void export(Builder<HSSFWorkbook, User> builder) throws Exception {
        HSSFWorkbook workbook = builder.getT();
        if (Objects.isNull(workbook)) {
            throw new Exception("HSSFWorkbook can not be null");
        }
        HSSFSheet sheet = workbook.createSheet(sheetName());
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(15);

        List<User> list = builder.getList();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("用户列表为空");
            return;
        }
        // 绘制头部
        List<IHeader> headerList = headerProvider.provide(UserItem.class);
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

        // 绘制具体内容
        List<UserItem> collect = list.stream()
                .flatMap((Function<User, Stream<UserItem>>) user -> parse2UserItem(user).stream())
                .collect(Collectors.toList());
        // 从第一行开始画
        int rowNum = 1;
        HSSFCellStyle contentStyle = cellStyleProvider.provide(new CellStyleProvider.Param(workbook, CellStyleProvider.Type.CONTENT));
        for (UserItem item : collect) {
            HSSFRow row = sheet.createRow(rowNum);
            drawLine(item, row, contentStyle);
            ++rowNum;
        }
    }

    private void drawLine(UserItem item, HSSFRow row, HSSFCellStyle contentStyle) throws Exception {
        Class<? extends UserItem> clazz = item.getClass();
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


    private List<UserItem> parse2UserItem(User user) {
        if (Objects.isNull(user) || StringUtils.isEmpty(user.getGuid())) {
            return new ArrayList<>(0);
        }
        String guid = user.getGuid();
        List<Usergroup> usergroupList = usergroupService.getUsergroupListByUserGuid(guid);
        String userGroupName = usergroupList.stream().map(Usergroup::getName).collect(Collectors.joining(","));
        List<Granted> grantedList = grantedService.findGrantedListByUserIdAndStatus(guid, 1);
        List<UserItem> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(grantedList)) {
            UserItem item = new UserItem();
            item.setName(user.getName());
            item.setUsername(user.getUsername());
            item.setUserGroupName(userGroupName);
            item.setGrantType(GrantType.NONE);
            list.add(item);
        } else {
            for (Granted granted : grantedList) {
                UserItem item = new UserItem();
                item.setName(user.getName());
                item.setUsername(user.getUsername());
                item.setUserGroupName(userGroupName);
                item.setGrantType(GrantType.matchValue(granted.getType()));
                item.setAssetGroupName(granted.getAssetgroupname());
                item.setAssetName(granted.getAssetname());
                item.setAssetIp(granted.getAssetip());
                item.setAccount(granted.getAccountname());
                item.setTrusteeship(granted.getTrusteeship());
                list.add(item);
            }
        }
        return list;
    }
    //endregion
}
