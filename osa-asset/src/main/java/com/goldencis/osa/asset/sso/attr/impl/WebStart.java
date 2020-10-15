package com.goldencis.osa.asset.sso.attr.impl;

import com.goldencis.osa.asset.sso.SsoCmd;
import com.goldencis.osa.asset.sso.param.SsoAttrParamFactory;
import com.goldencis.osa.asset.sso.param.impl.WebStartParam;

/**
 * B/S设备特有,启动浏览器
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-17 11:40
 **/
public class WebStart extends AbstractSsoAttr<WebStartParam> {

    @Override
    protected WebStartParam parse(String param) throws Exception {
        return SsoAttrParamFactory.create(WebStartParam.class, param);
    }

    /**
     * 命令
     *
     * @return
     */
    @Override
    public SsoCmd cmd() {
        return SsoCmd.webStartBrowser;
    }
}
