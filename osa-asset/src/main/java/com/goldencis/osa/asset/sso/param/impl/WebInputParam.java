package com.goldencis.osa.asset.sso.param.impl;

import com.goldencis.osa.asset.sso.param.ISsoAttrParam;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * web页面,在输入框中输入内容
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-17 11:44
 **/
@Data
public class WebInputParam implements ISsoAttrParam {

    /**
     * 输入框标签的id
     */
    private String name;
    /**
     * 输入的内容
     */
    private String data;

    /**
     * 参数格式为:
     *      fname,lougd
     * 以第一个英文逗号(,)作为分隔符,前边为输入框标签id,后边为待输入的内容
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
        this.name = param.substring(0, index);
        this.data = param.substring(index + 1);
    }
}
