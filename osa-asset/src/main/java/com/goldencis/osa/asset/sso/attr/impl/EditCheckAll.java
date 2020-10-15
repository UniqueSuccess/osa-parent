package com.goldencis.osa.asset.sso.attr.impl;

import com.goldencis.osa.asset.sso.SsoCmd;
import com.goldencis.osa.asset.sso.param.SsoAttrParamFactory;
import com.goldencis.osa.asset.sso.param.impl.NullParam;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 14:33
 **/
public class EditCheckAll extends AbstractSsoAttr<NullParam> {

    @Override
    public SsoCmd cmd() {
        return SsoCmd.editCheckAll;
    }

    @Override
    protected boolean allowNullRule() {
        return true;
    }

    @Override
    protected NullParam parse(String rule) throws Exception {
        return SsoAttrParamFactory.create(NullParam.class, rule);
    }

}
