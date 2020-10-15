package com.goldencis.osa.system.domain;

import lombok.Data;

/**
 * 审计权限列表条目
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-20 15:19
 **/
@Data
public class AuditDetailInfo {

    private String id;
    private String name;
    private Boolean checked;
    private Boolean disabled;

    public AuditDetailInfo(SystemAccountInfo.AuditPermission.UserItem item) {
        this.id = item.getId();
        this.name = item.getName();
        this.checked = item.getChecked();
        this.disabled = item.getChecked();
    }
}
