package com.goldencis.osa.asset.domain;

import lombok.Data;

/**
 * 资源数量(首页展示用)
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-03 15:09
 **/
@Data
public class AssetCount {
    /**
     * 设备类型
     */
    private String typeName;
    /**
     * 数量
     */
    private int count;

}
