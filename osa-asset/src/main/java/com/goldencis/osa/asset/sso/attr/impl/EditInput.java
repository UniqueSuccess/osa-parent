package com.goldencis.osa.asset.sso.attr.impl;

import com.goldencis.osa.asset.sso.SsoCmd;
import com.goldencis.osa.asset.sso.param.SsoAttrParamFactory;
import com.goldencis.osa.asset.sso.param.impl.InputParam;

/**
 * 编辑框录入内容；
 * 会先通过提供的坐标将光标定位到编辑框位置，然后全选其中的内容，录入提供的信息
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-29 17:07
 **/
public class EditInput extends AbstractSsoAttr<InputParam> {

    @Override
    public SsoCmd cmd() {
        return SsoCmd.editInput;
    }

    @Override
    protected InputParam parse(String param) throws Exception {
        return SsoAttrParamFactory.create(InputParam.class, param);
    }
}
