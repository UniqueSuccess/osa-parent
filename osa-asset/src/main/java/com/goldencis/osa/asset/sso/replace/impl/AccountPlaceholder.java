package com.goldencis.osa.asset.sso.replace.impl;

import com.goldencis.osa.asset.util.SsoConstants;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-26 15:55
 **/
public class AccountPlaceholder extends AbstractPlaceholder {

    public AccountPlaceholder(String content) {
        super(content);
    }

    @Override
    public String placeholder() {
        return SsoConstants.PLACEHOLDER_ACCOUNT;
    }
}
