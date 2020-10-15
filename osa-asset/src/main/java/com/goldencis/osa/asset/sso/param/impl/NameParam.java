package com.goldencis.osa.asset.sso.param.impl;

import com.goldencis.osa.asset.sso.param.ISsoAttrParam;
import lombok.Data;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-07 15:56
 **/
@Data
public class NameParam implements ISsoAttrParam {

    private String name;

    @Override
    public void from(String param) {
        this.name = param;
    }
}
