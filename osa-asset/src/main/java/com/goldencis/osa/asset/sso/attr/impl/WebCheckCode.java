package com.goldencis.osa.asset.sso.attr.impl;

import com.goldencis.osa.asset.sso.SsoCmd;
import com.goldencis.osa.asset.sso.param.SsoAttrParamFactory;
import com.goldencis.osa.asset.sso.param.impl.WebLabelParam;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-17 13:42
 **/
public class WebCheckCode extends AbstractSsoAttr<WebLabelParam> {
    @Override
    protected WebLabelParam parse(String param) throws Exception {
        return SsoAttrParamFactory.create(WebLabelParam.class, param);
    }

    /**
     * 命令
     *
     * @return
     */
    @Override
    public SsoCmd cmd() {
        return SsoCmd.webCheckCode;
    }
}
