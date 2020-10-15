package com.goldencis.osa.asset.sso.replace.impl;

import com.goldencis.osa.asset.util.SsoConstants;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-26 15:41
 **/
public class NamePlaceholder extends AbstractPlaceholder {

    public NamePlaceholder(String name) {
        super(name);
    }

    @Override
    public String placeholder() {
        return SsoConstants.PLACEHOLDER_NAME;
    }
}
