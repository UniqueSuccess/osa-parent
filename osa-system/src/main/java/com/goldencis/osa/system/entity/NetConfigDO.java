package com.goldencis.osa.system.entity;

public class NetConfigDO {

    private String ethname;

    private String addr;

    private String mask;

    private String gateway;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getEthname() {
        return ethname;
    }

    public void setEthname(String ethname) {
        this.ethname = ethname;
    }

}