package com.goldencis.osa.asset.excel.domain;

import com.goldencis.osa.asset.excel.annotation.Export;
import com.goldencis.osa.asset.excel.annotation.Import;
import lombok.Data;

/**
 * 发布规则导入导出
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-17 10:59
 **/
@Data
public class RuleItem {

    @Export(desc = "设备名称", order = 0)
    @Import(desc = "设备名称", order = 0)
    private String assetName;
    @Export(desc = "规则名称", order = 1)
    @Import(desc = "规则名称", order = 1)
    private String name;
    @Export(desc = "工具类型", order = 2)
    @Import(desc = "工具类型", order = 2)
    private String toolType;
    @Export(desc = "规则", order = 3)
    @Import(desc = "规则", order = 3)
    private String rule;

}
