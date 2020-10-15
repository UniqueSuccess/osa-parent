package com.goldencis.osa.asset.excel.domain;

import com.goldencis.osa.asset.excel.annotation.Export;
import com.goldencis.osa.asset.excel.annotation.Import;
import lombok.Data;

/**
 * 设备账号导入导出
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-17 11:37
 **/
@Data
public class AccountItem {

    @Export(desc = "设备名称", order = 0)
    @Import(desc = "设备名称", order = 0)
    private String assetName;
    @Export(desc = "账号", order = 1)
    @Import(desc = "账号", order = 1)
    private String account;
    @Export(desc = "密码", order = 2)
    @Import(desc = "密码", order = 2)
    private String password;
    @Export(desc = "托管", order = 3)
    @Import(desc = "托管", order = 3)
    private String trusteeship;

}
