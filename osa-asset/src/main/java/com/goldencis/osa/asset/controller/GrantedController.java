package com.goldencis.osa.asset.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.asset.domain.GrantDetail;
import com.goldencis.osa.asset.domain.GrantDetailParam;
import com.goldencis.osa.asset.domain.GrantWay;
import com.goldencis.osa.asset.entity.*;
import com.goldencis.osa.asset.service.IAssetAccountService;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.IAssetgroupService;
import com.goldencis.osa.asset.service.IGrantedService;
import com.goldencis.osa.common.config.Config;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.entity.ResultTree;
import com.goldencis.osa.common.export.ExcelExporter;
import com.goldencis.osa.common.utils.FileDownLoad;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.core.aop.OsaSystemLog;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.core.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 设备授权表 前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-11-05
 */
@Api(tags = "授权管理")
@RestController
@RequestMapping("/granted")
public class GrantedController {

    private final Logger logger = LoggerFactory.getLogger(GrantedController.class);

    @Autowired
    private IAssetService assetService;
    @Autowired
    private IAssetAccountService assetAccountService;
    @Autowired
    private IAssetgroupService assetgroupService;
    @Autowired
    private IGrantedService grantedService;
    @Autowired
    private IUserService userService;
    @Autowired
    private Config config;

    @ApiOperation(value = "新增授权设备账号树： 获取包含设备组、设备和用户的结构树，传guid参数时，认为是回显，将用户权限的树节点check=true")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "guid", value = "用户guid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "isGroup", value = "是否是设备组", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "isUserGroup", value = "是否是用户组", dataType = "boolean", paramType = "query")
    })
    @GetMapping(value = "/getGrantedTree")
    public Object getGrantedTree(String guid, boolean isGroup, boolean isUserGroup) {
        try {
            User operator = SecurityUtils.getCurrentUser();
            boolean isDefaultAdmin = SecurityUtils.isDefaultAdministrator();
            List<Assetgroup> groupList = null;
            List<Asset> assetList = null;

            //判断是否是管理员用户，是则回显完整的树，否则回显用户对应权限的树
            if (isDefaultAdmin) {
                groupList = assetgroupService.list(null);
                if (!isGroup) {
                    //获取设备集合（过滤除 应用程序发布器之外的设备、账号），级联获取对应的设备账户集合
                    assetList = assetService.listWithAssetAccount(null);
                }
            } else {
                //查询操作员对应的管理的所有设备组的id集合
                List<Integer> groupIds = grantedService.getGroupIdsByOperator(operator.getGuid());
                if (!ListUtils.isEmpty(groupIds)) {
                    //查询操作员对应的管理的所有设备组的集合
                    groupList = assetgroupService.list(new QueryWrapper<Assetgroup>().in("id", groupIds));
                }

                if (!isGroup) {
                    //查询操作员对应的管理的所有设备的id集合
                    List<Integer> assetIds = grantedService.getAssetIdsByOperator(operator.getGuid());
                    if (!ListUtils.isEmpty(assetIds)) {
                        //获取设备集合（过滤除 应用程序发布器之外的设备、账号），级联获取对应的设备账户集合
                        assetList = assetService.listWithAssetAccount(assetIds);
                    }
                }
            }

            if (ListUtils.isEmpty(groupList)) {
                groupList = new ArrayList<>();
            }

            if (ListUtils.isEmpty(assetList)) {
                assetList = new ArrayList<>();
            }

            //如果是授权设备的情况，则需要将设备组下的设备，插入并加入设备集合，构成完整的树
            if (!isGroup && !ListUtils.isEmpty(groupList)) {
                assetService.findAssetsInGroupToList(groupList, assetList);
            }

            //将设备组、设备、设备账户拼接为结构树
            List<ResultTree> trees = assetService.formatTreeWithAccountAndAssetAndGroup(groupList, assetList);

            //如果是授权设备组的情况，则需要将有子节点的设备组下添加虚拟组
            if (isGroup && !ListUtils.isEmpty(trees)) {
                List<ResultTree> nihilityTree = assetService.initNihilityList(trees);
                trees.addAll(nihilityTree);
            }

            //回显 设备账号树
            if (!StringUtils.isEmpty(guid)) {
                //设置操作员已选权限
                assetService.setSelectedByGuid(guid, trees, isGroup, isUserGroup);
            }
            return trees;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "授权详情树： 获取包含设备组、设备的结构树，传用户guid参数")
    @GetMapping(value = "/getGrantedTreeByGuid")
    public Object getGrantedTreeByGuid() {
        try {
            //获取设备集合（过滤除 应用程序发布器之外的设备）
            List<Asset> assetList = assetService.listAssetsNotPublis();
            List<Assetgroup> groupList = assetgroupService.list(null);

            //将设备组、设备拼接为结构树【没有设备账号】
            List<ResultTree> trees = assetService.formatTreeWithAccountAndAssetAndGroup(groupList, assetList);
            return trees;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "新增设备组授权树：获取包含设备组结构树，传用户guid参数")
    @ApiImplicitParam(name = "userGuid", value = "用户guid", dataType = "String", paramType = "query")
    @GetMapping(value = "/getGrantedAssetgroupTreeByGuid")
    public Object getGrantedAssetgroupTreeByGuid(String userGuid) {
        try {
            List<ResultTree> collect = assetService.getGrantedAssetgroupTreeByGuid(userGuid);
            return collect;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "为用户(组)添加设备(组)权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userJson", value = "用户（组），userType 1用户，2用户组，拼接格式：{\"userType\": 1,\"userId\": \"d65d6cfc-8463-405c-b5fc-9298df98fd19\",\"usergroupId\": \"\"}", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "grantedJson", value = "赋权设备（组），grantedType（ 1设备组，2设备，3账户），assetgroups（多个设备组id 英文,分割）， 拼接格式(授权设备账号为例)：{\"grantedType\": 3,\"assetgroups\": \"\",\"assetaccounts\": \"account-29,account-30\"}", dataType = "String", paramType = "query"),
    })
    @PostMapping(value = "/grantedAsset4User")
    @OsaSystemLog(module = "添加授权", template = "授权详细：%ret", ret = "data", type = LogType.GRANTED_ADD)
    public ResultMsg grantedAsset4User(String userJson, String grantedJson) {
        try {

            if (StringUtils.isEmpty(userJson)) {
                return ResultMsg.False("授权对象为空");
            }
            if (StringUtils.isEmpty(grantedJson)) {
                return ResultMsg.False("授权设备为空");
            }
            //为用户(组)添加设备(组)权限
            String grantedContent = grantedService.grantedAsset4User(userJson, grantedJson);
            return ResultMsg.ok(grantedContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation("分页获取用户授权设备账号列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer"),
            @ApiImplicitParam(name = "searchStr", value = "查询", dataType = "String"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String"),
            @ApiImplicitParam(name = "userGuid", value = "用户guid（userGuid、usergroupId只能有一个有值）", dataType = "String"),
            @ApiImplicitParam(name = "usergroupId", value = "用户组id（userGuid、usergroupId只能有一个有值）", dataType = "String")
    })
    @GetMapping(value = "/getAssestAccountsInPage")
    public ResultMsg getAssestAccountsInPage(@RequestParam Map<String, String> params) {
        try {
            //分页查询
            IPage<Granted> page = grantedService.getAssestAccountsInPage(params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation("分页获取用户授权设备组账号列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer"),
            @ApiImplicitParam(name = "searchStr", value = "查询", dataType = "String"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String"),
            @ApiImplicitParam(name = "userGuid", value = "用户guid（userGuid、usergroupId只能有一个有值）", dataType = "String"),
            @ApiImplicitParam(name = "usergroupId", value = "用户组id（userGuid、usergroupId只能有一个有值）", dataType = "String")
    })
    @GetMapping(value = "/getAssestgroupsInPage")
    public ResultMsg getAssestgroupsInPage(@RequestParam Map<String, String> params) {
        try {
            //分页查询
            IPage<Granted> page = grantedService.getAssestgroupsInPage(params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation("分页获取用户授权 所有 设备账号列表（单点登录之后列表）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "length", value = "每页条数", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "searchStr", value = "查询", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataTypeClass = String.class),
            @ApiImplicitParam(name = "assetTypeIds", value = "设备类型id(多个id用,拼接)", dataTypeClass = String.class),
            @ApiImplicitParam(name = "assetgroupId", value = "设备组id", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "assetId", value = "设备id", dataTypeClass = Integer.class),
    })
    @GetMapping(value = "/getGrantedsByCurrentUser4SSOInPage")
    public ResultMsg getGrantedsByCurrentUser4SSOInPage(@RequestParam Map<String, String> params) {
        try {
            //分页查询
            IPage<GrantedSignUser> page = grantedService.getGrantedsByCurrentUser4SSOInPage(params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "通过授权id删除授权信息")
    @ApiImplicitParam(name = "id", value = "授权id", required = true, paramType = "path", dataType = "Integer")
    @DeleteMapping(value = "/deleteById/{id}")
    @OsaSystemLog(module = "删除授权", template = "授权详细：%ret", ret = "data", type = LogType.GRANTED_DELETE)
    public ResultMsg deleteGrantedById(@PathVariable("id") Integer id) {

        try {
            if (Objects.isNull(id)) {
                return ResultMsg.False("授权id为空");
            }
            String grantedContent = grantedService.deleteGrantedById(id);
            return ResultMsg.ok(grantedContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "通过设备id删除设备，批量删除授权信息")
    @ApiImplicitParam(name = "id", value = "设备id", required = true, paramType = "path", dataType = "Integer")
    @DeleteMapping(value = "/deleteGrantedAssetByAssetId/{id}")
    @OsaSystemLog(module = "删除设备", template = "设备信息：%ret", ret = "data", type = LogType.GRANTED_DELETE)
    public ResultMsg deleteGrantedAssetByAssetId(@PathVariable("id") Integer id) {
        try {
            if (Objects.isNull(id)) {
                return ResultMsg.False("设备id不能为空");
            }
           String assetName =  grantedService.deleteGrantedAssetByAssetId(id);
            return ResultMsg.ok(assetName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "撤销授权，通过授权id撤销授权")
    @ApiImplicitParam(name = "id", value = "设备id", required = true, paramType = "path", dataTypeClass = java.lang.Integer.class)
    @PutMapping(value = "/revokeGrantedById/{id}")
    @OsaSystemLog(module = "撤销授权", template = "授权信息：%ret", ret = "data", type = LogType.GRANTED_REVOKE)
    public ResultMsg revokeGrantedById(@PathVariable("id") Integer id) {
        try {
            if (Objects.isNull(id)) {
                return ResultMsg.False("授权id不能为空");
            }
            String revokeContent =  grantedService.revokeGrantedById(id);
            return ResultMsg.ok(revokeContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation("分页获取用户授权详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "searchStr", value = "过滤条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userGuid", value = "用户guid", dataType = "String", paramType = "query"),
    })
    @GetMapping(value = "/detailInPage")
    public ResultMsg getGrantedDetailByUserId(@ModelAttribute GrantDetailParam param) {
        if (Objects.isNull(param)) {
            return ResultMsg.error("参数不能为空");
        }
        if (org.springframework.util.StringUtils.isEmpty(param.getUserGuid())) {
            return ResultMsg.error("用户id不能为空");
        }
        if (Objects.isNull(param.getStart())) {
            param.setStart(0);
        }
        if (Objects.isNull(param.getLength())) {
            param.setLength(20);
        }
        try {
            IPage<GrantDetail> page = grantedService.getGrantDetailInPage(param);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation("导出用户授权详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "searchStr", value = "过滤条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userGuid", value = "用户guid", dataType = "String", paramType = "query"),
    })
    @GetMapping("/detail/export")
    public void exportGrantDetail(@ModelAttribute GrantDetailParam param,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        User user = userService.getById(param.getUserGuid());
        if (Objects.isNull(user)) {
            logger.warn("用户不存在: {},不能导出授权详情", param.getUserGuid());
            return;
        }
        param.setStart(0);
        param.setLength(Integer.MAX_VALUE);
        IPage<GrantDetail> page = grantedService.getGrantDetailInPage(param);
        List<GrantDetail> list = page.getRecords();
        list.forEach(item -> {
            item.setGrantWayStr(formatGrantWay(item.getGrantWays()));
        });
        ExcelExporter<GrantDetail> excelExporter = new ExcelExporter.Builder<GrantDetail>()
                .setTitle("授权详情")
                .setData(list)
                .setClazz(GrantDetail.class)
                .setOutputName(user.getName() + "_" + user.getUsername() + "_授权详情")
                .setOutputPath(config.getExportPath())
                .build();
        File file = excelExporter.export();
        new FileDownLoad().download(response, request, file.getAbsolutePath());
    }

    private String formatGrantWay(List<GrantWay> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            GrantWay item = list.get(i);
            sb.append(item.getGrantFrom())
                    .append("->")
                    .append(item.getGrantTo());
            if (i != list.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

}
