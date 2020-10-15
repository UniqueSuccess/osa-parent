package com.goldencis.osa.job.domain;

import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.common.export.annotation.Export;
import lombok.Data;

/**
 * 修改密码的结果实体类
 * CP:change password
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-22 14:45
 **/
@Data
public class CPResult {

    @Export(order = 0, header = "设备IP")
    private String ip;
    @Export(order = 1, header = "设备名称")
    private String name;
    @Export(order = 2, header = "账号")
    private String account;
    /**
     * 修改后的密码信息
     */
    @Export(order = 3, header = "密码")
    private String password;

    public CPResult(Asset asset, AssetAccount account, String password) {
        this.ip = asset.getIp();
        this.name = asset.getName();
        this.account = account.getUsername();
        this.password = password;
    }
}
