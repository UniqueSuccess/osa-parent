package com.goldencis.osa.asset.sso.param.impl;

import com.goldencis.osa.asset.sso.param.ISsoAttrParam;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 启动web浏览器的参数
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-17 11:23
 **/
@Data
public class WebStartParam implements ISsoAttrParam {

    /**
     * 链接
     */
    private String url;
    /**
     * 标题
     */
    private String title;

    /**
     * 接受的参数格式为:
     *      http://www.baidu.com,百度一下,你就知道
     * 以第一个英文逗号(,)作为分隔符,前边为地址(url),后边为标题
     * @param param
     * @throws Exception
     */
    @Override
    public void from(String param) throws Exception {
        if (StringUtils.isEmpty(param)) {
            throw new Exception("参数不能为空");
        }
        int index = param.indexOf(",");
        if (index == -1) {
            throw new Exception(String.format("参数不正确,至少包含一个逗号: %s", param));
        }
        this.url = param.substring(0, index);
        this.title = param.substring(index + 1);
    }
}
