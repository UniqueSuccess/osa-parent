package com.goldencis.osa.asset.sso.param.impl;

import com.goldencis.osa.asset.sso.param.ISsoAttrParam;
import lombok.Data;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 13:56
 **/
@Data
public class TimeParam implements ISsoAttrParam {
    /**
     * 时间,单位(毫秒)
     */
    private long msec;

    @Override
    public void from(String param) throws Exception {
        this.msec = Long.parseLong(param);
    }
}
