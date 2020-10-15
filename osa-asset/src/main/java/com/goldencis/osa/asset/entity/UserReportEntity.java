package com.goldencis.osa.asset.entity;

import com.goldencis.osa.asset.excel.annotation.Export;

public class UserReportEntity {
    private String account_id;
    private String guid;
    private String asset_id;
    private String create_time;
    @Export(desc = "目标设备类型", order = 0)
    private String typename;
    @Export(desc = "目标设备名称", order = 1)
    private String assetname;
    @Export(desc = "目标设备IP", order = 2)
    private String ip;
    @Export(desc = "目标设备账号", order = 3)
    private String account;
    @Export(desc = "用户名", order = 4)
    private String username;
    @Export(desc = "姓名", order = 5)
    private String truename;
    @Export(desc = "用户组", order = 6)
    private String groupname;

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(String asset_id) {
        this.asset_id = asset_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getAssetname() {
        return assetname;
    }

    public void setAssetname(String assetname) {
        this.assetname = assetname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }
}
