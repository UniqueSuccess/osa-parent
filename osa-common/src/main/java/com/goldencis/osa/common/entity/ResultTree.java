package com.goldencis.osa.common.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by limingchao on 2018/11/1.
 */
@Data
public class ResultTree implements Serializable {

    private String id;

    private String name;

    private Integer level;

    @JsonProperty(value = "pId")
    private String pid;

    //树节点类型
    private Integer type;

    //节点图标
    private String icon;

    boolean checked = false;
    /**
     * 是否展开子节点
     */
    private boolean expand = false;

    /**
     * true 禁掉 checkbox  和 不能select
     */
    private boolean disabled = false;
    /**
     * true 禁掉checkbox不可选
     */
    private boolean ignore = false;

    public ResultTree() {
    }

    public ResultTree(String id, String name, Integer level, String pid, boolean checked, boolean disabled, boolean expand) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.pid = pid;
        this.checked = checked;
        this.disabled = disabled;
        this.expand = expand;
    }

    public ResultTree(String id, String name, Integer level, String pid, Integer type, String icon, boolean checked, boolean disabled, boolean expand) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.pid = pid;
        this.type = type;
        this.icon = icon;
        this.checked = checked;
        this.disabled = disabled;
        this.expand = expand;
    }

    public void lockCheckState(boolean state) {
        this.setChecked(state);
        this.setDisabled(state);
        this.setIgnore(state);
    }
}
