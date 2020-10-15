package com.goldencis.osa.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 全局配置类
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-10-31 14:40
 **/
@Component
@ConfigurationProperties(prefix = "goldencis.osa")
@PropertySource(value = "classpath:application.yaml")
public class Config {

    /**
     * 文件导出路径
     */
    private String exportPath;
    private String host;
    private String clinetDirpath;
    private String clinetFilename;
    private String osabinPath;
    private String unpackageFilename;
    private String ifconfigUtilsLoadpath;
    private String eth;

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }

    public String getExportPath() {
        return exportPath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getClinetDirpath() {
        return clinetDirpath;
    }

    public void setClinetDirpath(String clinetDirpath) {
        this.clinetDirpath = clinetDirpath;
    }

    public String getClinetFilename() {
        return clinetFilename;
    }

    public void setClinetFilename(String clinetFilename) {
        this.clinetFilename = clinetFilename;
    }

    public String getOsabinPath() {
        return osabinPath;
    }

    public void setOsabinPath(String osabinPath) {
        this.osabinPath = osabinPath;
    }

    public String getUnpackageFilename() {
        return unpackageFilename;
    }

    public void setUnpackageFilename(String unpackageFilename) {
        this.unpackageFilename = unpackageFilename;
    }

    public String getIfconfigUtilsLoadpath() {
        return ifconfigUtilsLoadpath;
    }

    public void setIfconfigUtilsLoadpath(String ifconfigUtilsLoadpath) {
        this.ifconfigUtilsLoadpath = ifconfigUtilsLoadpath;
    }

    public String getEth() {
        return eth;
    }

    public void setEth(String eth) {
        this.eth = eth;
    }
}
