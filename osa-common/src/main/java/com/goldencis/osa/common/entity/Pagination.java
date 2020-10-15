package com.goldencis.osa.common.entity;

import lombok.Data;

/**
 * 分页查询参数
 */
@Data
public class Pagination {
    private Integer start;
    private Integer length;
    private String orderType;
    private String orderColumn;
    private String searchStr;
}
