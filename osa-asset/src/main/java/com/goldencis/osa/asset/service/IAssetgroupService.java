package com.goldencis.osa.asset.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.asset.entity.Assetgroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.common.entity.ResultTree;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-24
 */
public interface IAssetgroupService extends IService<Assetgroup> {

    /**
     * 根据父类的设备组的id，查找对应设备组以及子类设备组集合
     * @param id 父类的设备组的id
     * @return 对应设备组以及子类设备组集合
     */
    List<Assetgroup> getAssetgroupListByPid(Integer id);

    /**
     * 将请求中的查询参数转化为包装类
     * @param params 请求中的参数Map
     * @return 查询的包装类
     */
    QueryWrapper<Assetgroup> parseParams2QueryWapper(Map<String, String> params);

    /**
     * 分页查询设备组
     * @param page 分页对象
     * @param wrapper 查询条件包装类
     */
    void getAssetgroupInPage(IPage<Assetgroup> page, QueryWrapper<Assetgroup> wrapper);

    /**
     * 统计指定id的设备组以及其子设备组中，设备的数量。
     * @param id 设备组id
     * @return 设备数量
     */
    int countAssetInGroups(Integer id);

    /**
     * 检查设备组名称是否重复
     * @param assetgroup 设备组对象
     * @return 是否可用,可用返回true
     */
    boolean checkAssetgroupNameDuplicate(Assetgroup assetgroup);

    /**
     * 根据名称查询设备组
     * @param name 成名
     * @return 设备组对象
     */
    Assetgroup findAssetgroupByName(String name);

    /**
     * 添加新的设备组
     * @param assetgroup 设备组
     */
    void saveAssetgroup(Assetgroup assetgroup);

    /**
     * 编辑设备组
     * @param assetgroup 设备组
     */
    void editAssetgroup(Assetgroup assetgroup);

    /**
     * 根据id删除设备组
     * @param id
     */
    int deleteAssetgroup(Integer id);

    /**
     * 批量删除用户组
     * @param list 用户组id的集合
     * @return 删除成功的数量
     */
    int deleteBatch(List<Integer> list);

    /**
     * 根据设备的id，查找对应设备组列表，关联设备组的checked=true
     * @param id 设备的id
     * @return 设备组列表，关联设备组的checked=true
     */
    List<Assetgroup> getAssetgroupListByAssetId(Integer id);

    /**
     * 根据设备组id获取 父级设备组名字
     * @param assetgroupId 设备组id
     * @return 父级设备组名字
     */
    String getPnameByAssetgroupId(Integer assetgroupId);

    /**
     * 查询操作员对应的管理的所有设备组的id集合
     * @param guid 操作员guid
     * @return
     */
    List<Integer> getGroupIdsByOperator(String guid);

    List<ResultTree> formatTreePermissionGroupList(List<Assetgroup> groupList);
}
