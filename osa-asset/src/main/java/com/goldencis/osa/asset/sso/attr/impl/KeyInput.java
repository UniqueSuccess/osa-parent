package com.goldencis.osa.asset.sso.attr.impl;

import com.goldencis.osa.asset.sso.SsoCmd;
import com.goldencis.osa.asset.sso.param.SsoAttrParamFactory;
import com.goldencis.osa.asset.sso.param.impl.DataParam;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 14:05
 **/
public class KeyInput extends AbstractSsoAttr<DataParam> {

    @Override
    public SsoCmd cmd() {
        return SsoCmd.keyInput;
    }

    @Override
    protected DataParam parse(String param) throws Exception {
        return SsoAttrParamFactory.create(DataParam.class, param);
    }
}
