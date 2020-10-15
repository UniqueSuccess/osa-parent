package com.goldencis.osa.asset.params;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goldencis.osa.common.entity.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 资产列表的分页查询参数
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-10-26 15:26
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class AssetParams extends Pagination {

    /**
     * 设备类型,多个类型用,隔开
     */
    private String assetType;

    /**
     * 设备组id,多个id用,隔开
     */
    private String groupId;
    /**
     * 用户勾选的设备id 用,隔开
     */
    private String assetIds;
    /**
     * 设备类型集合
     */
    @JsonIgnore
    private List<Integer> assetTypeList;
    /**
     * 前端选择的设备组集合
     */
    @JsonIgnore
    private List<Integer> groupIdList;
    /**
     * 用户勾选的设备id列表
     */
    private List<Integer> assetIdList;
    /**
     * 用户拥有权限的设备组集合
     */
    @JsonIgnore
    private List<Integer> permissionGroupIdList;
    /**
     * 用户拥有权限的设备集合
     */
    @JsonIgnore
    private List<Integer> permissionAssetIdList;
}
