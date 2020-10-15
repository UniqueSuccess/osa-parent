package com.goldencis.osa.system.domain;

import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.Assetgroup;
import com.goldencis.osa.core.entity.User;
import lombok.Data;

import java.util.List;

/**
 * 系统权限详情
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-18 13:56
 **/
@Data
public class SystemAccountInfo {

    /**
     * 设备权限
     */
    private AssetPermission assetPermission;

    /**
     * 审计权限
     */
    private AuditPermission auditPermission;

    @Data
    public static class AuditPermission {

        private List<UserItem> userList;

        @Data
        public static class UserItem {
            private String id;
            private String name;
            private String username;
            private String roleName;
            private Boolean checked;

            public UserItem(User user, Boolean checked) {
                this.id = user.getGuid();
                this.name = user.getName();
                this.username = user.getUsername();
                this.roleName = user.getRoleName();
                this.checked = checked;
            }
        }

    }

    @Data
    public static class AssetPermission {

        private List<AssetItem> assetList;
        private List<AssetGroupItem> assetGroupList;

        @Data
        public static class AssetItem {
            private int id;
            private String icon;
            private String name;
            private String ip;
            private String groupName;

            public AssetItem(Asset a) {
                this.id = a.getId();
                this.icon = a.getIcon();
                this.name = a.getName();
                this.ip = a.getIp();
                this.groupName = a.getGroupName();
            }
        }

        @Data
        public static class  AssetGroupItem {
            private int id;
            private String name;
            private String pName;
            private int assetCount;

            public AssetGroupItem() {
            }

            public AssetGroupItem(Assetgroup a) {
                this.id = a.getId();
                this.name = a.getName();
                this.pName = a.getPname();
                this.assetCount = a.getAssetCount();
            }
        }
    }

}
