package com.goldencis.osa.asset.sso.attr.impl;

import com.goldencis.osa.asset.sso.SsoCmd;
import com.goldencis.osa.asset.sso.param.SsoAttrParamFactory;
import com.goldencis.osa.asset.sso.param.impl.TimeParam;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 13:57
 **/
public class Sleep extends AbstractSsoAttr<TimeParam> {

    @Override
    public SsoCmd cmd() {
        return SsoCmd.sleep;
    }

    @Override
    protected TimeParam parse(String param) throws Exception {
        return SsoAttrParamFactory.create(TimeParam.class, param);
    }
}
