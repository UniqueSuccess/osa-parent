package com.goldencis.osa.core.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源登录限制
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-27 19:44
 **/
@Data
public class Assets {

    /**
     * ip黑名单
     */
    private List<String> ip = new ArrayList<>();

}
