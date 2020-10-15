package com.goldencis.osa.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.core.entity.Department;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.mapper.DepartmentMapper;
import com.goldencis.osa.core.service.IDepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.core.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门信息表 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-17
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<Department> getDepartmentListByLoginUser() {
        //获取当前用户
        User user = SecurityUtils.getCurrentUser();

        //根据登录用户所在部门id查询部门及子级部门的集合
        return this.getDepartmentListByPid(user.getDepartment().getId());
    }

    @Override
    public Department getDepartmentByLoginUser() {
        //获取当前用户
        User user = SecurityUtils.getCurrentUser();
        return this.getDepartmentById(user.getDepartment().getId());
    }

    @Override
    public List<Department> getDepartmentListByPid(Integer id) {
        //根据父级部门的id，查询该部门及子级部门的集合
        List<Department> departmentList = departmentMapper.getDepartmentListByPid(id);
        return departmentList;
    }

    @Override
    public Department getDepartmentById(Integer id) {
        List<Department> departmentList = this.getAllDepartmentList();
        Map<Integer, Department> deptMap = this.getDepartmentMap();
        departmentList.forEach(department -> {
            if (department.getPid() != null) {
                Department pDept = deptMap.get(department.getPid());
                if (pDept.getChildDepartments() == null) {
                    pDept.setChildDepartments(new ArrayList<>());
                }
                pDept.getChildDepartments().add(department);
            }
        });
        return deptMap.get(id);
    }

    @Override
    public void saveDepartment(Department department) {
        //获取父类部门
        Department pdept = departmentMapper.selectById(department.getPid());
        //补充部门树
        department.setTreePath(pdept.getTreePath() + pdept.getId() + ",");
        departmentMapper.insert(department);
    }

    @Override
    @Cacheable(value = "department", key = "deptMap")
    public Map<Integer, Department> getDepartmentMap() {
        List<Department> departmentList = this.getAllDepartmentList();
        return departmentList.stream().collect(Collectors.toMap(Department::getId, department -> department));
    }

    @Override
    @Cacheable(value = "department", key = "deptList")
    public List<Department> getAllDepartmentList() {
        return departmentMapper.selectList(new QueryWrapper<>());
    }
}
