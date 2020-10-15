package com.goldencis.osa.asset.domain;

import com.goldencis.osa.common.entity.Pagination;
import lombok.Data;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-21 11:09
 **/
@Data
public class GrantDetailParam extends Pagination {

    private String userGuid;

}
