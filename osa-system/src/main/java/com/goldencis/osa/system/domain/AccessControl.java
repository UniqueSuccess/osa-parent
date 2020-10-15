package com.goldencis.osa.system.domain;

import lombok.Data;

import java.util.List;

/**
 * 准入控制
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-24 14:12
 **/
@Data
public class AccessControl {

    /**
     * 标记是否开启
     */
    private Boolean status = false;

    /**
     * 网关mac地址
     */
    private String mac;

    /**
     * ip黑名单
     */
    private List<IpBlack> ips;

    /**
     * 端口黑名单
     */
    private List<PortBlack> ports;


    @Data
    public static class IpBlack {

        /**
         * ip或者ip段
         */
        private String ip;
        /**
         * 备注信息
         */
        private String remark;
    }

    @Data
    public static class PortBlack {

        /**
         * 开始端口
         */
        private Integer start;
        /**
         * 结束端口
         */
        private Integer end;

    }

}
