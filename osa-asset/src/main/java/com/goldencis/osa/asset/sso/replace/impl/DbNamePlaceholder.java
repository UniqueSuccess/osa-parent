package com.goldencis.osa.asset.sso.replace.impl;

import com.goldencis.osa.asset.util.SsoConstants;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-07 19:05
 **/
public class DbNamePlaceholder extends AbstractPlaceholder {

    public DbNamePlaceholder(String dbName) {
        super(dbName);
    }

    /**
     * 占位符
     *
     * @return
     */
    @Override
    public String placeholder() {
        return SsoConstants.PLACEHOLDER_DB_NAME;
    }
}
