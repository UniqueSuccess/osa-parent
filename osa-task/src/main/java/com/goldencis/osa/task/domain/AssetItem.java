package com.goldencis.osa.task.domain;

import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.util.AssetConstans;
import lombok.Data;

/**
 * 自动改密计划中添加资源时,使用的对象信息
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-21 14:00
 **/
@Data
public class AssetItem {
    private String id;
    private String icon;
    private String name;
    private String ip;
    private String groupName;

    public AssetItem(Asset a) {
        this.id = AssetConstans.PREFIX_ASSET + a.getId();
        this.icon = a.getIcon();
        this.name = a.getName();
        this.ip = a.getIp();
        this.groupName = a.getGroupName();
    }
}
