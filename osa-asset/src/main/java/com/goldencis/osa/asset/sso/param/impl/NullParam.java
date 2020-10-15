package com.goldencis.osa.asset.sso.param.impl;

import com.goldencis.osa.asset.sso.param.ISsoAttrParam;
import lombok.Data;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 14:32
 **/
@Data
public class NullParam implements ISsoAttrParam {

    private Object object;

    @Override
    public void from(String param) {
        this.object = param;
    }
}
