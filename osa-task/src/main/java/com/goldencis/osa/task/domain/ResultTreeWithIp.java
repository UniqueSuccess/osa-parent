package com.goldencis.osa.task.domain;

import com.goldencis.osa.common.entity.ResultTree;
import lombok.Data;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-22 09:48
 **/
@Data
public class ResultTreeWithIp extends ResultTree {

    private String ip;

    //节点第二个图标
    private String secondaryIcon;

    public ResultTreeWithIp() {
    }

    public ResultTreeWithIp(String id, String name, String ip, Integer level, String pid, boolean checked, boolean disabled, boolean expand) {
        super(id, name, level, pid, checked, disabled, expand);
        this.ip = ip;
    }

    public ResultTreeWithIp(String id, String name, String ip, Integer level, String pid, Integer type, String icon, String secondaryIcon, boolean checked, boolean disabled, boolean expand) {
        super(id, name, level, pid, type, icon, checked, disabled, expand);
        this.ip = ip;
        this.secondaryIcon = secondaryIcon;
    }
}
