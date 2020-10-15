package com.goldencis.osa.asset.sso.param.impl;

import com.goldencis.osa.asset.sso.param.ISsoAttrParam;
import com.goldencis.osa.common.utils.StringUtils;
import lombok.Data;

/**
 * 会先通过提供的坐标将光标定位到编辑框位置，然后全选其中的内容，录入提供的信息
 *
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-29 17:07
 **/
@Data
public class InputParam implements ISsoAttrParam {

    private int x;
    private int y;
    private String data;

    @Override
    public void from(String param) throws Exception {
        if (StringUtils.isEmpty(param)) {
            throw new IllegalArgumentException(String.format("参数格式不正确,不能为null, %s", param));
        }
        int firstIndex = param.indexOf(",");
        String xStr = param.substring(0, firstIndex);
        int secondIndex = param.indexOf(",", firstIndex + 1);
        String yStr = param.substring(firstIndex + 1, secondIndex);
        int x = -1;
        int y = -1;
        try {
            x = Integer.parseInt(xStr);
            y = Integer.parseInt(yStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (x == -1 || y == -1) {
            throw new IllegalArgumentException(String.format("参数格式不正确, %s", param));
        }
        this.x = x;
        this.y = y;
        this.data = param.substring(secondIndex + 1);
    }

}
