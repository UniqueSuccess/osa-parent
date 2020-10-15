package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 首页运维次数
 * </p>
 *
 * @author limingchao
 * @since 2018-10-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HomeAssetsWeek extends Model<HomeAssetsWeek>  {

    private static final long serialVersionUID = 1L;
    /**
     * 日期
     */
    private String date;

    /**
     * 主机数量【unix + windows】
     */
    private Integer hostNums;
    /**
     * 应用数量【c/s + b/s】
     */
    private Integer applicationNums;

    /**
     * unix数量
     */
    private Integer unixNums;
    /**
     * windows数量
     */
    private Integer windowsNums;
    /**
     * 网络设备数量
     */
    private Integer netNums;
    /**
     * 数据库数量
     */
    private Integer dbNums;
    /**
     * C/S 数量
     */
    private Integer csNums;
    /**
     * B/S 数量
     */
    private Integer bsNums;

    @Override
    protected Serializable pkVal() {
        return 1;
    }

    public HomeAssetsWeek() {
    }

    public HomeAssetsWeek(String date, Integer unixNums, Integer windowsNums, Integer netNums, Integer dbNums, Integer csNums, Integer bsNums) {
        this.date = date;
        this.unixNums = unixNums;
        this.windowsNums = windowsNums;
        this.netNums = netNums;
        this.dbNums = dbNums;
        this.csNums = csNums;
        this.bsNums = bsNums;
    }
}
