package com.goldencis.osa.asset.sso.attr;

import com.goldencis.osa.asset.sso.SsoCmd;
import com.goldencis.osa.asset.sso.param.ISsoAttrParam;

/**
 * sso规则属性(规则中的每一条命令)
 */
public interface ISsoAttr<P extends ISsoAttrParam> {

    /**
     * 命令
     * @return
     */
    SsoCmd cmd();

    /**
     * 参数
     * @return
     */
    P param();

    void from(String param) throws Exception;

}
