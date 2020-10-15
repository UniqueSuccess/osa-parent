package com.goldencis.osa.core.mapper;

import com.goldencis.osa.core.entity.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 部门信息表 Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-10-17
 */
public interface DepartmentMapper extends BaseMapper<Department> {

    /**
     * 根据登录用户的guid查询关联的部门集合
     * @param guid 用户的guid
     * @return 部门集合
     */
    List<Department> getDepartmentListByLoginUserGuid(@Param(value = "guid") String guid);

    /**
     * 根据父级部门的id，查询该部门及子级部门的集合
     * @param id 父级部门的id
     * @return 该部门及子级部门的集合
     */
    List<Department> getDepartmentListByPid(@Param(value = "id") Integer id);
}
