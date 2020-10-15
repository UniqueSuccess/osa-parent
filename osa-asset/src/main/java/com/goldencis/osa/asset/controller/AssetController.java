package com.goldencis.osa.asset.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.asset.domain.AssetCount;
import com.goldencis.osa.asset.entity.*;
import com.goldencis.osa.asset.excel.ExcelUtil;
import com.goldencis.osa.asset.excel.IExport;
import com.goldencis.osa.asset.excel.ImportRule;
import com.goldencis.osa.asset.excel.impl.AssetExcelHelper;
import com.goldencis.osa.asset.excel.impl.AssetGroupExcelHelper;
import com.goldencis.osa.asset.excel.impl.UserExcelHelper;
import com.goldencis.osa.asset.excel.impl.UserGroupExcelHelper;
import com.goldencis.osa.asset.excel.provide.impl.FileProvider;
import com.goldencis.osa.asset.params.AssetAccountParams;
import com.goldencis.osa.asset.params.AssetParams;
import com.goldencis.osa.asset.service.*;
import com.goldencis.osa.common.config.Config;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.FileDownLoad;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.core.aop.OsaSystemLog;
import com.goldencis.osa.core.entity.Dictionary;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.IDictionaryService;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.core.utils.AuthUtils;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.core.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备模块,前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-10-25
 */
@Api(tags = "设备管理")
@RestController
@RequestMapping("/asset")
public class AssetController implements ServletContextAware {

    private final Logger logger = LoggerFactory.getLogger(AssetController.class);
    private ServletContext servletContext;
    @Autowired
    private Config config;
    @Autowired
    private IAssetService assetService;
    @Autowired
    private IAssetAccountService assetAccountService;
    @Autowired
    private IAssetTypeService assetTypeService;
    @Autowired
    private IDictionaryService dictionaryService;
    @Autowired
    private ISsoRuleService ssoRuleService;
    @Autowired
    private ISsoRuleAttrService ssoRuleAttrService;
    @Autowired
    private IUserService userService;
    @Autowired
    private FileProvider fileProvider;
    @Autowired
    private UserExcelHelper userExcelHelper;
    @Autowired
    private UserGroupExcelHelper userGroupExcelHelper;
    @Autowired
    private AssetExcelHelper assetExcelHelper;
    @Autowired
    private AssetGroupExcelHelper assetGroupExcelHelper;

    @Override
    public void setServletContext(ServletContext sc) {
        this.servletContext = sc;
    }

