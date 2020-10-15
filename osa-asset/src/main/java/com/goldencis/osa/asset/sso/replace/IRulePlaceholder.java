package com.goldencis.osa.asset.sso.replace;

/**
 * 规则占位符
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-26 15:29
 **/
public interface IRulePlaceholder {

    /**
     * 占位符
     * @return
     */
    String placeholder();

    /**
     * 需要替换的具体内容
     * @return
     */
    String content();

}
