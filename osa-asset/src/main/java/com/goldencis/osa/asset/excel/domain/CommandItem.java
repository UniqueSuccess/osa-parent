package com.goldencis.osa.asset.excel.domain;

import com.goldencis.osa.asset.excel.annotation.Export;
import lombok.Data;

@Data
public class CommandItem {
    @Export(desc = "命令", order = 1)
    private String input;
    @Export(desc = "类型", order = 0)
    private String status;
    @Export(desc = "时间", order = 9)
    private String timestamp;
    @Export(desc = "目标资源名称", order = 3)
    private String asset;
    @Export(desc = "目标资源类型", order = 2)
    private String typename;
    @Export(desc = "目标资源IP", order = 4)
    private String ip;
    @Export(desc = "目标资源账号", order = 5)
    private String account;
    private String assetId;
    @Export(desc = "用户名", order = 6)
    private String username;
    @Export(desc = "姓名", order = 7)
    private String truename;
    @Export(desc = "用户组", order = 8)
    private String groupname;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
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

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
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
