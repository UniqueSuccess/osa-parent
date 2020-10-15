package com.goldencis.osa.core.service;

import com.goldencis.osa.core.entity.Department;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部门信息表 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-17
 */
public interface IDepartmentService extends IService<Department> {

    /**
     * 获取当前登录用户权限下的部门集合
     * @return 部门集合
     */
    List<Department> getDepartmentListByLoginUser();

    /**
     * 获取当前登录用户权限下的部门，自己部门为级联属性
     * @return 部门
     */
    Department getDepartmentByLoginUser();

    /**
     * 根据部门id，查询部门及其子类部门的集合
     * @param id 部门id
     * @return 该部门及其子类部门的集合
     */
    List<Department> getDepartmentListByPid(Integer id);

    /**
     * 根据部门id，查询部门，并将子类存放在级联属性中
     * @param id 部门id
     * @return 部门对象，级联属性中包含子级部门
     */
    Department getDepartmentById(Integer id);

    /**
     * 新增部门
     * @param department 部门对象
     */
    void saveDepartment(Department department);

    /**
     * 获取全部部门的Map，key为部门的id，value为部门对象
     * @return 全部部门的Map
     */
    Map<Integer,Department> getDepartmentMap();

    /**
     * 获取全部部门
     * @return 全部部门的集合
     */
    List<Department> getAllDepartmentList();
}
