package com.goldencis.osa.asset.sso.replace.impl;

import com.goldencis.osa.asset.util.SsoConstants;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-07 19:40
 **/
public class UrlPlaceholder extends AbstractPlaceholder {

    public UrlPlaceholder(String url) {
        super(url);
    }

    /**
     * 占位符
     *
     * @return
     */
    @Override
    public String placeholder() {
        return SsoConstants.PLACEHOLDER_URL;
    }
}
