package com.goldencis.osa.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.common.config.ClientConfig;
import com.goldencis.osa.common.entity.MailConfig;
import com.goldencis.osa.common.entity.MailInfo;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.FileUpload;
import com.goldencis.osa.common.utils.JsonUtils;
import com.goldencis.osa.common.utils.MailManager;
import com.goldencis.osa.core.entity.Platform;
import com.goldencis.osa.system.domain.LogServer;
import com.goldencis.osa.system.domain.AccessControl;
import com.goldencis.osa.core.params.UkeyParams;
import com.goldencis.osa.system.entity.ApprovalExpire;
import com.goldencis.osa.system.entity.SystemSet;
import com.goldencis.osa.core.entity.Ukey;
import com.goldencis.osa.system.service.ISystemSetService;
import com.goldencis.osa.core.service.IUkeyService;
import com.goldencis.osa.system.utils.ClientUpdateUtil;
import com.goldencis.osa.system.utils.ComputerInfoUtil;
import com.goldencis.osa.system.utils.SystemCons;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author shigd
 * @since 2018-12-27
 */
@Api(tags = "系统-系统配置")
@RestController
@RequestMapping("/system/systemSet")
public class SystemSetController {

    @Autowired
    private ISystemSetService systemSetService;

    @Autowired
    private ClientConfig clientConfig;
    @Autowired
    private IUkeyService ukeyService;
    @Autowired
    private MailManager mailManager;

    @ApiOperation(value = "上传客户端、跳板机的安装包、升级包")
    @ApiImplicitParam(name = "packageType", value = "上传的文件参数类型", dataType = "Integer", paramType = "query")
    @PostMapping(value = "/uploadClientPackage")
    public ResultMsg uploadClientPackage(Integer packageType, @RequestParam(value = "packageFile", required = false) MultipartFile packageFile) {
        try {
            if (packageType == null || (packageType < SystemCons.CLIENT_PACKAGE || packageType > SystemCons.BRIDGE_UPDATE)) {
                ResultMsg.False("错误的文件参数类型！");
            }

            //根据packageType获取实际的上传文件名称以及路径
            String dirPath;
            String filename;

            if (packageType == SystemCons.CLIENT_PACKAGE) {
                dirPath = clientConfig.getPackageuploadPath();
                filename = clientConfig.getPackageuploadFileName();
            } else if (packageType == SystemCons.CLIENT_PACKAGE_XP) {
                dirPath = clientConfig.getPackageuploadPath();
                filename = clientConfig.getXpPackageuploadFileName();
            } else if (packageType == SystemCons.CLIENT_UPDATE) {
                dirPath = clientConfig.getClientupdateuploadPath();
                filename = clientConfig.getClientupdateuploadFileName();
                //备份升级文件夹
                systemSetService.backupFolder(dirPath);
            } else if (packageType == SystemCons.BRIDGE_PACKAGE) {
                dirPath = clientConfig.getPackageuploadPath();
                filename = clientConfig.getBridgeuploadFileName();
            } else if (packageType == SystemCons.BRIDGE_UPDATE) {
                dirPath = clientConfig.getBridgeupdateuploadPath();
                filename = clientConfig.getBridgeupdateuploadFileName();
                //备份升级文件夹
                systemSetService.backupFolder(dirPath);
            } else {
                return ResultMsg.False("上传文件类型错误!");
            }

            FileUpload fileUploader = new FileUpload();
            fileUploader.uploadFile(packageFile, dirPath, filename);

            //如果上传的是升级包，在控制台上传升级包后，调用脚本解压升级包
            if (packageType == SystemCons.CLIENT_UPDATE || packageType == SystemCons.BRIDGE_UPDATE) {
                ClientUpdateUtil.executeClientUpdate(dirPath, filename);
            }

            return ResultMsg.ok("上传文件成功");
        } catch (Exception e) {
            return ResultMsg.error("上传文件错误");
        }
    }

