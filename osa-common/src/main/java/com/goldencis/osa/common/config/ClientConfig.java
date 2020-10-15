package com.goldencis.osa.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-24 14:02
 **/
@Component
@ConfigurationProperties(prefix = "goldencis.client")
@PropertySource(value = "classpath:application.yaml")
public class ClientConfig {

    /**
     * 客户端地址
     */
    private String winServer2003Url;
    private String winServerUrl;
    private String linuxServerUrl;
    private String packageuploadPath;
    private String packageuploadFileName;
    private String xpPackageuploadFileName;
    private String bridgeuploadFileName;
    private String clientupdateuploadPath;
    private String clientupdateuploadFileName;
    private String bridgeupdateuploadPath;
    private String bridgeupdateuploadFileName;

    public String getWinServer2003Url() {
        return winServer2003Url;
    }

    public void setWinServer2003Url(String winServer2003Url) {
        this.winServer2003Url = winServer2003Url;
    }

    public String getWinServerUrl() {
        return winServerUrl;
    }

    public void setWinServerUrl(String winServerUrl) {
        this.winServerUrl = winServerUrl;
    }

    public String getLinuxServerUrl() {
        return linuxServerUrl;
    }

    public void setLinuxServerUrl(String linuxServerUrl) {
        this.linuxServerUrl = linuxServerUrl;
    }

    public String getPackageuploadPath() {
        return packageuploadPath;
    }

    public void setPackageuploadPath(String packageuploadPath) {
        this.packageuploadPath = packageuploadPath;
    }

    public String getPackageuploadFileName() {
        return packageuploadFileName;
    }

    public void setPackageuploadFileName(String packageuploadFileName) {
        this.packageuploadFileName = packageuploadFileName;
    }

    public String getXpPackageuploadFileName() {
        return xpPackageuploadFileName;
    }

    public void setXpPackageuploadFileName(String xpPackageuploadFileName) {
        this.xpPackageuploadFileName = xpPackageuploadFileName;
    }

    public String getBridgeuploadFileName() {
        return bridgeuploadFileName;
    }

    public void setBridgeuploadFileName(String bridgeuploadFileName) {
        this.bridgeuploadFileName = bridgeuploadFileName;
    }

    public String getClientupdateuploadPath() {
        return clientupdateuploadPath;
    }

    public void setClientupdateuploadPath(String clientupdateuploadPath) {
        this.clientupdateuploadPath = clientupdateuploadPath;
    }

    public String getClientupdateuploadFileName() {
        return clientupdateuploadFileName;
    }

    public void setClientupdateuploadFileName(String clientupdateuploadFileName) {
        this.clientupdateuploadFileName = clientupdateuploadFileName;
    }

    public String getBridgeupdateuploadPath() {
        return bridgeupdateuploadPath;
    }

    public void setBridgeupdateuploadPath(String bridgeupdateuploadPath) {
        this.bridgeupdateuploadPath = bridgeupdateuploadPath;
    }

    public String getBridgeupdateuploadFileName() {
        return bridgeupdateuploadFileName;
    }

    public void setBridgeupdateuploadFileName(String bridgeupdateuploadFileName) {
        this.bridgeupdateuploadFileName = bridgeupdateuploadFileName;
    }
}
