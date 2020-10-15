package com.goldencis.osa.asset.sso.param.impl;

import com.goldencis.osa.asset.sso.param.ISsoAttrParam;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * web浏览器中,
 * 指定标签的参数
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-17 11:57
 **/
@Data
public class WebLabelParam implements ISsoAttrParam {
    /**
     * 标签id
     */
    private String name;

    @Override
    public void from(String param) throws Exception {
        if (StringUtils.isEmpty(param)) {
            throw new Exception("参数不能为空");
        }
        this.name = param;
    }
}
