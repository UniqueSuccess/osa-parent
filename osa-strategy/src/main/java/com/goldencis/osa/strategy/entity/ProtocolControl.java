package com.goldencis.osa.strategy.entity;

import com.goldencis.osa.core.entity.Dictionary;
import lombok.Data;

/**
 * 协议控制
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-20 16:30
 **/
@Data
public class ProtocolControl {

    /**
     * 源数据类型
     */
    private String type;

    /**
     * 字典名
     */
    private String name;

    /**
     * 排序(字典值)
     */
    private Integer value;

    /**
     * 是否勾选
     */
    private boolean checked = false;

    public ProtocolControl() {
    }

    public ProtocolControl(Dictionary dictionary) {
        this.type = dictionary.getType();
        this.name = dictionary.getName();
        this.value = dictionary.getValue();
        this.checked = false;
    }

    public ProtocolControl(Dictionary dictionary, boolean checked) {
        this(dictionary);
        this.checked = checked;
    }
}
