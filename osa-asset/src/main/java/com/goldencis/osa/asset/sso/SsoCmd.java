package com.goldencis.osa.asset.sso;

import com.goldencis.osa.asset.sso.attr.ISsoAttr;
import com.goldencis.osa.asset.sso.attr.impl.*;

/**
 * 单点登录规则中的命令列表
 */
public enum SsoCmd {
    /**
     * 执行程序
     */
    exec(Exec.class),
    /**
     * 锁定
     */
    waitWindow(WaitWindow.class),
    /**
     * 鼠标左键点击
     */
    mouseLClick(MouseLClick.class),
    /**
     * 鼠标右键点击
     */
    mouseRClick(MouseRClick.class),
    /**
     * 等待
     */
    sleep(Sleep.class),
    /**
     * 编辑框全选
     */
    editCheckAll(EditCheckAll.class),
    /**
     * 输入内容
     */
    keyInput(KeyInput.class),
    /**
     * 编辑框录入内容；
     * 会先通过提供的坐标将光标定位到编辑框位置，然后全选其中的内容，录入提供的信息
     */
    editInput(EditInput.class),
    /**
     * 通过按钮名称查找按钮,然后点击鼠标左键
     */
    btnClick(BtnClick.class),
    /**
     *
     */
    findTree(FindTree.class),
    /**
     * 启动web浏览器
     */
    webStartBrowser(WebStart.class),
    /**
     * web浏览器,在编辑框中输入内容
     */
    webInputText(WebInput.class),
    /**
     * web浏览器中输入验证码
     */
    webCheckCode(WebCheckCode.class),
    /**
     * web浏览器中点击按钮
     */
    webButClick(WebButClick.class);

    /**
     * 指定该命令由哪个对象进行解析
     */
    private Class<? extends ISsoAttr> clazz;

    SsoCmd(Class<? extends ISsoAttr> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends ISsoAttr> getClazz() {
        return clazz;
    }
}
