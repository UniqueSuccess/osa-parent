package com.goldencis.osa.asset.sso.param;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-07 14:23
 **/
public class SsoAttrParamFactory {

    @SuppressWarnings("unchecked")
    public static <T extends ISsoAttrParam> T create(Class<T> clazz, String param) throws Exception {
        T t = clazz.newInstance();
        t.from(param);
        return t;
    }

}
