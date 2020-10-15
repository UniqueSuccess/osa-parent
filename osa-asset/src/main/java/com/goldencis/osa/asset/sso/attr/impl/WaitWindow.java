package com.goldencis.osa.asset.sso.attr.impl;

import com.goldencis.osa.asset.sso.SsoCmd;
import com.goldencis.osa.asset.sso.param.SsoAttrParamFactory;
import com.goldencis.osa.asset.sso.param.impl.WindowParam;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 14:09
 **/
public class WaitWindow extends AbstractSsoAttr<WindowParam> {

    @Override
    public SsoCmd cmd() {
        return SsoCmd.waitWindow;
    }

    @Override
    protected WindowParam parse(String param) throws Exception {
        return SsoAttrParamFactory.create(WindowParam.class, param);
    }
}
