package com.goldencis.osa.asset.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.entity.Assetgroup;
import com.goldencis.osa.asset.service.IAssetAccountService;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.IAssetgroupService;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.entity.ResultTree;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.core.aop.OsaSystemLog;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.core.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备组模块,前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-10-24
 */
@Api(tags = "设备组管理")
@RestController
@RequestMapping("/assetgroup")
public class AssetgroupController {

    @Autowired
    private IAssetService assetService;
    @Autowired
    private IAssetgroupService assetgroupService;
    @Autowired
    private IAssetAccountService assetAccountService;

    @ApiOperation(value = "根据父类的设备组的id，查找对应设备组以及子类设备组")
    @ApiImplicitParam(name = "id", value = "设备组id", required = true, paramType = "query", dataType = "Integer")
    @GetMapping(value = "/getAssetgroupListByPid")
    public Object getAssetgroupListByPid(Integer id) {
        if (SecurityUtils.isAdministrator()) {
            return getTreeForAdmin(id);
        } else {
            return getTreeForNormal(id);
        }
    }

    /**
     * 管理员或超级管理员的设备树,全部的
     *
     * @param id
     * @return
     */
    private Object getTreeForAdmin(Integer id) {
        try {
            List<Assetgroup> list;
            //如果不传id，则直接查询全部，不需要设置查询条件
            if (id == null) {
                list = assetgroupService.list(null);
            } else {
                //根据父类的设备组的id，查找对应设备组以及子类设备组集合
                list = assetgroupService.getAssetgroupListByPid(id);
            }
            List<ResultTree> collect = new ArrayList<>(list.size());
            if (!ListUtils.isEmpty(list)) {
                collect = list.stream().map(item -> {
                    ResultTree resultTree = item.formatTree();
                    if (Objects.isNull(resultTree.getPid())) {
                        resultTree.setExpand(true);
                    }
                    return resultTree;
                }).collect(Collectors.toList());
            }
            return collect;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    private Object getTreeForNormal(Integer id) {
        User user = SecurityUtils.getCurrentUser();
        if (Objects.isNull(user)) {
            return ResultMsg.False("获取当前登录用户失败");
        }
        // 系统自带操作员比较特殊,他要看到所有的设备组
        if (ConstantsDto.USER_OPERATOR_ID.equals(user.getGuid())) {
            return getTreeForAdmin(id);
        }
        List<Integer> list = assetgroupService.getGroupIdsByOperator(user.getGuid());
        List<Assetgroup> groupList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            groupList = assetgroupService.list(new QueryWrapper<Assetgroup>().in("id", list));
        }
        List<ResultTree> resultTrees = assetgroupService.formatTreePermissionGroupList(groupList);
        return resultTrees;
    }

    @ApiOperation(value = "根据设备的id，查找对应设备组列表，关联设备组的checked=true")
    @ApiImplicitParam(name = "id", value = "设备id", required = true, paramType = "query", dataType = "Integer")
    @GetMapping(value = "/getAssetgroupListByAssetId")
    public Object getAssetgroupListByAssetId(Integer id) {
        try {
            List<Assetgroup> list;
            if (id == null) {
                //如果不传id，则直接查询全部，不需要设置查询条件
                list = assetgroupService.list(null);
            } else {
                //根据设备的id，查找对应设备组列表
                list = assetgroupService.getAssetgroupListByAssetId(id);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "获取设备组分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer"),
            @ApiImplicitParam(name = "searchStr", value = "查询条件", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String")
    })
    @GetMapping(value = "/getAssetgroupInPage")
    public ResultMsg getAssetgroupInPage(@RequestParam Map<String, String> params) {
        try {
            //获取分页参数
            IPage<Assetgroup> page = QueryUtils.paresParams2Page(params);

            //解析参数，封装查询包装类
            QueryWrapper<Assetgroup> wrapper = assetgroupService.parseParams2QueryWapper(params);
            //分页查询
            assetgroupService.getAssetgroupInPage(page, wrapper);

            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "新增设备组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", dataType = "String"),
            @ApiImplicitParam(name = "pid", value = "上级id", dataType = "Integer"),
            @ApiImplicitParam(name = "remark", value = "备注", dataType = "String"),
            @ApiImplicitParam(name = "treePath", value = "路径", dataType = "String"),
            @ApiImplicitParam(name = "level", value = "节点层级", dataType = "Integer")
    })
    @PostMapping(value = "/assetgroup")
    @OsaSystemLog(module = "新建设备组", template = "设备组名称：%s", args = "0.name", type = LogType.SYSTEM_ADD)
    public ResultMsg save(Assetgroup assetgroup) {
        try {
            //检查设备组名称是否重复
            boolean flag = assetgroupService.checkAssetgroupNameDuplicate(assetgroup);

            if (!flag) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "设备组名称重复！");
            }

            //添加新的设备组
            assetgroupService.saveAssetgroup(assetgroup);

            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "编辑设备组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "name", value = "名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pid", value = "上级id", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "remark", value = "备注", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "treePath", value = "路径", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "level", value = "节点层级", paramType = "query", dataType = "Integer")
    })
    @PutMapping(value = "/assetgroup")
    @OsaSystemLog(module = "编辑设备组",  template = "设备组名称：%s", args = "0.name",type = LogType.SYSTEM_UPDATE)
    public ResultMsg edit(Assetgroup assetgroup) {
        try {
            //检查设备组名称是否重复
            boolean flag = assetgroupService.checkAssetgroupNameDuplicate(assetgroup);

            if (!flag) {
                return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "设备组名称重复！");
            }

            if (ConstantsDto.SUPER_GROUP.equals(assetgroup.getId())) {
                if (!StringUtils.isEmpty(assetgroup.getPid())) {
                    return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "该设备组为顶级节点，无法变更！");
                }
            } else {
                if (StringUtils.isEmpty(assetgroup.getPid()) || assetgroup.getPid().equals(assetgroup.getId())) {
                    return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "设备组不能选自己作为父节点！");
                }

                Assetgroup pGroup = assetgroupService.getOne(new QueryWrapper<Assetgroup>().eq("id", assetgroup.getPid()));
                if (pGroup == null) {
                    return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "不存在的上级设备组！");
                }

                if (pGroup.getTreePath().contains(assetgroup.getTreePath() + assetgroup.getId() + ConstantsDto.SEPARATOR_COMMA)) {
                    //用户组不能选子节点作为父节点
                    return ResultMsg.build(ConstantsDto.RESPONSE_FALSE, "用户组不能选子节点作为父节点！");
                }
            }

            //编辑设备组
            assetgroupService.editAssetgroup(assetgroup);

            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "删除设备组")
    @ApiImplicitParam(name = "id", value = "id", paramType = "path", dataType = "Integer")
    @DeleteMapping(value = "/assetgroup/{id}")
    public ResultMsg delete(@PathVariable(value = "id") Integer id) {
        try {
            if (ConstantsDto.SUPER_GROUP.equals(id)) {
                return ResultMsg.False("不能删除默认设备组!");
            }
            int status = assetgroupService.deleteAssetgroup(id);
            ResultMsg ok = ResultMsg.ok();
            ok.setResultMsg(status == -1 ? "提交审批成功" : "删除成功");
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation("批量删除设备组")
    @ApiImplicitParam(name = "ids", value = "设备组id,多个用,隔开", required = true, paramType = "query", dataType = "String")
    @OsaSystemLog(module = "批量删除设备组")
    @DeleteMapping("/assetgroup/delete")
    public ResultMsg deleteBatch(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return ResultMsg.error("设备组id不能为空");
        }
        try {
            // 获取设备组id集合
            List<Integer> list = Arrays.stream(ids.split(ConstantsDto.SEPARATOR_COMMA)).map(Integer::parseInt).collect(Collectors.toList());
            int successCount = assetgroupService.deleteBatch(list);
            if (successCount == 0) {
                return ResultMsg.False("删除失败");
            }
            String msg;
            if (successCount == list.size()) {
                msg = "删除成功";
            } else {
                msg = "部分删除成功";
            }
            return ResultMsg.build(ResultMsg.RESPONSE_TRUE, msg);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ModelAttribute
    public void getModel(@RequestParam(value = "id", required = false) Integer id, Map<String, Object> map) {
        if (id != null) {
            Assetgroup assetgroup = assetgroupService.getById(id);
            map.put("assetgroup", assetgroup);
        }
    }

    @ApiOperation(value = "根据设备组id获取组内所有设备的账号信息")
    @ApiImplicitParam(name = "id", value = "设备组id", paramType = "path", dataType = "Integer")
    @GetMapping(value = "/{id}/account")
    public ResultMsg getAccountListByAssetGroupId(@PathVariable Integer id) {
        if (Objects.isNull(id)) {
            return ResultMsg.error("设备组id不能为空");
        }
        try {
            IPage<AssetAccount> page = assetAccountService.getAccountListByAssetGroupId(id);
            return ResultMsg.page(page);
        } catch (Exception e) {
            return ResultMsg.error(e.getMessage());
        }
    }
}
