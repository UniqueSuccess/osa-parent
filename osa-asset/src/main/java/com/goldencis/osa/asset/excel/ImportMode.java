package com.goldencis.osa.asset.excel;

/**
 * 导入模式
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-10 16:32
 **/
public enum ImportMode {
    /**
     * 严格模式,数据出现异常,直接停止导入,抛出异常
     */
    STRICT,
    /**
     * 标准模式,将异常数据跳过,继续导入
     */
    STANDARD

}
