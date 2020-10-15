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
public class HomeAssets extends Model<HomeAssets>  {

    private static final long serialVersionUID = 1L;

    /**
     * 设备数量
     */
    private Integer assetNums;
    /**
     * 设备类型
     */
    private Integer assetType;
    /**
     * 设备id
     */
    private Integer assetId;
    /**
     * 设备名称
     */
    private String assetName;
    /**
     * 设备ip
     */
    private String assetIp;

    @Override
    protected Serializable pkVal() {
        return 1;
    }

}
