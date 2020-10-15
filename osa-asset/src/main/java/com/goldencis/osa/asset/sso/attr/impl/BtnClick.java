package com.goldencis.osa.asset.sso.attr.impl;

import com.goldencis.osa.asset.sso.SsoCmd;
import com.goldencis.osa.asset.sso.param.SsoAttrParamFactory;
import com.goldencis.osa.asset.sso.param.impl.NameParam;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-07 15:57
 **/
public class BtnClick extends AbstractSsoAttr<NameParam> {

    @Override
    protected NameParam parse(String param) throws Exception {
        return SsoAttrParamFactory.create(NameParam.class, param);
    }

    @Override
    public SsoCmd cmd() {
        return SsoCmd.btnClick;
    }
}
