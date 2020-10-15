package com.goldencis.osa.core.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.core.entity.Department;
import com.goldencis.osa.core.service.IDepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部门信息表 前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-10-17
 */
@Api(tags = "部门管理")
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private IDepartmentService departmentService;

    @ApiOperation(value = "获取当前登录用户权限下的部门集合")
    @GetMapping(value = "/getDepartmentListByLoginUser")
    public ResultMsg getDepartmentListByLoginUser() {
        try {
            //获取当前登录用户权限下的部门集合
            List<Department> departmentList = departmentService.getDepartmentListByLoginUser();

            return ResultMsg.ok(departmentList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "根据部门id，查询部门及其子类部门的集合")
    @GetMapping(value = "/getDepartmentListByPid")
    public Object getDepartmentListByPid(Integer id) {
        try {
            //根据部门id，查询部门及其子类部门的集合
            List<Department> departmentList = departmentService.getDepartmentListByPid(id);

//            return MAPPER.writeValueAsString(departmentList);
            return departmentList;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @PostMapping(value = "/department")
    public ResultMsg save(Department department) {
        try {
            //新增部门
            departmentService.saveDepartment(department);

            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }

    @ModelAttribute
    public void getModel(@RequestParam(value = "id", required = false) Integer id, Map<String, Object> map) {
        if (id != null) {
            Department department = departmentService.getById(id);
            map.put("department", department);
        }
    }
}
