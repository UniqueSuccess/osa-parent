package com.goldencis.osa.asset.sso.param.impl;

import com.goldencis.osa.asset.sso.param.ISsoAttrParam;
import com.goldencis.osa.common.utils.StringUtils;
import lombok.Data;

/**
 * 矩形参数,移动鼠标时,或者鼠标左右键点击时,需要的参数
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 14:19
 **/
@Data
public class RectParam implements ISsoAttrParam {

    private int x;
    private int y;

    @Override
    public void from(String param) throws Exception {
        if (StringUtils.isEmpty(param)) {
            throw new IllegalArgumentException(String.format("参数格式不正确,不能为null, %s", param));
        }
        String[] rect = param.split(",");
        if (rect.length != 2) {
            throw new IllegalArgumentException(String.format("参数格式不正确, %s", param));
        }
        int x = -1;
        int y = -1;
        try {
            x = Integer.parseInt(rect[0]);
            y = Integer.parseInt(rect[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (x == -1 || y == -1) {
            throw new IllegalArgumentException(String.format("参数格式不正确, %s", param));
        }
        this.x = x;
        this.y = y;
    }
}
