package com.goldencis.osa.asset.sso.attr.impl;

import com.goldencis.osa.asset.sso.attr.ISsoAttr;
import com.goldencis.osa.asset.sso.param.ISsoAttrParam;
import lombok.Data;

import java.util.Objects;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-30 13:50
 **/
@Data
public abstract class AbstractSsoAttr<P extends ISsoAttrParam> implements ISsoAttr<P> {

    protected String cmd = cmd().name();
    private P param;

    @Override
    public P param() {
        return this.param;
    }

    protected AbstractSsoAttr() {
    }

    @Override
    public void from(String param) throws Exception {
        if (!allowNullRule() && Objects.isNull(param)) {
            throw new NullPointerException(String.format("规则 %s 不允许空的参数", cmd));
        }
        this.param = parse(param);
    }

    /**
     * 是否允许规则字符串为null,默认不允许
     * @return 如果允许，返回true；否则，返回false
     */
    protected boolean allowNullRule() {
        return false;
    }

    protected abstract P parse(String param) throws Exception;

}
