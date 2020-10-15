package com.goldencis.osa.asset.mapper;

import com.goldencis.osa.asset.entity.Assetgroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-10-24
 */
public interface AssetgroupMapper extends BaseMapper<Assetgroup> {

    /**
     * 根据父类的设备组的id，查找对应设备组以及子类设备组集合
     * @param id 父类的设备组的id
     * @return 对应设备组以及子类设备组集合
     */
    List<Assetgroup> getAssetgroupListByPid(Integer id);

    /**
     * 替换TreePath路径
     * @param oldTreePath 需要修改的路径部分
     * @param newTreePath 替换的新的路径部分
     * @param oldSelfPath 自身的旧路径
     */
    void updateTreePath(@Param(value = "oldTreePath") String oldTreePath, @Param(value = "newTreePath") String newTreePath, @Param(value = "oldSelfPath") String oldSelfPath);

    /**
     * 根据设备组id获取 父级设备组名字
     * @param assetgroupId 设备组id
     * @return 父级设备组名字
     */
    String getPnameByAssetgroupId(Integer assetgroupId);

    void deleteAssetGroupPermissionById(@Param(value = "assetGroupId") Integer assetGroupId);

    /**
     * 查询操作员对应的管理的所有设备组的id集合
     * @param guid
     * @return
     */
    List<Integer> getGroupIdsByOperator(String guid);

    void insertAndGetPrimaryKey(Assetgroup assetgroup);

    /**
     * 插入一条系统账号与设备的权限关联
     * @param guid 用户guid
     * @param id 设备id
     */
    void insertUserAssetGroupPermission(@Param(value = "guid") String guid,
                                        @Param(value = "id") Integer id);

    List<Integer> getGroupIdByUserPermission(@Param(value = "guid") String guid);
}
