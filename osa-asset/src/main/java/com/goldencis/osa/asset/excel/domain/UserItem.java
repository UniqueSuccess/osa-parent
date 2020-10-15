package com.goldencis.osa.asset.excel.domain;

import com.goldencis.osa.asset.excel.GrantType;
import com.goldencis.osa.asset.excel.annotation.Export;
import com.goldencis.osa.asset.excel.annotation.Import;
import lombok.Data;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-10 16:44
 **/
@Data
public class UserItem {
    /**
     * 用户名
     */
    @Export(desc = "用户名", order = 0)
    @Import(desc = "用户名", order = 0)
    private String username;
    /**
     * 真实姓名
     */
    @Export(desc = "姓名", order = 1)
    @Import(desc = "姓名", order = 1)
    private String name;
    @Export(desc = "部门", order = 2)
    @Import(desc = "部门", order = 2)
    private String userGroupName;
    @Export(desc = "已授权设备组", order = 3)
    @Import(desc = "已授权设备组", order = 3, nullable = true)
    private String assetGroupName;
    @Export(desc = "已授权设备", order = 4)
    @Import(desc = "已授权设备", order = 4, nullable = true)
    private String assetName;
    @Export(desc = "设备IP", order = 5)
    @Import(desc = "设备IP", order = 5, nullable = true)
    private String assetIp;
    @Export(desc = "授权账号", order = 6)
    @Import(desc = "授权账号", order = 6, nullable = true)
    private String account;
    @Export(desc = "账号权限", order = 7)
    @Import(desc = "账号权限", order = 7, nullable = true)
    private String trusteeship;
    /**
     * 授权类型
     */
    private GrantType grantType;

}