    @ApiOperation(value = "获取管控平台信息")
    @GetMapping(value = "/platform")
    public ResultMsg platform() {
        try {
            SystemSet set = systemSetService.getOne(new QueryWrapper<SystemSet>().eq("code", "Platform"));
            if (Objects.isNull(set)) {
                return ResultMsg.False("获取管控平台数据失败");
            }
            Platform platform = JsonUtils.jsonToPojo(set.getContent(), Platform.class);
            System.out.println(JsonUtils.objectToJson(platform));
            return ResultMsg.ok(platform);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取密码限制信息")
    @GetMapping(value = "/platform/security")
    public ResultMsg security() {
        try {
            SystemSet set = systemSetService.getOne(new QueryWrapper<SystemSet>().eq("code", "Platform"));
            if (Objects.isNull(set)) {
                return ResultMsg.False("获取管控平台数据失败");
            }
            Platform platform = JsonUtils.jsonToPojo(set.getContent(), Platform.class);
            if (Objects.isNull(platform)) {
                return ResultMsg.False("json解析出错");
            }
            return ResultMsg.ok(platform.getSecurity());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取系统名称")
    @GetMapping(value = "/platform/systemName")
    public ResultMsg systemName() {
        try {
            SystemSet set = systemSetService.getOne(new QueryWrapper<SystemSet>().eq("code", "Platform"));
            if (Objects.isNull(set)) {
                return ResultMsg.False("获取管控平台数据失败");
            }
            Platform platform = JsonUtils.jsonToPojo(set.getContent(), Platform.class);
            if (Objects.isNull(platform)) {
                return ResultMsg.False("json解析出错");
            }
            String systemName = platform.getCustom().getSystemName();
            if (StringUtils.isEmpty(systemName)) {
                systemName = "osa";
            }
            return ResultMsg.ok(systemName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "保存管控平台信息")
    @PostMapping(value = "/platform/save", produces = "application/json")
    public ResultMsg platform(@RequestBody Platform platform) {
        if (Objects.isNull(platform)) {
            return ResultMsg.error("参数不能为空");
        }

        try {
            String json = systemSetService.convertPlatformToJson(platform);
            systemSetService.updatePlatformJson(json);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取计算机信息")
    @GetMapping(value = "/computerInfo")
    public ResultMsg getComputerInfo() {
        try {
            Map<String, Object> resMap = new HashMap<>();

            double cpu = ComputerInfoUtil.cpu();
            resMap.put("cpu", cpu);
            Map memory = ComputerInfoUtil.memory();
            resMap.put("memory", memory);
            Double mySQLUsage = ComputerInfoUtil.mySQLUsage();
            resMap.put("mySQLUsage", mySQLUsage);
            Map dbUsage = ComputerInfoUtil.dbUsage();
            resMap.put("dbUsage", dbUsage);

            return ResultMsg.ok(resMap);
        } catch (Exception e) {
            return ResultMsg.error("获取计算机信息错误！");
        }
    }

    @ApiOperation(value = "获取第三方日志服务器信息")
    @GetMapping(value = "/serverInfo")
    @SuppressWarnings("unchecked")
    public ResultMsg getLogserverInfo() {
        try {
            SystemSet logSet = systemSetService.getOne(new QueryWrapper<SystemSet>().eq("code", "LogServer"));
            if (Objects.isNull(logSet)) {
                return ResultMsg.False("获取第三方日志服务器信息失败");
            }
            SystemSet mailSet = systemSetService.getOne(new QueryWrapper<SystemSet>().eq("code", ISystemSetService.MAIL_CONFIG));
            if (Objects.isNull(mailSet)) {
                return ResultMsg.False("获取邮件服务器信息失败");
            }

            Map<String, Objects> map = new HashMap<>();

            Map<String, Objects> logMap = JsonUtils.jsonToPojo(logSet.getContent(), Map.class);
            map.putAll(logMap);

            Map<String, Objects> mailMap = JsonUtils.jsonToPojo(mailSet.getContent(), Map.class);
            map.putAll(mailMap);

            return ResultMsg.ok(map);
        } catch (Exception e) {
            return ResultMsg.error("获取服务器信息错误！");
        }
    }

    @ApiOperation(value = "保存第三方日志服务器和邮件服务器信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "日志服务器名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "url", value = "日志服务器地址", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "serverAddress", value = "邮件服务器地址", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "port", value = "邮件服务器端口", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "from", value = "发送邮箱", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "发送邮箱的密码", dataType = "String", paramType = "query"),
    })
    @PostMapping(value = "/serverInfo")
    public ResultMsg saveServerInfo(LogServer logserver, MailConfig mailConfig) {
        try {
            if (StringUtils.isEmpty(logserver.getName()) || StringUtils.isEmpty(logserver.getUrl())) {
                return ResultMsg.error("参数不能为空");
            }

            //保存第三方日志服务器信息
            systemSetService.updateLogServer(logserver);

            // 保存邮件服务器信息
            systemSetService.updateMailConfig(mailConfig);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error("保存服务器信息失败！");
        }
    }

    @ApiOperation(value = "测试邮件服务器配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverAddress", value = "邮件服务器地址", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "port", value = "邮件服务器端口", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "from", value = "发送邮箱", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "发送邮箱的密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "to", value = "收件箱", dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/mail/test")
    public ResultMsg testMailConfig(MailConfig config, String to) {
        MailInfo info = new MailInfo();
        info.addTo(to);
        info.setSubject("测试");
        info.setContent("这是一封测试邮件");
        try {
            boolean send = mailManager.send(info, config);
            return send ? ResultMsg.ok() : ResultMsg.False("邮件发送失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取网口状态信息")
    @GetMapping(value = "/netStatInfo")
    public ResultMsg getNetStatInfo() {
        try {
            //获取网口状态信息
            Map<String, Object> netstat = systemSetService.getNetStat();

            return ResultMsg.ok(netstat);
        } catch (Exception e) {
            return ResultMsg.error("获取网口状态信息错误！");
        }
    }

    @ApiOperation(value = "上传背景图片")
    @PostMapping(value = "/background/upload", produces = "application/json")
    public ResultMsg upload(@RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            URL resource = getClass().getResource("/static/images/bg_img.png");
            File target = Paths.get(resource.toURI()).toFile();
            file.transferTo(target);
            return ResultMsg.ok();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "还原背景图片")
    @GetMapping(value = "/background/restore")
    public ResultMsg restore() {
        try {
            File backup = Paths.get(getClass().getResource("/static/images/backup/bg_img.png").toURI()).toFile();
            File target = Paths.get(getClass().getResource("/static/images/bg_img.png").toURI()).toFile();
            Files.copy(backup.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return ResultMsg.ok();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取审批参数配置")
    @GetMapping(value = "/platform/getApprovalExpire")
    public ResultMsg getApprovalExpire() {
        try {
           ApprovalExpire approvalExpire = systemSetService.getApprovalExpire();
            return ResultMsg.ok(approvalExpire);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    //region 准入控制
    @ApiOperation(value = "获取准入控制的配置信息")
    @GetMapping(value = "/accessControl")
    public ResultMsg getAccessControlConfig() {
        try {
            return ResultMsg.ok(systemSetService.getAccessControlConfig());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "更新准入控制的配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "是否开启准入控制", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "mac", value = "网关mac地址", dataType = "String", paramType = "query"),
    })
    @PostMapping(value = "/accessControl", produces = "application/json")
    public ResultMsg updateAccessControlConfig(@RequestBody AccessControl config) {
        try {
            systemSetService.updateAccessControlConfig(config);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }
    //endregion

    //region USBKey
    @ApiOperation(value = "分页获取USBKey列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "searchStr", value = "过滤条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String", paramType = "query"),
    })
    @GetMapping(value = "/ukey")
    public ResultMsg ukey(UkeyParams params) {
        try {
            IPage<Ukey> page = ukeyService.getUkeyListInPage(params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "保存一条新的USBKey到数据库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "USBKey名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "USBKey标识", dataType = "String", paramType = "query"),
    })
    @PostMapping(value = "/ukey", produces = "application/json")
    public ResultMsg ukey(@RequestBody Ukey ukey) {
        try {
            ukeyService.saveUkey(ukey);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取没有绑定的USBKey列表")
    @GetMapping(value = "/ukey/unused")
    public ResultMsg ukey() {
        try {
            List<Map<String, Object>> list = ukeyService.getUkeyUnused();
            return ResultMsg.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "删除USBKey")
    @DeleteMapping(value = "/ukey/{id}")
    public ResultMsg ukey(@PathVariable(value = "id") String id) {
        try {
            ukeyService.deleteUkeyById(id);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }
    //endregion

}
