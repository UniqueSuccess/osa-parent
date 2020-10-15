package com.goldencis.osa.asset.sso.replace.impl;

import com.goldencis.osa.asset.util.SsoConstants;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-26 15:54
 **/
public class PortPlaceholder extends AbstractPlaceholder {

    public PortPlaceholder(int port) {
        super(String.valueOf(port));
    }

    @Override
    public String placeholder() {
        return SsoConstants.PLACEHOLDER_PORT;
    }
}
