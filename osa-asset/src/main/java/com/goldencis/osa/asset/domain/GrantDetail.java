package com.goldencis.osa.asset.domain;

import com.goldencis.osa.common.export.annotation.Export;
import lombok.Data;

import java.util.List;

/**
 * 授权详情
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-21 09:07
 **/
@Data
public class GrantDetail {
    /**
     * 授权状态
     */
    @Export(order = 0, header = "状态")
    private String status;
    /**
     * 设备名称
     */
    @Export(order = 1, header = "设备名称")
    private String assetName;
    /**
     * 设备ip
     */
    @Export(order = 2, header = "设备IP")
    private String assetIp;
    /**
     * 账号
     */
    @Export(order = 3, header = "设备账号")
    private String account;
    /**
     * 设备类型
     */
    @Export(order = 4, header = "设备类型")
    private String assetType;
    /**
     * 设备组
     */
    @Export(order = 5, header = "设备组")
    private String groupName;
    /**
     * 授权来源
     */
    @Export(order = 6, header = "授权来源")
    private String grantWayStr;
    /**
     * 托管类型
     */
    @Export(order = 7, header = "账号权限")
    private String trusteeship;
    /**
     * 授权来源
     */
    private List<GrantWay> grantWays;
    /**
     * 用户guid
     */
    private String userGuid;

}