    //region 设备CRUD,导出
    @ApiOperation("获取设备分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "searchStr", value = "过滤条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "groupId", value = "设备组id,多个id用,隔开", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "assetType", value = "设备类型id,多个id之间用,隔开", dataType = "String", paramType = "query"),
    })
    @GetMapping(value = "/getAssetsInPage")
    public ResultMsg getAssetsInPage(@ModelAttribute AssetParams params) {
        try {
            //获取分页参数
            IPage<Asset> page = QueryUtils.paresParams2Page(params);
            //分页查询
            assetService.getAssetsInPage(page, params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation("根据设备id获取设备详情")
    @ApiImplicitParam(name = "id", value = "设备id", required = true, paramType = "path", dataTypeClass = Integer.class)
    @GetMapping(value = "/asset/{id}")
    public ResultMsg getAssetDetailById(@PathVariable Integer id) {
        if (Objects.isNull(id)) {
            return ResultMsg.error("设备id不能为空");
        }
        try {
            Asset asset = assetService.getAssetDetailById(id);
            return ResultMsg.ok(asset);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation("保存设备信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备id", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "ip", value = "设备ip地址", paramType = "query", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "name", value = "设备名称", paramType = "query", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "type", value = "设备类型id", paramType = "query", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(name = "encode", value = "设备编码", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "groupId", value = "所属设备组id", paramType = "query", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(name = "remark", value = "备注", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "account", value = "管理账号", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "password", value = "管理账号的密码", paramType = "query", dataTypeClass = String.class)
    })
    @PostMapping(value = "/asset")
    @OsaSystemLog(module = "新建设备", template = "设备名称：%s", args = "0.name", type = LogType.SYSTEM_ADD)
    public ResultMsg save(@RequestBody Asset asset) {
        // ip不能为空
        if (Objects.isNull(asset)) {
            return ResultMsg.error("参数不正确");
        }
        try {
            int authNumber = getAuthNumber();
            int count = assetService.count(null);
            if (authNumber <= count) {
                return ResultMsg.False("授权点数不足");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }

        return this.saveOrUpdateAsset(asset);
    }

    @ApiOperation("更新设备信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备id", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "ip", value = "设备ip地址", paramType = "query", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "name", value = "设备名称", paramType = "query", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "type", value = "设备类型id", paramType = "query", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(name = "encode", value = "设备编码", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "groupId", value = "所属设备组id", paramType = "query", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(name = "remark", value = "备注", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "account", value = "管理账号", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "password", value = "管理账号的密码", paramType = "query", dataTypeClass = String.class)
    })
    @PutMapping(value = "/asset")
    @OsaSystemLog(module = "编辑设备", template = "设备名称：%s", args = "0.name", type = LogType.SYSTEM_UPDATE)
    public ResultMsg update(@RequestBody Asset asset) {
        // ip不能为空
        if (Objects.isNull(asset)) {
            return ResultMsg.error("参数不正确");
        }
        return this.saveOrUpdateAsset(asset);
    }

    private ResultMsg saveOrUpdateAsset(Asset asset) {
        try {
            AtomicReference<String> reference = new AtomicReference<>();
            assetService.saveOrUpdateAsset(asset, reference::set);
            ResultMsg ok = ResultMsg.ok();
            if (!StringUtils.isEmpty(reference.get())) {
                ok.setResultMsg(reference.get());
            } else {
                ok.setResultMsg("修改设备信息成功!");
            }
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    /**
     * 根据id删除指定的设备
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据设备id删除对应的设备")
    @ApiImplicitParam(name = "id", value = "设备id", required = true, paramType = "path", dataType = "Integer")
    @DeleteMapping("/asset/{id}")
    public ResultMsg delete(@PathVariable Integer id) {
        if (Objects.isNull(id)) {
            return ResultMsg.error("设备id不能为空");
        }
        try {
            AtomicReference<String> reference = new AtomicReference<>();
            assetService.deleteAssetById(id, reference::set);
            ResultMsg ok = ResultMsg.ok();
            if (StringUtils.isEmpty(reference.get())) {
                ok.setResultMsg("删除成功!");
            } else {
                ok.setResultMsg(reference.get());
            }
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "根据设备id删除一批设备")
    @ApiImplicitParam(name = "ids", value = "设备id字符串,多个id用,隔开", required = true, paramType = "query", dataType = "String")
    @DeleteMapping(value = "/asset/delete")
    public ResultMsg delete(String ids) {
        if (ids == null) {
            return ResultMsg.error("设备id不能为空");
        }
        try {
            List<Integer> list = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            String assetNames = assetService.deleteAssetsByIdList(list);
            ResultMsg ok = ResultMsg.ok();
            ok.setResultMsg("删除成功!");
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取设备类型")
    @GetMapping(value = "/getAssetType")
    public ResultMsg getAssetTypeList() {
        try {
            List<AssetType> list = assetTypeService.getPartAssetTypeList();
            return ResultMsg.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取设备类型树")
    @GetMapping(value = "/getAssetTypeTree")
    public Object getAssetTypeTree() {
        return assetTypeService.getEnabledAssetTypeList();
    }

    @ApiOperation(value = "获取应用程序发布器列表")
    @GetMapping(value = "/publishList")
    public ResultMsg getPublishList() {
        try {
            List<Asset> list = assetService.getPublishList();
            return ResultMsg.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "根据设备id获取运维工具列表")
    @ApiImplicitParam(name = "assetId", value = "设备id", paramType = "path", dataType = "Integer")
    @GetMapping(value = "/getOperationToolList/{assetId}")
    public ResultMsg getOperationToolList(@PathVariable Integer assetId) {
        if (Objects.isNull(assetId)) {
            return ResultMsg.error("设备id不能为空");
        }
        try {
            List<SsoRule> list = ssoRuleService.getSSORuleListByAssetId(assetId);
            return ResultMsg.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    /**
     * 导出
     */
    @ApiOperation("导出设备列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchStr", value = "过滤条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "groupId", value = "设备组id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "assetType", value = "设备类型id,多个id之间用,隔开", dataType = "String", paramType = "query"),
    })
    @GetMapping(value = "/export")
    public void export(@ModelAttribute AssetParams params, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (Objects.isNull(params.getStart())) {
                params.setStart(0);
            }
            if (Objects.isNull(params.getLength())) {
                params.setLength(Integer.MAX_VALUE);
            }
            //获取分页参数
            IPage<Asset> page = QueryUtils.paresParams2Page(params);
            assetService.getAssetsInPage(page, params);
            List<Asset> list = page.getRecords();

            HSSFWorkbook workbook = new HSSFWorkbook();
            IExport.Builder<HSSFWorkbook, Asset> assetBuilder = new IExport.Builder<>();
            assetBuilder.setList(list);
            assetBuilder.setT(workbook);
            assetExcelHelper.export(assetBuilder);
            assetGroupExcelHelper.export(workbook);
            FileProvider.Builder<HSSFWorkbook> builder = new FileProvider.Builder<>();
            builder.setPath(config.getExportPath());
            builder.setFileName("设备信息表" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            builder.setSuffix(".xls");
            builder.setInput(workbook);
            File file = fileProvider.provide(builder);
            FileDownLoad downLoad = new FileDownLoad();
            downLoad.download(response, request, file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("下载设备导入模板")
    @GetMapping(value = "/import/template")
    public void downloadAssetTemplate(HttpServletRequest request, HttpServletResponse response) {
        try {
            URL resource = getClass().getResource("/static/data/设备导入模板.xls");
            File file = Paths.get(resource.toURI()).toFile();
            new FileDownLoad().download(response, request, file.getAbsolutePath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private int getAuthNumber() throws Exception {
        if (ConstantsDto.ISCHECKAUTH != ConstantsDto.CONST_TRUE) {
            return Integer.MAX_VALUE;
        }
        Map<String, Object> authInfo = AuthUtils.getAuthInfo(servletContext);
        if (!org.apache.commons.lang.StringUtils.isEmpty(authInfo.get("authmsg").toString())) {
            throw new Exception(authInfo.get("authmsg").toString());
        }
        // 获取授权数量
        return null != authInfo.get("authDeviceNum") ? Integer.parseInt(authInfo.get("authDeviceNum").toString()) : 0;
    }

    @PostMapping(value = "/import", produces = "application/json")
    public ResultMsg importAssetFromExcel(@RequestParam(value = "file", required = false) MultipartFile file,
                                          @RequestParam("flag") String flag,
                                          HttpServletRequest request) {
        User user = SecurityUtils.getCurrentUser();
        if (Objects.isNull(user)) {
            return ResultMsg.error("获取当前登录用户失败");
        }
        if (!ConstantsDto.USER_SYSTEM_ID.equals(user.getGuid())) {
            return ResultMsg.False("您没有权限导入设备");
        }
        System.out.println("flag : " + flag);
        ImportRule rule;
        try {
            rule = ImportRule.valueOf(flag);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
        // 校验文件
        String fileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(fileName) || !fileName.endsWith(ExcelUtil.EXCEL_XLS)) {
            return ResultMsg.False("不支持的文件格式,目前仅支持xls");
        }
        HSSFWorkbook workbok = null;
        try {
            String path = request.getSession().getServletContext().getRealPath(ConstantsDto.UPLOAD_PATH);
            File parent = new File(path);
            if (!parent.exists()) {
                parent.mkdirs();
            }
            File targetFile = new File(parent, fileName);
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            // 保存
            file.transferTo(targetFile);
            String filePath = path + File.separator + fileName;
            workbok = (HSSFWorkbook) ExcelUtil.getWorkbok(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Objects.isNull(workbok)) {
            return ResultMsg.False("文件校验失败");
        }

        // 获取授权数量
        int authDeviceNum = 0;
        try {
            authDeviceNum = getAuthNumber();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
        logger.debug("authDeviceNum : {}", authDeviceNum);
        int total = assetService.count(null);
        if (total >= authDeviceNum) {
            return ResultMsg.False(ConstantsDto.AUTH_NUM_ERROR);
        }
        try {
            assetExcelHelper.setRule(rule);
            assetExcelHelper.setAuthNumber(authDeviceNum);
            assetExcelHelper.in(workbok);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation("导出用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "usergroupPid", value = "父级用户组", dataType = "Integer"),
            @ApiImplicitParam(name = "searchStr", value = "用户名/姓名查询", dataType = "String"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String"),
            @ApiImplicitParam(name = "authenticationMethod", value = "认证方式的筛选条件", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态的筛选条件", dataType = "String")
    })
    @GetMapping(value = "/export/user")
    public void exportUser(@RequestParam Map<String, String> params,
                           HttpServletRequest request, HttpServletResponse response) {
        try {
            //导出不需要分页
            params.put("start", "0");
            params.put("length", String.valueOf(Integer.MAX_VALUE));
            // 查询用户列表
            IPage<User> page = userService.getUsersInPage(params);
            HSSFWorkbook workbook = new HSSFWorkbook();
            IExport.Builder<HSSFWorkbook, User> userBuilder = new IExport.Builder<>();
            userBuilder.setList(page.getRecords());
            userBuilder.setT(workbook);
            userExcelHelper.export(userBuilder);
            userGroupExcelHelper.export(workbook);
            FileProvider.Builder<HSSFWorkbook> builder = new FileProvider.Builder<>();
            builder.setPath(config.getExportPath());
            builder.setFileName("员工信息表" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            builder.setSuffix(".xls");
            builder.setInput(workbook);
            File file = fileProvider.provide(builder);
            FileDownLoad fileDownLoad = new FileDownLoad();
            fileDownLoad.download(response, request, file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("下载用户导入模板")
    @GetMapping(value = "/import/user/template")
    public void downloadUserTemplate(HttpServletRequest request, HttpServletResponse response) {
        try {
            URL resource = getClass().getResource("/static/data/用户导入模板.xls");
            File file = Paths.get(resource.toURI()).toFile();
            new FileDownLoad().download(response, request, file.getAbsolutePath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @PostMapping(value = "/import/user", produces = "application/json")
    public ResultMsg importUserFromExcel(@RequestParam(value = "file", required = false) MultipartFile file,
                                         @RequestParam("flag") String flag,
                                         HttpServletRequest request) {
        System.out.println("flag : " + flag);
        ImportRule rule;
        try {
            rule = ImportRule.valueOf(flag);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
        String fileName = file.getOriginalFilename();
        String path = request.getSession().getServletContext().getRealPath(ConstantsDto.UPLOAD_PATH);
        File parent = new File(path);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        // 保存
        try {
            File targetFile = new File(path, fileName);
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            file.transferTo(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
        String filePath = path + File.separator + fileName;
        try {
            HSSFWorkbook workbok = (HSSFWorkbook) ExcelUtil.getWorkbok(new File(filePath));
            userExcelHelper.setRule(rule);
            userExcelHelper.in(workbok);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    /**
     * 首页,资源数量
     * @return
     */
    @ApiOperation(value = "首页-资源数量")
    @GetMapping(value = "/infoForHomePage")
    public ResultMsg infoForHomePage() {
        try {
            List<AssetCount> map = assetService.infoForHomePage();
            int count = assetService.count(null);
            ResultMsg ok = ResultMsg.ok();
            ok.setData(map);
            ok.setTotal(count);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    /**
     * 首页-本周运维次数
     * @return
     */
    @ApiOperation(value = "首页-本周运维次数")
    @GetMapping(value = "/getHomeAssetsWeek")
    public ResultMsg getHomeAssetsWeek() {
        try {
            List<HomeAssetsWeek> homeAssetsWeek = assetService.getHomeAssetsWeek();
            return ResultMsg.ok(homeAssetsWeek);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }
    //endregion

    //region 设备账号CRUD

    /**
     * 根据设备id获取账号列表信息
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "根据设备id获取账号列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "searchStr", value = "过滤条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "assetId", value = "设备id", required = true, dataType = "Integer", paramType = "query")
    })
    @GetMapping(value = "/assetAccount/getAssetAccountsInPage")
    public ResultMsg getAccountByAssetId(@ModelAttribute AssetAccountParams params) {
        if (params == null || params.getAssetId() == null) {
            return ResultMsg.error("参数不正确");
        }
        try {
            IPage<AssetAccount> page = QueryUtils.paresParams2Page(params);
            assetAccountService.getAssetAccountsInPage(page, params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "新增账号信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "账号id,新增时不需要传,更新时必传", required = false, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "assetId", value = "设备id", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "username", value = "账号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码(加密)", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "accountType", value = "账号类型(管理:admin,普通:normal)", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "trusteeship", value = "是否托管(是:1,否:0)", required = true, paramType = "query", dataType = "Integer")
    })
    @PostMapping(value = "/assetAccount")
    @OsaSystemLog(module = "新建设备账号", template = "设备账号名：%s", args = "0.username", type = LogType.SYSTEM_ADD)
    public ResultMsg saveAccountInfo(AssetAccount account) {
        if (account == null || account.getAssetId() == null) {
            return ResultMsg.error("参数不正确");
        }
        return this.saveOrUpdateAccountInfo(account);
    }

    @ApiOperation(value = "更新账号信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "账号id,新增时不需要传,更新时必传", required = false, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "assetId", value = "设备id", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "username", value = "账号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码(加密)", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "accountType", value = "账号类型(管理:admin,普通:normal)", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "trusteeship", value = "是否托管(是:1,否:0)", required = true, paramType = "query", dataType = "Integer")
    })
    @PutMapping(value = "/assetAccount")
    @OsaSystemLog(module = "编辑设备账号",  template = "设备账号名：%s", args = "0.username",type = LogType.SYSTEM_UPDATE)
    public ResultMsg updateAccountInfo(AssetAccount account) {
        if (account == null || account.getAssetId() == null) {
            return ResultMsg.error("参数不正确");
        }
        return this.saveOrUpdateAccountInfo(account);
    }

    private ResultMsg saveOrUpdateAccountInfo(AssetAccount account) {
        try {
            assetAccountService.saveOrUpdateAccountInfo(account);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取设备账号信息")
    @ApiImplicitParam(name = "id", value = "设备账号id", required = true, paramType = "path", dataType = "Integer")
    @GetMapping(value = "assetAccount/{id}")
    public Object getAssetAccountById(@PathVariable Integer id) {
        if (Objects.isNull(id)) {
            return ResultMsg.error("设备账号id不能为空");
        }
        try {
            return assetAccountService.getById(id);
        } catch (Exception e) {
            return ResultMsg.error(e.getMessage());
        }
    }

    /**
     * 删除设备账号,
     * 目前弃用
     * @param id
     * @return
     */
    @Deprecated
    @ApiOperation(value = "删除设备账号信息")
    @ApiImplicitParam(name = "id", value = "设备账号id", required = true, paramType = "path", dataType = "Integer")
    @DeleteMapping(value = "assetAccount/{id}")
    public ResultMsg deleteAssetAccountById(@PathVariable Integer id) {
        if (Objects.isNull(id)) {
            return ResultMsg.error("设备账号id不能为空");
        }
        try {
            int status = assetAccountService.deleteAccountById(id);
            String msg = null;
            if (status == -1) {
                msg = "提交审批成功";
            }
            ResultMsg ok = ResultMsg.ok();
            ok.setResultMsg(msg);
            return ok;
        } catch (Exception e) {
            return ResultMsg.error(e.getMessage());
        }
    }

    /**
     * 批量删除设备账号,
     * 目前弃用
     * @param ids
     * @return
     */
    @Deprecated
    @ApiOperation(value = "根据id删除一批设备账号信息")
    @ApiImplicitParam(name = "id", value = "设备账号id,多个id用,隔开", required = true, paramType = "query", dataType = "String")
    @DeleteMapping(value = "assetAccount/delete")
    public ResultMsg deleteAssetAccountByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return ResultMsg.error("设备账号id不能为空");
        }
        try {
            List<Integer> list = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            int status = assetAccountService.deleteAccountById(list);
            String msg = null;
            if (status == -1) {
                msg = "提交审批成功";
            }
            ResultMsg ok = ResultMsg.ok();
            ok.setResultMsg(msg);
            return ok;
        } catch (Exception e) {
            return ResultMsg.error(e.getMessage());
        }
    }
    //endregion

    //region SSO(单点登录)相关
    @ApiOperation(value = "获取SSO规则类型")
    @GetMapping(value = "/sso/ruleType")
    public ResultMsg getSSORuleType() {
        try {
            List<Dictionary> list = dictionaryService.getAllSSORuleType();
            return ResultMsg.ok(list);
        } catch (Exception e) {
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取SSO规则属性")
    @GetMapping(value = "/sso/ruleAttr")
    public ResultMsg getSSORuleAttr() {
        try {
            List<SsoRuleAttr> list = ssoRuleAttrService.list(null);
            return ResultMsg.ok(list);
        } catch (Exception e) {
            return ResultMsg.error(e.getMessage());
        }
    }

    /**
     * 导入应用程序发布规则
     *
     * @return
     */
    @PostMapping(value = "/import/rule", produces = "application/json")
    public ResultMsg importSSORule(@RequestParam(value = "file", required = false) MultipartFile file,
                                   HttpServletRequest request) {
        String fileName = file.getOriginalFilename();
        String path = request.getSession().getServletContext().getRealPath(ConstantsDto.UPLOAD_PATH);
        File parent = new File(path);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        // 保存
        try {
            File targetFile = new File(path, fileName);
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            file.transferTo(targetFile);
            SsoRule ssoRule = ssoRuleService.importSSORuleFromFile(targetFile);
            return ResultMsg.ok(ssoRule);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation("下载发布规则的导入模板")
    @GetMapping(value = "/import/rule/template")
    public void downloadSSORuleTemplate(HttpServletRequest request, HttpServletResponse response) {
        try {
            URL resource = getClass().getResource("/static/data/规则模板.zip");
            File file = Paths.get(resource.toURI()).toFile();
            new FileDownLoad().download(response, request, file.getAbsolutePath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //endregion
}
