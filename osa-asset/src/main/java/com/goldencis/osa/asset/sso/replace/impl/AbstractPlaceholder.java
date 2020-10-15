package com.goldencis.osa.asset.sso.replace.impl;

import com.goldencis.osa.asset.sso.replace.IRulePlaceholder;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-26 15:42
 **/
public abstract class AbstractPlaceholder implements IRulePlaceholder {

    private String content;

    public AbstractPlaceholder(String content) {
        this.content = content;
    }

    @Override
    public String content() {
        return content;
    }
}
