package com.goldencis.osa.session.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.service.IAssetAccountService;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.IAssetTypeService;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.session.entity.SsoConfiguration;
import com.goldencis.osa.session.service.ISsoConfigurationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 单点登录配置表 前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
@Api(tags = "单点登录配置模块")
@RestController
@RequestMapping("/ssoConfiguration")
public class SsoConfigurationController {

    @Autowired
    private IAssetService assetService;

    @Autowired
    private IAssetAccountService assetAccountService;

    @Autowired
    private ISsoConfigurationService ssoConfigurationService;

    @ApiOperation(value = "根据设备id和账户id查找当前用户的单点登录配置信息，如果该设备没有配置过，则返回默认配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "assetId", value = "设备id", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "accountId", value = "账户id", paramType = "query", dataType = "Integer")
    })
    @GetMapping(value = "/findSSOConfigurationByAssetIdAndAccountIdWithCurrentUser")
    public ResultMsg findSSOConfigurationByAssetIdAndAccountIdWithCurrentUser(Integer assetId, Integer accountId) {
        try {
            //校验参数
            if (assetId == null || accountId == null) {
                return ResultMsg.False("未传递正确的参数！");
            }

            //获取设备
            Asset asset = assetService.getById(assetId);
            if (asset == null) {
                return ResultMsg.False("未找到对应的设备！");
            }

            //获取账户
            AssetAccount assetAccount = assetAccountService.getById(accountId);
            if (assetAccount == null || !assetAccount.getAssetId().equals(assetId)) {
                return ResultMsg.False("未找到对应的设备账户！");
            }

            //根据设备和账户，查找对应的单点登录配置信息
            SsoConfiguration configuration = ssoConfigurationService.findSsoConfigurationByAssetAndAccount(asset, assetAccount);

            return ResultMsg.ok(configuration);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "保存当前登录用户指定设备和账户的ssh登录配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "assetId", value = "设备id", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "accountId", value = "账户id", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "configuration", value = "配置信息", paramType = "query", dataType = "String")
    })
    @PostMapping(value = "/saveConfigureSSO4CurrentUser")
    public ResultMsg saveConfigureSSO4CurrentUser(Integer assetId, Integer accountId, String configuration) {
        try {
            //校验参数
            if (assetId == null || accountId == null || StringUtils.isEmpty(configuration)) {
                return ResultMsg.False("未传递正确的参数！");
            }

            //获取设备
            Asset asset = assetService.getById(assetId);
            if (asset == null) {
                return ResultMsg.False("未找到对应的设备！");
            }

            //获取账户
            AssetAccount assetAccount = assetAccountService.getById(accountId);
            if (assetAccount == null || !assetAccount.getAssetId().equals(assetId)) {
                return ResultMsg.False("未找到对应的设备账户！");
            }

            //保存单点登录配置
            ssoConfigurationService.saveConfigureSSO(asset, assetAccount, configuration);

            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "批量保存当前登录用户指定设备和账户的ssh登录配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "batchInfo", value = "json对象的数组，内部对象有两个key，assetId和accountId", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "configuration", value = "配置信息", paramType = "query", dataType = "String")
    })
    @PostMapping(value = "/batchSaveConfigureSSO4CurrentUser")
    public ResultMsg batchSaveConfigureSSO4CurrentUser(String batchInfo, String configuration) {
        try {
            if (StringUtils.isEmpty(batchInfo)) {
                return ResultMsg.False("批量配置信息不能为空！");
            }

            //转化批量配置信息
            JSONArray infoArr = JSONArray.parseArray(batchInfo);
            if (infoArr == null || infoArr.size() == 0) {
                return ResultMsg.False("批量配置信息不能为空！");
            }

            //批量保存单点登录配置
            ssoConfigurationService.saveConfigureSSO(infoArr, configuration);

            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }
}
