package com.goldencis.osa.asset.sso.param.impl;

import com.goldencis.osa.asset.sso.param.ISsoAttrParam;
import lombok.Data;

/**
 * 输入命令所需要携带的参数
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 14:04
 **/
@Data
public class DataParam implements ISsoAttrParam {
    /**
     * 输入的内容
     */
    private String data;

    @Override
    public void from(String param) {
        this.data = param;
    }
}
