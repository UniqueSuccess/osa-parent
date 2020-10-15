package com.goldencis.osa.asset.sso.attr.impl;

import com.goldencis.osa.asset.sso.SsoCmd;
import com.goldencis.osa.asset.sso.param.SsoAttrParamFactory;
import com.goldencis.osa.asset.sso.param.impl.RectParam;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 14:18
 **/
public class MouseRClick extends AbstractSsoAttr<RectParam> {

    @Override
    public SsoCmd cmd() {
        return SsoCmd.mouseRClick;
    }

    @Override
    protected RectParam parse(String param) throws Exception {
        return SsoAttrParamFactory.create(RectParam.class, param);
    }
}
