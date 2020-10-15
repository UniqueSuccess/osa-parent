package com.goldencis.osa.asset.entity;

import com.goldencis.osa.asset.excel.annotation.Export;

public class ResourceEntity {
    private String accountId;
    private String guid;
    private String assetId;
    private String createTime;
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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
