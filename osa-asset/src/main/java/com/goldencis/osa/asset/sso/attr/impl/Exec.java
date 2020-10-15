package com.goldencis.osa.asset.sso.attr.impl;

import com.goldencis.osa.asset.sso.SsoCmd;
import com.goldencis.osa.asset.sso.param.SsoAttrParamFactory;
import com.goldencis.osa.asset.sso.param.impl.PathParam;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 11:45
 **/
public class Exec extends AbstractSsoAttr<PathParam> {

    @Override
    public SsoCmd cmd() {
        return SsoCmd.exec;
    }

    @Override
    protected PathParam parse(String param) throws Exception {
        return SsoAttrParamFactory.create(PathParam.class, param);
    }
}
