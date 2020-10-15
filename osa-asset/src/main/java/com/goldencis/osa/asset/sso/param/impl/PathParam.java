package com.goldencis.osa.asset.sso.param.impl;

import com.goldencis.osa.asset.sso.param.ISsoAttrParam;
import lombok.Data;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 11:43
 **/
@Data
public class PathParam implements ISsoAttrParam {
    /**
     * 程序执行路径
     */
    private String path;
    /**
     * 程序执行参数
     */
    private String param;

    public PathParam() {
    }

    private PathParam(String path) {
        this.path = path;
    }

    private PathParam(String path, String param) {
        this(path);
        this.param = param;
    }

    @Override
    public void from(String param) throws Exception {
        this.path = param;
    }
}
