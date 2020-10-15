package com.goldencis.osa.asset.sso.attr.impl;

import com.goldencis.osa.asset.sso.SsoCmd;
import com.goldencis.osa.asset.sso.param.SsoAttrParamFactory;
import com.goldencis.osa.asset.sso.param.impl.WebInputParam;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-17 11:53
 **/
public class WebInput extends AbstractSsoAttr<WebInputParam> {

    @Override
    protected WebInputParam parse(String param) throws Exception {
        return SsoAttrParamFactory.create(WebInputParam.class, param);
    }

    /**
     * 命令
     *
     * @return
     */
    @Override
    public SsoCmd cmd() {
        return SsoCmd.webInputText;
    }
}
