package com.goldencis.osa.asset.params;

import com.goldencis.osa.common.entity.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备账号分页查询参数
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-10-26 13:55
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class AssetAccountParams extends Pagination {

    /**
     * 所属资产id
     */
    private Integer assetId;

}
