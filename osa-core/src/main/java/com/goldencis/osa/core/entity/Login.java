package com.goldencis.osa.core.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录黑名单
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-27 18:02
 **/
@Data
public class Login {

    /**
     * 用户名黑名单
     */
    private List<String> username = new ArrayList<>();
    /**
     * ip黑名单
     */
    private List<String> ip = new ArrayList<>();

}
